package com.capstone.authServer.controller.ticket;

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
public class TicketStatusUpdateController {

    private final JiraTicketService jiraTicketService;

    public TicketStatusUpdateController(JiraTicketService jiraTicketService) {
        this.jiraTicketService = jiraTicketService;
    }

    /**
     * Only job: change status from "To Do" to "Done"
     */
    @PutMapping("/{ticketId}/done")
    @AllowedRoles({"USER", "ADMIN", "SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<?>> markTicketAsDone(@PathVariable("ticketId") String ticketId) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long tenantId = (Long) auth.getDetails();
        System.out.println("INSIDE TICKET STATUS UPDATE CONTROLLER");
        jiraTicketService.transitionTicketToDone(tenantId, ticketId);

        return new ResponseEntity<>(
            ApiResponse.success(HttpStatus.OK.value(), 
                "Ticket status changed to Done for " + ticketId, 
                null),
            HttpStatus.OK
        );
    }
}
