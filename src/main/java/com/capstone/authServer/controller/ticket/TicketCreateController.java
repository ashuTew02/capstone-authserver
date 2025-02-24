package com.capstone.authServer.controller.ticket;

import com.capstone.authServer.dto.CreateTicketRequest;
import com.capstone.authServer.dto.response.ApiResponse;
import com.capstone.authServer.security.annotation.AllowedRoles;
import com.capstone.authServer.service.JiraTicketService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/tickets")
@CrossOrigin
public class TicketCreateController {

    private final JiraTicketService jiraTicketService;

    public TicketCreateController(JiraTicketService jiraTicketService) {
        this.jiraTicketService = jiraTicketService;
    }

    @PostMapping("/create")
    @AllowedRoles({"USER", "ADMIN", "SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<?>> createTicket(@RequestBody CreateTicketRequest request) {

        System.out.println("INSIDE TICKET CREATION CONTROLLER");
        // We assume tenantId is in the authentication details
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long tenantId = (Long) auth.getDetails();

        String newTicketId = jiraTicketService.createTicket(
            tenantId,
            request.getFindingId(),
            request.getSummary(),
            request.getDescription()
        );

        return new ResponseEntity<>(
            ApiResponse.success(HttpStatus.OK.value(), "Ticket created successfully.", newTicketId),
            HttpStatus.OK
        );
    }
}
