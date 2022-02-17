package com.company.archon.mapper;

import com.company.archon.dto.UserDto;
import com.company.archon.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto mapToDto(User user);
}
