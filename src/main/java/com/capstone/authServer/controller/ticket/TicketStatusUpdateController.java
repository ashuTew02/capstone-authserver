package com.capstone.authServer.controller.ticket;

import com.capstone.authServer.dto.event.payload.ticket.TicketUpdateStatusEventPayload;
import com.capstone.authServer.dto.event.ticket.TicketUpdateStatusEvent;
import com.capstone.authServer.dto.response.ApiResponse;
import com.capstone.authServer.kafka.producer.TicketUpdateStatusEventProducer;
import com.capstone.authServer.model.Finding;
import com.capstone.authServer.model.KafkaTopic;
import com.capstone.authServer.security.annotation.AllowedRoles;
import com.capstone.authServer.service.ElasticSearchService;
import com.capstone.authServer.service.JiraTicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tickets")
@CrossOrigin
public class TicketStatusUpdateController {

    private final JiraTicketService jiraTicketService;
    private final TicketUpdateStatusEventProducer producer;
    private final ElasticSearchService esService;

    public TicketStatusUpdateController(JiraTicketService jiraTicketService, TicketUpdateStatusEventProducer producer, ElasticSearchService esService) {
        this.esService = esService;
        this.jiraTicketService = jiraTicketService;
        this.producer = producer;
    }

    /**
     * Only job: change status from "To Do" to "Done"
     */
    @PutMapping("/{findingId}/{ticketId}/done")
    @AllowedRoles({"USER", "ADMIN", "SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<?>> markTicketAsDone(@PathVariable("ticketId") String ticketId, @PathVariable("findingId") String findingId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long tenantId = (Long) auth.getDetails();
        System.out.println("INSIDE TICKET STATUS UPDATE CONTROLLER");
        // jiraTicketService.transitionTicketToDone(tenantId, ticketId);
        Finding finding = esService.getFindingById(findingId, tenantId);
        TicketUpdateStatusEventPayload payload = new TicketUpdateStatusEventPayload(
            findingId,
            tenantId,
            KafkaTopic.BGJOBS_JFC,
            finding.getToolType(),
            ticketId
        );
        TicketUpdateStatusEvent event = new TicketUpdateStatusEvent(payload);
        producer.produce(event);

        return new ResponseEntity<>(
            ApiResponse.success(HttpStatus.OK.value(), 
                "Ticket status will be changed to Done for " + ticketId, 
                null),
            HttpStatus.OK
        );
    }
}
