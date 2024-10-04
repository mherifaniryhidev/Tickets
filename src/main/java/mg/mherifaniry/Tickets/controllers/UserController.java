package mg.mherifaniry.Tickets.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import mg.mherifaniry.Tickets.dto.UserDto;
import mg.mherifaniry.Tickets.dto.TicketDto;
import mg.mherifaniry.Tickets.entities.User;
import mg.mherifaniry.Tickets.services.UserServiceImpl;
import mg.mherifaniry.Tickets.services.TicketServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@RestController
@RequestMapping("/users")
@Tag(name = "User Management", description = "API for managing users")
public class UserController {

    private UserServiceImpl userService;
    private TicketServiceImpl ticketService;
    private final MessageSource messageSource;
    private BCryptPasswordEncoder bCryptPass = new BCryptPasswordEncoder(5);

    @Autowired
    public UserController(UserServiceImpl userService, TicketServiceImpl ticketService, MessageSource messageSource) {
        this.userService = userService;
        this.ticketService = ticketService;
        this.messageSource = messageSource;
    }

    @Operation(summary = "Get all users", description = "Retrieve a list of all users in the system.")
    @GetMapping
    public ResponseEntity<List<User>> getAllUsers() {
        List<UserDto> users = userService.findAll();
        return new ResponseEntity(users, HttpStatus.OK);
    }

    @Operation(summary = "Get all tickets assigned to a user", description = "Retrieve all tickets assigned to a specific user by their ID.")
    @GetMapping(value = "/{id}/ticket", produces = "application/json")
    public ResponseEntity<?> getAllTicketsByUserId(
            @Parameter(description = "ID of the user whose assigned tickets are to be retrieved", required = true)
            @PathVariable int id) {
        List<TicketDto> ticketDtos = new ArrayList<>();
        try {
            ticketDtos = this.ticketService.findAssignedTicket(id);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(messageSource.getMessage("user.notFound", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(ticketDtos, HttpStatus.OK);
    }

    @Operation(summary = "Add a new user", description = "Create a new user with the provided details. Email must be unique.")
    @PostMapping(consumes = "application/json")
    public ResponseEntity<?> addUser(
            @Parameter(description = "Details of the new user to be created", required = true)
            @Valid @RequestBody UserDto userDto, BindingResult result) {

        if (result.hasErrors()) {
            // Return a response with validation errors
            return ResponseEntity.badRequest().body(result.getAllErrors());
        }

        try {
            userDto.setPassword(bCryptPass.encode(userDto.getPassword()));
            userDto = userService.save(userDto);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(messageSource.getMessage("user.emailExists", null, Locale.getDefault()), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity(userDto, HttpStatus.CREATED);
    }

    @Operation(summary = "Update an existing user", description = "Update the details of an existing user by their ID.")
    @PutMapping(value = "/{id}", consumes = "application/json")
    public ResponseEntity<?> updateUser(
            @Parameter(description = "ID of the user to update", required = true) @PathVariable Integer id,
            @Parameter(description = "Updated user details", required = true) @RequestBody UserDto user) {

        UserDto userToUpdate = new UserDto();
        try {
            userToUpdate = userService.findOne(id);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(messageSource.getMessage("user.notFound", null, Locale.getDefault()), HttpStatus.NOT_FOUND);
        }

        userToUpdate.setEmail(user.getEmail());
        userToUpdate.setUsername(user.getUsername());
        userToUpdate.setPassword(bCryptPass.encode(user.getPassword()));

        try {
            userToUpdate = userService.save(userToUpdate);
        } catch (ResponseStatusException e) {
            return new ResponseEntity<>(messageSource.getMessage("user.unsaved", null, Locale.getDefault()), HttpStatus.BAD_REQUEST);
        }

        return new ResponseEntity<>(userToUpdate, HttpStatus.OK);
    }
}
