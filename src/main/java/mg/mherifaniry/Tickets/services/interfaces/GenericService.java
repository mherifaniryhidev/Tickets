package mg.mherifaniry.Tickets.services.interfaces;

import java.util.List;
import java.util.Optional;

public interface GenericService<T, ID> {
    List<T> findAll();
    T findOne(ID id);
    T save(T entity);
    void remove(T entity);
}
