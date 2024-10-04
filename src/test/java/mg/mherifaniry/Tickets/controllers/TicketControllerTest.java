package mg.mherifaniry.Tickets.controllers;


import mg.mherifaniry.Tickets.dto.TicketDto;
import mg.mherifaniry.Tickets.dto.UserDto;

import mg.mherifaniry.Tickets.entities.enums.TicketStatus;
import mg.mherifaniry.Tickets.services.TicketServiceImpl;
import mg.mherifaniry.Tickets.services.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Arrays;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(TicketController.class)
@AutoConfigureMockMvc
class TicketControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @InjectMocks
    private TicketController ticketController;

    @MockBean
    private TicketServiceImpl ticketServiceImpl;
    @MockBean
    private UserServiceImpl userServiceImpl;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        this.mockMvc = MockMvcBuilders.standaloneSetup(ticketController).build();
    }

    @Test
    @Order(1)
    @WithMockUser(username = "test2@test.com", password = "Tester 2")  // Simulate authenticated user
    public void testgetAllTickets() throws Exception {
        //Given
        List<TicketDto> tickets =  Arrays.asList(
                new TicketDto(1, "ticket 1", "description 1", new UserDto(1, "tester1", "tester1@test.com"), TicketStatus.IN_PROGRESS),
                new TicketDto(2, "ticket 2", "description 2", new UserDto(2, "tester2", "tester2@test.com"), TicketStatus.IN_PROGRESS)
        );

        //Mock
        Mockito.when(ticketController.getAllTickets())
                .thenReturn(tickets);

        mockMvc.perform(get("/tickets")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].titre").value("ticket 1"))
                .andExpect(jsonPath("$[0].description").value("description 1"))
                .andExpect(jsonPath("$[0].creator.id").value(1))
                .andExpect(jsonPath("$[0].creator.username").value("tester1"))
                .andExpect(jsonPath("$[0].creator.email").value("tester1@test.com"))
                .andExpect(jsonPath("$[0].status").value("IN_PROGRESS"))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].titre").value("ticket 2"))
                .andExpect(jsonPath("$[1].description").value("description 2"))
                .andExpect(jsonPath("$[1].creator.id").value(2))
                .andExpect(jsonPath("$[1].creator.username").value("tester2"))
                .andExpect(jsonPath("$[1].creator.email").value("tester2@test.com"))
                .andExpect(jsonPath("$[1].status").value("IN_PROGRESS"));

    }

    /*@Test
    public void getAllTickets_shouldReturnUnauthorized_whenNotAuthenticated() throws Exception {
        // Try to access the secured endpoint without authentication
        mockMvc.perform(get("/tickets"))
                .andExpect(status().isUnauthorized()); // Expect 401 Unauthorized
    }*/
}