package mg.mherifaniry.Tickets.services;

import mg.mherifaniry.Tickets.dao.UserRepository;
import mg.mherifaniry.Tickets.dto.UserDto;
import mg.mherifaniry.Tickets.entities.User;
import mg.mherifaniry.Tickets.mapper.UserMapper;
import mg.mherifaniry.Tickets.services.interfaces.GenericService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl implements UserDetailsService, GenericService<UserDto, Integer> {

    private UserRepository userRepository;
    private UserMapper userMapper;


    @Autowired
    public UserServiceImpl(UserRepository userRepository, UserMapper userMapper){
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }
    @Override
    public UserDto save(UserDto userDto) {
        User user = this.userMapper.toEntity(userDto);
        return this.userMapper.toDTO(this.userRepository.save(user));
    }

    @Override
    public void remove(UserDto entity) {

    }

    @Override
    public List<UserDto> findAll() {
        List<User> users = this.userRepository.findAll();
        return users.stream()
                .map(user -> {
                    return this.userMapper.toDTO(user);
                })
                .collect(Collectors.toList());
    }

    @Override
    public UserDto findOne(Integer id) {
        Optional<User> found = this.userRepository.findById(id);
        if(found.isEmpty() == true)
        {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
        return this.userMapper.toDTO(found.get());
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = this.userRepository.findByEmail(email); // Cherche l'utilisateur par son email dans la base de données
        if(user == null)
        {
            throw new UsernameNotFoundException(email+" not found");
        }
        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                true,
                true,
                true,
                true,
                getAuthorities()
        );
    }

    private Collection<GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // Return an empty list if there are no roles
    }
}
