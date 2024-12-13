package com.elmobachiadil.airbnb_clone_back.user.mapper;

import com.elmobachiadil.airbnb_clone_back.user.application.dto.ReadUserDTO;
import com.elmobachiadil.airbnb_clone_back.user.domain.Authority;
import com.elmobachiadil.airbnb_clone_back.user.domain.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    ReadUserDTO readUserDTOToUser(User user);

    default String mapAuthoritiesToString(Authority authority) {
        return authority.getName();
    }
}
