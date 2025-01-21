package com.ilyasbugra.excusegenerator.v2.mapper;

import com.ilyasbugra.excusegenerator.v2.dto.UserLoginResponseDTO;
import com.ilyasbugra.excusegenerator.v2.dto.UserSignUpRequestDTO;
import com.ilyasbugra.excusegenerator.v2.dto.UserSignUpResponseDTO;
import com.ilyasbugra.excusegenerator.v2.model.User;
import com.ilyasbugra.excusegenerator.v2.model.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserSignUpResponseDTO toUserSignUpResponseDTO(User user, String message);

    @Mapping(target = "id", ignore = true)
    User toUser(UserSignUpRequestDTO userSignUpRequestDTO, String password, UserRole userRole);

    UserLoginResponseDTO toUserLoginResponseDTO(User user, String token);
}
