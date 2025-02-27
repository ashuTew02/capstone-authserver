package com.capstone.authServer.service.runbook;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;

import com.capstone.authServer.dto.runbook.ConfigureActionsRequest;
import com.capstone.authServer.dto.runbook.ConfigureFiltersRequest;
import com.capstone.authServer.dto.runbook.ConfigureTriggersRequest;
import com.capstone.authServer.dto.runbook.CreateRunbookRequest;
import com.capstone.authServer.dto.runbook.TriggerDTO;
import com.capstone.authServer.model.runbook.Runbook;
import com.capstone.authServer.model.runbook.RunbookAction;
import com.capstone.authServer.model.runbook.RunbookFilter;
import com.capstone.authServer.model.runbook.RunbookTrigger;
import com.capstone.authServer.model.runbook.RunbookTriggerType;
import com.capstone.authServer.repository.runbook.RunbookActionRepository;
import com.capstone.authServer.repository.runbook.RunbookFilterRepository;
import com.capstone.authServer.repository.runbook.RunbookRepository;
import com.capstone.authServer.repository.runbook.RunbookTriggerRepository;

import jakarta.transaction.Transactional;



@Service
@Transactional
public class RunbookService {

    private final RunbookRepository runbookRepository;
    private final RunbookTriggerRepository triggerRepository;
    private final RunbookFilterRepository filterRepository;
    private final RunbookActionRepository actionRepository;

    public RunbookService(
        RunbookRepository runbookRepository,
        RunbookTriggerRepository triggerRepository,
        RunbookFilterRepository filterRepository,
        RunbookActionRepository actionRepository
    ) {
        this.runbookRepository = runbookRepository;
        this.triggerRepository = triggerRepository;
        this.filterRepository = filterRepository;
        this.actionRepository = actionRepository;
    }

    // -----------------
    // 1) CREATE / GET
    // -----------------
    public Runbook createRunbook(CreateRunbookRequest request, Long tenantId) {
        Runbook runbook = new Runbook();
        runbook.setTenantId(tenantId);
        runbook.setName(request.getName());
        runbook.setDescription(request.getDescription());
        return runbookRepository.save(runbook);
    }

    public Runbook getRunbookById(Long id, Long tenantId) {
        Runbook runbook = runbookRepository.findById(id).orElse(null);
        if (runbook == null || !runbook.getTenantId().equals(tenantId)) {
            return null;
        }
        return runbook;
    }

    public List<Runbook> getRunbooksForTenant(Long tenantId) {
        return runbookRepository.findByTenantId(tenantId);
    }

    // -----------------
    // 2) UPDATE STATUS
    // -----------------
    public List<String> checkRunbookStatus(Long runbookId, Long tenantId) {
        Runbook runbook = getRunbookById(runbookId, tenantId);
        if (runbook == null) return new ArrayList<>();

        List<String> status = new ArrayList<>();
        if (runbook.getTriggers() != null && !runbook.getTriggers().isEmpty()) {
            status.add("TRIGGER");
        }
        if (runbook.getFilters() != null && !runbook.getFilters().isEmpty()) {
            status.add("FILTER");
        }
        if (runbook.getActions() != null && !runbook.getActions().isEmpty()) {
            status.add("ACTION");
        }
        return status;
    }

    // -----------------
    // 3) TRIGGERS
    // -----------------
    public List<RunbookTrigger> configureTriggers(ConfigureTriggersRequest request, Long tenantId) {
        Runbook runbook = getRunbookById(request.getRunbookId(), tenantId);
        if (runbook == null) {
            return Collections.emptyList();
        }

        // 1) Remove old triggers
        // Copy them first to avoid ConcurrentModification
        List<RunbookTrigger> oldTriggers = new ArrayList<>(runbook.getTriggers());
        for (RunbookTrigger t : oldTriggers) {
            runbook.removeTrigger(t);
        }
        runbookRepository.save(runbook); 
        // old triggers are now orphan-removed

        // 2) Add new triggers
        for (TriggerDTO dto : request.getTriggers()) {
            RunbookTrigger trigger = new RunbookTrigger();
            trigger.setTriggerType(dto.getTriggerType());
            runbook.addTrigger(trigger);
        }
        runbookRepository.save(runbook); // persist the new triggers

        return runbook.getTriggers();
    }
    
