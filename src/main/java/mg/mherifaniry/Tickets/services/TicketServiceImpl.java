package mg.mherifaniry.Tickets.services;

import mg.mherifaniry.Tickets.dao.TicketRepository;
import mg.mherifaniry.Tickets.dao.UserRepository;
import mg.mherifaniry.Tickets.dto.TicketDto;
import mg.mherifaniry.Tickets.entities.Ticket;
import mg.mherifaniry.Tickets.entities.User;
import mg.mherifaniry.Tickets.mapper.TicketMapper;
import mg.mherifaniry.Tickets.mapper.UserMapper;
import mg.mherifaniry.Tickets.services.interfaces.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TicketServiceImpl implements GenericService<TicketDto, Integer> {

    private TicketRepository ticketRepository;
    private UserRepository userRepository;
    private TicketMapper ticketMapper;
    private UserMapper userMapper;
    @Autowired
    public TicketServiceImpl(TicketRepository ticketRepository,
                             UserRepository userRepository,
                             TicketMapper ticketMapper,
                             UserMapper userMapper){
        this.ticketRepository = ticketRepository;
        this.userRepository = userRepository;
        this.ticketMapper = ticketMapper;
        this.userMapper = userMapper;
    }

    public TicketServiceImpl(){}

    @Override
    public TicketDto findOne(Integer id) {
        Optional<Ticket> ticket = ticketRepository.findById(id);
        if(ticket.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return this.ticketMapper.toDTO(ticket.get());
    }

    public List<TicketDto> findAssignedTicket(Integer id)
    {
        List<Ticket> tickets = this.ticketRepository.findByAssignedUserIdOrCreatorId(id, id);
        return tickets.stream()
                .map(ticket -> {
                    return this.ticketMapper.toDTO(ticket);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<TicketDto> findAll() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User theUser = userRepository.findByUsername(username);
        List<Ticket> tickets = this.ticketRepository.findByCreatorOrAssignedUser(theUser, theUser);
        return tickets.stream()
                .map(ticket -> {
                    return this.ticketMapper.toDTO(ticket);
                })
                .collect(Collectors.toList());
    }

    @Override
    public TicketDto save(TicketDto ticketDto) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        User theCreator = userRepository.findByUsername(username);
        ticketDto.setCreator(this.userMapper.toDTO(theCreator));
        Ticket ticket = this.ticketMapper.toEntity(ticketDto);
        return this.ticketMapper.toDTO(this.ticketRepository.save(ticket));
    }

    @Override
    public void remove(TicketDto ticketDto) {
        Ticket ticket = this.ticketMapper.toEntity(ticketDto);
        this.ticketRepository.deleteById(ticket.getId());
    }
}
