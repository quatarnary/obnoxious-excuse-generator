package com.ilyasbugra.excusegenerator.mapper;

import com.ilyasbugra.excusegenerator.dto.UserLoginResponseDTO;
import com.ilyasbugra.excusegenerator.dto.UserSignUpRequestDTO;
import com.ilyasbugra.excusegenerator.dto.UserSignUpResponseDTO;
import com.ilyasbugra.excusegenerator.model.User;
import com.ilyasbugra.excusegenerator.model.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserSignUpResponseDTO toUserSignUpResponseDTO(User user, String message);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "password", source = "password")
    User toUser(UserSignUpRequestDTO userSignUpRequestDTO, String password, UserRole userRole);

    UserLoginResponseDTO toUserLoginResponseDTO(User user, String token);
}