    public List<RunbookTrigger> getRunbookTriggers(Long runbookId, Long tenantId) {
        Runbook runbook = getRunbookById(runbookId, tenantId);
        if (runbook == null) return new ArrayList<>();
        return runbook.getTriggers();
    }

    public List<String> getAllAvailableTriggers() {
        // Hardcoded or from enum
        List<String> triggers = new ArrayList<>();
        triggers.add("NEW_SCAN_INITIATE"); 
        // etc.
        return triggers;
    }

    // -----------------
    // 4) FILTERS
    // -----------------
    @Transactional
    public List<RunbookFilter> configureFilters(ConfigureFiltersRequest request, Long tenantId) {
        Runbook runbook = getRunbookById(request.getRunbookId(), tenantId);
        if (runbook == null) {
            return Collections.emptyList();
        }
    
        // 1) Remove old filters properly
        List<RunbookFilter> oldFilters = new ArrayList<>(runbook.getFilters());
        for (RunbookFilter f : oldFilters) {
            runbook.removeFilter(f); // sets f.runbook=null, removes from list
        }
        runbookRepository.save(runbook);
        // Now the old filters are orphan-removed
    
        // 2) Add new filters
        // Based on your code, you might only add one or multiple
        RunbookFilter newFilter = new RunbookFilter();
        newFilter.setState(request.getState());
        newFilter.setSeverity(request.getSeverity());
        runbook.addFilter(newFilter);
    
        runbookRepository.save(runbook);
        return runbook.getFilters();
    }


    public List<RunbookFilter> getRunbookFilters(Long runbookId, Long tenantId) {
        Runbook runbook = getRunbookById(runbookId, tenantId);
        if (runbook == null) return new ArrayList<>();
        return runbook.getFilters();
    }

    // -----------------
    // 5) ACTIONS
    // -----------------
    @Transactional
    public List<RunbookAction> configureActions(ConfigureActionsRequest request, Long tenantId) {
        Runbook runbook = getRunbookById(request.getRunbookId(), tenantId);
        if (runbook == null) {
            return Collections.emptyList();
        }

        // 1) Remove old actions
        List<RunbookAction> oldActions = new ArrayList<>(runbook.getActions());
        for (RunbookAction a : oldActions) {
            runbook.removeAction(a);
        }
        runbookRepository.save(runbook);

        // 2) Add new action(s)
        RunbookAction action = new RunbookAction();
        if (request.getUpdate() != null) {
            action.setToState(request.getUpdate().getTo());
        }
        boolean ticketCreate = request.getTicketCreate() != null && request.getTicketCreate();
        action.setTicketCreate(ticketCreate);
        runbook.addAction(action);

        runbookRepository.save(runbook);
        return runbook.getActions();
    }

    public List<RunbookAction> getRunbookActions(Long runbookId, Long tenantId) {
        Runbook runbook = getRunbookById(runbookId, tenantId);
        if (runbook == null) return new ArrayList<>();
        return runbook.getActions();
    }

    // -----------------
    // 6) Enable/Disable
    // -----------------
    public Runbook updateRunbookEnabled(Long runbookId, Long tenantId, boolean enabled) {
        Runbook runbook = getRunbookById(runbookId, tenantId);
        if (runbook == null) {
            return null;
        }
        runbook.setEnabled(enabled);
        return runbookRepository.save(runbook);
    }

    // -----------------
    // 7) Delete Runbook
    // -----------------
    public boolean deleteRunbook(Long runbookId, Long tenantId) {
        Runbook runbook = getRunbookById(runbookId, tenantId);
        if (runbook == null) {
            return false;
        }
        runbookRepository.delete(runbook);
        return true;
    }
}
