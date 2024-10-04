package mg.mherifaniry.Tickets.dao;

import mg.mherifaniry.Tickets.entities.Ticket;
import mg.mherifaniry.Tickets.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Integer> {
    List<Ticket> findByCreatorOrAssignedUser(User creator, User assignedUser);
    // Trouver tous les tickets soit assignés soit créés par un utilisateur
    List<Ticket> findByAssignedUserIdOrCreatorId(int assignedUserId, int creatorId);
}
