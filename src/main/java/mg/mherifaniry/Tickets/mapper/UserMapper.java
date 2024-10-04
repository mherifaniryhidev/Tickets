package mg.mherifaniry.Tickets.mapper;

import mg.mherifaniry.Tickets.dto.UserDto;
import mg.mherifaniry.Tickets.entities.Ticket;
import mg.mherifaniry.Tickets.entities.User;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserMapper implements Mapper<User, UserDto> {

    private static ModelMapper modelMapper;
    @Autowired
    public UserMapper(ModelMapper modelMapper){
        this.modelMapper = modelMapper;
    }


    @Override
    public  User toEntity(UserDto userDto) {
        return this.modelMapper.map(userDto, User.class);
    }

    @Override
    public  UserDto toDTO(User user) {
        return this.modelMapper.map(user, UserDto.class);
    }
}
