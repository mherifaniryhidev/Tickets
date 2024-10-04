package mg.mherifaniry.Tickets.entities;

import jakarta.persistence.*;
import lombok.*;
import mg.mherifaniry.Tickets.entities.enums.TicketStatus;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ticket")
public class Ticket {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String titre;
    private String description;


    @ManyToOne
    @JoinColumn(name = "creator_user_id", referencedColumnName = "id")
    private User creator; // creator

    @ManyToOne
    @JoinColumn(name = "assigned_user_id", referencedColumnName = "id")
    private User assignedUser; // creator


    @Enumerated(EnumType.STRING)
    private TicketStatus status;

    public Ticket(int id, String titre, String description,  User creator, TicketStatus status){
        this.id = id;
        this.titre = titre;
        this.description = description;
        this.creator = creator;
        this.status = status;
    }


}
