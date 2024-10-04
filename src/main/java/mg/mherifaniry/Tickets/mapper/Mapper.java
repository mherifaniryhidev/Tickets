package mg.mherifaniry.Tickets.mapper;

public interface Mapper<E, DTO> {
    E toEntity(DTO dto);
    DTO toDTO(E e);
}
