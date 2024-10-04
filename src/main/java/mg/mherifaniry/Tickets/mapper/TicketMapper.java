package mg.mherifaniry.Tickets.mapper;

import mg.mherifaniry.Tickets.dto.TicketDto;
import mg.mherifaniry.Tickets.entities.Ticket;
import mg.mherifaniry.Tickets.entities.enums.TicketStatus;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class TicketMapper implements Mapper<Ticket, TicketDto>{
    private static ModelMapper modelMapper;
    @Autowired
    public TicketMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }


    @Override
     public Ticket toEntity(TicketDto ticketDto) {
        return this.modelMapper.map(ticketDto, Ticket.class);
    }

    @Override
     public TicketDto toDTO(Ticket ticket) {
        return  this.modelMapper.map(ticket, TicketDto.class);
    }
}
