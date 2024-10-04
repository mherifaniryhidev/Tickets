package mg.mherifaniry.Tickets.dto;

import lombok.*;
import mg.mherifaniry.Tickets.entities.User;
import mg.mherifaniry.Tickets.entities.enums.TicketStatus;

@Data
@NoArgsConstructor
public class TicketDto {
    private int id;
    private String titre;
    private String description;
    private UserDto creator;
    private UserDto assignedUser;
    private TicketStatus status;

    public TicketDto(int id, String titre, String description, UserDto creator, TicketStatus status){
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.creator = creator;
        this.status = status;
    }

}
