package com.capstone.authServer.controller.ticket;

import com.capstone.authServer.dto.CreateTicketRequest;
import com.capstone.authServer.dto.event.payload.ticket.TicketCreateEventPayload;
import com.capstone.authServer.dto.event.ticket.TicketCreateEvent;
import com.capstone.authServer.dto.response.ApiResponse;
import com.capstone.authServer.kafka.producer.TicketCreateEventProducer;
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
public class TicketCreateController {

    private final ElasticSearchService esService;
    private final TicketCreateEventProducer producer;

    public TicketCreateController(JiraTicketService jiraTicketService, ElasticSearchService esService, TicketCreateEventProducer producer) {
        this.esService = esService;
        this.producer = producer;
    }

    @PostMapping("/create")
    @AllowedRoles({"USER", "ADMIN", "SUPER_ADMIN"})
    public ResponseEntity<ApiResponse<?>> createTicket(@RequestBody CreateTicketRequest request) {

        System.out.println("INSIDE TICKET CREATION CONTROLLER");
        // We assume tenantId is in the authentication details
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Long tenantId = (Long) auth.getDetails();

        Finding finding = esService.getFindingById(request.getFindingId(), tenantId);

        TicketCreateEventPayload payload = new TicketCreateEventPayload(
            request.getFindingId(),
            request.getSummary(),
            request.getDescription(),
            tenantId,
            KafkaTopic.BGJOBS_JFC,
            finding.getToolType()
        );
        TicketCreateEvent event = new TicketCreateEvent(payload);
        producer.produce(event);
        
        // String newTicketId = jiraTicketService.createTicket(
        //     tenantId,
        //     request.getFindingId(),
        //     request.getSummary(),
        //     request.getDescription()
        // );

        return new ResponseEntity<>(
            ApiResponse.success(HttpStatus.OK.value(), "Ticket created successfully.", null),
            HttpStatus.OK
        );
    }
}
