package mg.mherifaniry.Tickets.services;

import mg.mherifaniry.Tickets.dao.TicketRepository;
import mg.mherifaniry.Tickets.dao.UserRepository;
import mg.mherifaniry.Tickets.dto.TicketDto;
import mg.mherifaniry.Tickets.dto.UserDto;
import mg.mherifaniry.Tickets.entities.Ticket;
import mg.mherifaniry.Tickets.entities.User;
import mg.mherifaniry.Tickets.entities.enums.TicketStatus;
import mg.mherifaniry.Tickets.mapper.TicketMapper;
import org.junit.jupiter.api.*;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class TicketServiceImplTest {

    @InjectMocks
    private TicketServiceImpl ticketServiceImpl; // inject the mock TestRepository here
    //declare the dependencies
    @Mock
    private TicketRepository ticketRepository; // mock this repository
    @Mock
    private UserRepository userRepository;
    @Mock
    private TicketMapper ticketMapper;
    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Start the mock for this current class
        SecurityContextHolder.setContext(securityContext);
    }
    @Test
    @Order(1)
    public void saveTest()
    {
        //Given
        String username = "tester1";
        User user = new User(1, "tester1", "tester1@test.com");
        UserDto userDto = new UserDto(1, "tester1", "tester1@test.com");
        TicketDto ticketDto = new TicketDto();
        ticketDto.setTitre("Test Ticket");
        ticketDto.setDescription("Description long long text");
        ticketDto.setCreator(userDto);
        ticketDto.setStatus(TicketStatus.IN_PROGRESS);

        Ticket ticket = new Ticket();
        ticket.setTitre("Test Ticket");
        ticket.setDescription("Description long long text");
        ticket.setCreator(user);

        Ticket savedTicket = new Ticket();
        savedTicket.setId(1);
        savedTicket.setTitre("Test Ticket");
        savedTicket.setDescription("Description long long text");
        savedTicket.setCreator(user);

        // Mock the calls
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getName()).thenReturn(username);
        Mockito.when(userRepository.findByUsername(username))
                .thenReturn(user);
        Mockito.when(ticketMapper.toEntity(ticketDto))
                        .thenReturn(ticket);
        Mockito.when(ticketRepository.save(ticket))
                .thenReturn(savedTicket);
        Mockito.when(ticketMapper.toDTO(savedTicket))
                .thenReturn(new TicketDto(1, "Test Ticket", "Description long long text", userDto, TicketStatus.IN_PROGRESS ));


        //When
        TicketDto resultTicket = ticketServiceImpl.save(ticketDto);
        // Then
        assertNotNull(resultTicket);
        assertEquals(ticketDto.getTitre(), resultTicket.getTitre());
        assertEquals(ticketDto.getDescription(), resultTicket.getDescription());
        assertEquals(ticketDto.getStatus(), resultTicket.getStatus());
        assertEquals(ticketDto.getCreator().getId(), resultTicket.getCreator().getId());

        //verify(ticketRepository).save(ticket);
        Mockito.verify(securityContext).getAuthentication();
        Mockito.verify(authentication).getName();
        Mockito.verify(userRepository, Mockito.times(1)).findByUsername(username);
        Mockito.verify(ticketMapper, Mockito.times(1)).toEntity(ticketDto);
        Mockito.verify(ticketRepository, Mockito.times(1)).save(ticket);
        Mockito.verify(ticketMapper, Mockito.times(1)).toDTO(savedTicket);
    }

    @Test
    @Order(2)
    public void testFindAll(){
        //Given
        String username = "tester1";
        User user = new User(1, "tester1", "tester1@test.com");
        UserDto userDto = new UserDto(1, "tester1", "tester1@test.com");
        Ticket ticket = new Ticket(1, "ticket 1", "description 1", user, TicketStatus.IN_PROGRESS);
        TicketDto ticketDto = new TicketDto(1, "ticket 1", "description 1", userDto, TicketStatus.IN_PROGRESS);
        List<Ticket> tcks =  Arrays.asList(
                ticket
        );

        // Mock the calls
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        Mockito.when(authentication.getName()).thenReturn(username);
        Mockito.when(userRepository.findByUsername(username))
                        .thenReturn(user);


        Mockito.when(ticketRepository.findByCreatorOrAssignedUser(user, user))
                .thenReturn(tcks);
        
        Mockito.when(ticketMapper.toDTO(ticket))
                .thenReturn(ticketDto);

        List<TicketDto> tickets = ticketServiceImpl.findAll(); // méthode à tester
        assertNotNull(tickets);
        assertEquals(1, tickets.size());
        assertEquals(1, tickets.get(0).getId());
        assertEquals("ticket 1", tickets.get(0).getTitre());
        assertEquals("description 1", tickets.get(0).getDescription());
        assertEquals(TicketStatus.IN_PROGRESS, tickets.get(0).getStatus());

        assertEquals(user.getId(), tickets.get(0).getCreator().getId());
        assertEquals(user.getUsername(), tickets.get(0).getCreator().getUsername());
        assertEquals(user.getEmail(), tickets.get(0).getCreator().getEmail());


        Mockito.verify(ticketRepository, Mockito.times(1)).findByCreatorOrAssignedUser(user, user);
    }


    @Test
    @Order(3)
    public void testFindOne(){
        int id = 1;
        Ticket ticket = new Ticket(1, "ticket 1", "description 1", new User(1, "tester1", "tester1@test.com"), TicketStatus.IN_PROGRESS);
        Mockito.when(ticketRepository.findById(id))
                .thenReturn(Optional.of(ticket));
        Mockito.when(ticketMapper.toDTO(ticket))
                        .thenReturn(new TicketDto(1, "ticket 1", "description 1", new UserDto(1, "tester1", "tester1@test.com"), TicketStatus.IN_PROGRESS));


        TicketDto ticketFound = ticketServiceImpl.findOne(id);
        assertNotNull(ticketFound);
        assertEquals(ticket.getId(), ticketFound.getId());
        assertEquals(ticket.getTitre(), ticketFound.getTitre());
        assertEquals(ticket.getDescription(), ticketFound.getDescription());
        assertEquals(ticket.getCreator().getId(), ticketFound.getCreator().getId());
        assertEquals(ticket.getCreator().getUsername(), ticketFound.getCreator().getUsername());
        assertEquals(ticket.getCreator().getEmail(), ticketFound.getCreator().getEmail());
        assertEquals(ticket.getStatus(), ticketFound.getStatus());

        Mockito.verify(ticketRepository, Mockito.times(1)).findById(id);
        Mockito.verify(ticketMapper, Mockito.times(1)).toDTO(ticket);
    }

    @Test
    @Order(4)
    public void testRemoveTicket() {
        // Créer un ticket pour le test
        //Given
        TicketDto ticketDto = new TicketDto(1, "ticket 1", "description 1", new UserDto(1, "tester1", "tester1@test.com"), TicketStatus.IN_PROGRESS);
        Ticket ticket = new Ticket(1, "ticket 1", "description 1", new User(1, "tester1", "tester1@test.com"), TicketStatus.IN_PROGRESS);

        Mockito.when(ticketMapper.toEntity(ticketDto))
                .thenReturn(ticket);

        // Appeler la méthode à tester
        ticketServiceImpl.remove(ticketDto);

        // Vérifier que la méthode deleteById a bien été appelée avec l'ID du ticket
        verify(ticketMapper, Mockito.times(1)).toEntity(ticketDto);
        verify(ticketRepository, Mockito.times(1)).deleteById(ticket.getId());
    }


}