package com.carincho.backend.usersapp.backendusersapp.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carincho.backend.usersapp.backendusersapp.models.dto.UserDto;
import com.carincho.backend.usersapp.backendusersapp.models.dto.mapper.DtoMapperUser;
import com.carincho.backend.usersapp.backendusersapp.models.entities.Role;
import com.carincho.backend.usersapp.backendusersapp.models.entities.User;
import com.carincho.backend.usersapp.backendusersapp.models.request.UserRequest;
import com.carincho.backend.usersapp.backendusersapp.repositories.RoleRepository;
import com.carincho.backend.usersapp.backendusersapp.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> findAll() {

        List<User> users = (List<User>) userRepository.findAll();

        return users
                .stream()
                .map(u -> DtoMapperUser.builder().setUser(u).build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<UserDto> findById(Long id) {

        return userRepository.findById(id).map(u -> DtoMapperUser
                .builder()
                .setUser(u)
                .build());

    }

    @Override
    @Transactional
    public UserDto save(User user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));

        Optional<Role> optRole = roleRepository.findByName("ROLE_USER");
        List<Role> roles = new ArrayList<>();

        if (optRole.isPresent()) {
            roles.add(optRole.orElseThrow());
        }

        user.setRoles(roles);

        return DtoMapperUser
                .builder()
                .setUser(userRepository.save(user))
                .build();
    }

    @Override
    @Transactional
    public void remove(Long id) {

        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Optional<UserDto> update(UserRequest user, Long id) {

        Optional<User> o = userRepository.findById(id);
        User userOptional = null;

        if (o.isPresent()) {
            User userDb = o.orElseThrow();
            userDb.setUsername(user.getUsername());
            userDb.setEmail(user.getEmail());
            userOptional = userRepository.save(userDb);

        }

        return Optional.ofNullable(DtoMapperUser.builder().setUser(userOptional).build());
    }

}
