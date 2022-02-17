package com.company.archon.mapper;

import com.company.archon.dto.GameDto;
import com.company.archon.entity.Game;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface GameMapper {
    GameMapper INSTANCE = Mappers.getMapper(GameMapper.class);

    GameDto mapToDto(Game game);
}
