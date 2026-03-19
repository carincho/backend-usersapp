package com.carincho.backend.usersapp.backendusersapp.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Optional;
import java.util.stream.Collectors;

import com.carincho.backend.usersapp.backendusersapp.repositories.UserRepository;


//Pagina de login
//La validacion del password lo hace por debajo 
@Service
public class JpaUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        Optional<com.carincho.backend.usersapp.backendusersapp.models.entities.User> opt = userRepository.getUserByUsername(username);

        if (!opt.isPresent()) {
            throw new UsernameNotFoundException(String.format("User %s not found", username));
        }

        com.carincho.backend.usersapp.backendusersapp.models.entities.User user  = opt.orElseThrow();        
        List<GrantedAuthority> authorities = user.getRoles()
        .stream()
        .map(r -> new SimpleGrantedAuthority(r.getName()))
        .collect(Collectors.toList());

        return new User(user.getUsername(),
                // "{noop}12345",//Hay que indicar que no va a ser encriptado, por eso se utiliza el prefijo {noop} para indicar que no se va a aplicar ningún tipo de codificación a la contraseña. Esto es útil para propósitos de desarrollo o pruebas, pero en un entorno de producción se recomienda utilizar un encoder de contraseñas más seguro, como BCryptPasswordEncoder, para proteger las contraseñas de los usuarios.
                user.getPassword(),
                true,
                true,
                true,
                true,
                authorities);

    }
}