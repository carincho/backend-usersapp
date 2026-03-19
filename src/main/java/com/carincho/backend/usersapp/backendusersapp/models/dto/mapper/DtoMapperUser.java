package com.carincho.backend.usersapp.backendusersapp.models.dto.mapper;

import com.carincho.backend.usersapp.backendusersapp.models.dto.UserDto;
import com.carincho.backend.usersapp.backendusersapp.models.entities.User;

public class DtoMapperUser {



    private User user;

    // el constructor private se usa para que no se crea la instancia con new si no con el metodo estatic
    private DtoMapperUser() {
    }

    public static DtoMapperUser builder() {

        return new DtoMapperUser();
        
    }

    public DtoMapperUser setUser(User user) {
        this.user = user;

        return this;
    }

    public UserDto build() {

        if (user == null) {

            throw new RuntimeException("Debe pasar el entity user");
        }

        return new UserDto(this.user.getId(), user.getUsername(), user.getEmail());

    }

}
