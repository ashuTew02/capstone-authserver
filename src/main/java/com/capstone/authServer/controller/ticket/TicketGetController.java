package com.capstone.authServer.controller.ticket;

import com.capstone.authServer.dto.TicketDTO;
import com.capstone.authServer.dto.response.ApiResponse;
import com.capstone.authServer.security.annotation.AllowedRoles;
import com.capstone.authServer.service.JiraTicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/tickets")
@CrossOrigin
public class TicketGetController {

    private final JiraTicketService jiraTicketService;

    public TicketGetController(JiraTicketService jiraTicketService) {
        this.jiraTicketService = jiraTicketService;
    }

    @GetMapping
    @AllowedRoles({"USER", "ADMIN", "SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<?>> getTicketsForTenant() {
        // Usually you'd either pass tenantId in path param or get from auth context
        // Let's assume we get it from the Authentication as we do elsewhere
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long tenantId = (Long) auth.getDetails();

        System.out.println("INSIDE GET tiCKETS CONTROLLER");
        List<TicketDTO> ticketList = jiraTicketService.getAllTenantTickets(tenantId);

        return new ResponseEntity<>(
            ApiResponse.success(HttpStatus.OK.value(), "Tickets fetched successfully.", ticketList),
            HttpStatus.OK
        );
    }
}
