package mg.mherifaniry.Tickets.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.context.MessageSource;
import mg.mherifaniry.Tickets.dto.TicketDto;
import mg.mherifaniry.Tickets.dto.UserDto;
import mg.mherifaniry.Tickets.entities.enums.TicketStatus;
import mg.mherifaniry.Tickets.services.TicketServiceImpl;
import mg.mherifaniry.Tickets.services.UserServiceImpl;

import java.util.List;
import java.util.Locale;
import java.util.Objects;

@RestController
@RequestMapping(value = "/tickets")
@Tag(name = "Ticket Management", description = "API for managing tickets")
public class TicketController {

    private TicketServiceImpl ticketService;
    private UserServiceImpl userService;
    private final MessageSource messageSource;

    @Autowired
    public TicketController(TicketServiceImpl ticketService,
                            UserServiceImpl userService,
                            MessageSource messageSource) {
        this.ticketService = ticketService;
        this.userService = userService;
        this.messageSource = messageSource;
    }

    @Operation(summary = "Get all tickets", description = "Retrieve a list of all tickets in the system.")
    @GetMapping
    public List<TicketDto> getAllTickets() {
        return ticketService.findAll();
    }

    @Operation(summary = "Get ticket by ID", description = "Retrieve a ticket by its ID. Only the creator or assigned user can access the ticket.")
    @GetMapping(value = "/{id}")
    public ResponseEntity<?> getTicketById(
            @Parameter(description = "ID of the ticket to retrieve", required = true) @PathVariable int id) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        try {
            TicketDto ticketDto = ticketService.findOne(id);

            // Vérification de l'utilisateur assigné ou du créateur du ticket
            if ((ticketDto.getAssignedUser() != null && Objects.equals(ticketDto.getAssignedUser().getUsername(), username))
                    || Objects.equals(ticketDto.getCreator().getUsername(), username)) {
                return new ResponseEntity<>(ticketDto, HttpStatus.OK);
            }

            // Si l'utilisateur n'est ni le créateur ni l'assigné
            return new ResponseEntity<>(messageSource.getMessage("ressource.noaccess", null, Locale.getDefault()), HttpStatus.FORBIDDEN);

        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(messageSource.getMessage("ressource.notFound", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }
    }

    @Operation(summary = "Create a new ticket", description = "Create a new ticket with the provided details.")
    @PostMapping(consumes = "application/json")
    public TicketDto addTicket(
            @Parameter(description = "Details of the ticket to be created", required = true) @RequestBody TicketDto ticketDto) {
        return ticketService.save(ticketDto);
    }

    @Operation(summary = "Update a ticket", description = "Update an existing ticket by its ID with the provided details.")
    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> updateTicket(
            @Parameter(description = "ID of the ticket to update", required = true) @PathVariable int id,
            @Parameter(description = "Updated ticket details", required = true) @RequestBody TicketDto ticketDto) {
        TicketDto ticketToUpdate = new TicketDto();
        try {
            ticketToUpdate = ticketService.findOne(id);
            ticketToUpdate.setDescription(ticketDto.getDescription());
            ticketToUpdate.setTitre(ticketDto.getTitre());
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(messageSource.getMessage("ressource.notFound", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(ticketService.save(ticketToUpdate), HttpStatus.OK);
    }

    @Operation(summary = "Assign a ticket to a user", description = "Assign an existing ticket to a user by their ID.")
    @PutMapping(value = "/{id}/assign/{userId}")
    public ResponseEntity<?> assignTicket(
            @Parameter(description = "ID of the ticket to assign", required = true) @PathVariable int id,
            @Parameter(description = "ID of the user to assign the ticket to", required = true) @PathVariable int userId) {
        UserDto userDto = new UserDto();
        TicketDto ticket = new TicketDto();
        try {
            userDto = this.userService.findOne(userId);
            ticket = this.ticketService.findOne(id);
            ticket.setStatus(TicketStatus.IN_PROGRESS);
            ticket.setAssignedUser(userDto);
            this.ticketService.save(ticket);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(messageSource.getMessage("ressource.notFound", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(messageSource.getMessage("ticket.assigned", null, Locale.getDefault()), HttpStatus.OK);
    }

    @Operation(summary = "Delete a ticket", description = "Delete a ticket by its ID.")
    @DeleteMapping(value = "/{id}")
    public ResponseEntity<?> removeTicket(
            @Parameter(description = "ID of the ticket to delete", required = true) @PathVariable int id) {

        try {
            TicketDto ticket = this.ticketService.findOne(id);
            this.ticketService.remove(ticket);
            return new ResponseEntity<>(messageSource.getMessage("success.delete", null, Locale.getDefault()), HttpStatus.OK);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(messageSource.getMessage("ressource.notFound", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }
    }
}
