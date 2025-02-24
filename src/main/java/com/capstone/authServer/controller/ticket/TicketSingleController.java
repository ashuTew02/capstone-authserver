// src/main/java/com/capstone/authServer/controller/ticket/TicketSingleController.java

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

@RestController
@RequestMapping("/tickets")
@CrossOrigin
public class TicketSingleController {

    private final JiraTicketService jiraTicketService;

    public TicketSingleController(JiraTicketService jiraTicketService) {
        this.jiraTicketService = jiraTicketService;
    }

    @GetMapping("/{ticketId}")
    @AllowedRoles({"USER", "ADMIN", "SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<?>> getSingleTicket(@PathVariable String ticketId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long tenantId = (Long) auth.getDetails();

        TicketDTO dto = jiraTicketService.getTicketById(tenantId, ticketId);
        if (dto == null) {
            return new ResponseEntity<>(
                ApiResponse.error(HttpStatus.NOT_FOUND.value(), "Ticket not found: " + ticketId),
                HttpStatus.NOT_FOUND
            );
        }

        return new ResponseEntity<>(
            ApiResponse.success(HttpStatus.OK.value(), "Ticket fetched successfully.", dto),
            HttpStatus.OK
        );
    }
}
