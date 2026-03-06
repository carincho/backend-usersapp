package com.carincho.backend.usersapp.backendusersapp.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.carincho.backend.usersapp.backendusersapp.models.entities.User;
import com.carincho.backend.usersapp.backendusersapp.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public List<User> findAll() {

        return (List<User>) userRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<User> findById(Long id) {

        return userRepository.findById(id);
    }

    @Override
    @Transactional
    public User save(User user) {

        return userRepository.save(user);
    }

    @Override
    @Transactional
    public void remove(Long id) {

        userRepository.deleteById(id);
    }

    @Override
    @Transactional
    public Optional<User> update(User user, Long id) {

        Optional<User> o = findById(id);
        User userOptional = null;

        if (o.isPresent()) {
            User userDb = o.orElseThrow();
            userDb.setUsername(user.getUsername());
            userDb.setEmail(user.getEmail());
            userOptional = this.save(userDb);

        }

        return Optional.ofNullable(userOptional);
    }

}
