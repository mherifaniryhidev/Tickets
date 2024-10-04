package mg.mherifaniry.Tickets.services;


import mg.mherifaniry.Tickets.dao.UserRepository;
import mg.mherifaniry.Tickets.dto.UserDto;
import mg.mherifaniry.Tickets.entities.User;
import mg.mherifaniry.Tickets.mapper.UserMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userServiceImpl;

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    @Order(1)
    public void testSave()
    {
        UserDto userDto = new UserDto(0, "tester1", "tester1@test.com");
        User user = new User(0,"tester1", "tester1@test.com");
        User savedUser = new User(1,"tester1", "tester1@test.com");


        Mockito.when(this.userMapper.toEntity(userDto))
                .thenReturn(user);
        Mockito.when(this.userRepository.save(user))
                .thenReturn(savedUser);
        Mockito.when(this.userMapper.toDTO(savedUser))
                .thenReturn(new UserDto(1,"tester1", "tester1@test.com"));


        UserDto newUser = userServiceImpl.save(userDto);
        assertNotNull(newUser);
        assertEquals(user.getEmail(), newUser.getEmail());
        assertEquals(user.getUsername(), newUser.getUsername());

        Mockito.verify(userRepository).save(user);
        Mockito.verify(userRepository, Mockito.times(1));
    }

    @Test
    @Order(2)
    public void testFindAll(){
        List<User> users = List.of(
                new User(1, "user1", "user1@test.com"),
                new User(2, "user2", "user2@test.com"),
                new User(3, "user3", "user3@test.com"));
        Mockito.when(this.userRepository.findAll()).thenReturn(users);

        for(int i=0; i <= users.size(); i++)
        {
            Mockito.when(this.userMapper.toDTO(new User(i, "user"+i, "user"+i+"@test.com")))
                    .thenReturn(new UserDto(i, "user"+i, "user"+i+"@test.com"));
        }

        List<UserDto> foundUsers = userServiceImpl.findAll();
        assertEquals(3, foundUsers.size());

        for(int i=0; i < users.size(); i++)
        {
            int value = i+1;
            assertEquals(value, foundUsers.get(i).getId());
            assertEquals("user"+value, foundUsers.get(i).getUsername());
            assertEquals("user"+value+"@test.com", foundUsers.get(i).getEmail());
        }

        Mockito.verify(userRepository, Mockito.times(1)).findAll(); // verifie si la méthode à été appellée
    }

    @Test
    @Order(3)
    public void testFindOne()
    {
        int id = 1;
        User user = new User(1, "test1", "test1@test.com");
        Mockito.when(this.userRepository.findById(id))
                .thenReturn(Optional.of(user));
        Mockito.when(this.userMapper.toDTO(user))
                .thenReturn(new UserDto(1, "test1", "test1@test.com"));

        UserDto foundUser = userServiceImpl.findOne(id);
        assertNotNull(foundUser);
        assertEquals(user.getId(), foundUser.getId());
        assertEquals(user.getUsername(),foundUser.getUsername());
        assertEquals(user.getEmail(), foundUser.getEmail());
        Mockito.verify(userRepository, Mockito.times(1)).findById(id);
    }
}