package com.company.archon.services.impl;

import com.company.archon.dto.GameDto;
import com.company.archon.dto.UserDto;
import com.company.archon.entity.Game;
import com.company.archon.entity.User;
import com.company.archon.enums.GameStatus;
import com.company.archon.exception.EntityNotFoundException;
import com.company.archon.mapper.GameMapper;
import com.company.archon.mapper.UserMapper;
import com.company.archon.repositories.GameRepository;
import com.company.archon.repositories.UserRepository;
import com.company.archon.services.AuthorizationService;
import com.company.archon.services.GameService;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository       gameRepository;
    private final UserRepository       userRepository;
    private final AuthorizationService authorizationService;

    private List<String> questions;

    @EventListener(ApplicationReadyEvent.class)
    public void initialise() {
        questions.add("Ты выучил 19 из 20 билетов и на экзамене попался тот самый");
        questions.add("Когда пришел пьяным на философия и получил 5");
        questions.add("Когда учишься на программиста но не понял какй-то мем о программировании");
    }

    @Override
    public GameDto start() {
        Long id = authorizationService.getProfileOfCurrent().getId();
        User user = userRepository.findById(id)
                                  .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " doesn't exists!"));
        user.setScore(0);
        Game game = gameRepository.findByUsers(user).orElse(new Game());
        game.getUsers().add(user);
        game.setGameStatus(GameStatus.WAITING);
        user.setGame(game);
        gameRepository.save(game);
        userRepository.save(user);
        return GameMapper.INSTANCE.mapToDto(game);
    }

    @Override
    public GameDto invite(Long id) {
        Long currentId = authorizationService.getProfileOfCurrent().getId();
        User user = userRepository.findById(currentId)
                                  .orElseThrow(() -> new EntityNotFoundException("User with id " + currentId + " doesn't exists!"));
        User friend = userRepository.findById(id)
                                    .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " doesn't exists!"));
        friend.setScore(0);
        Game game = user.getGame();
        game.getUsers().add(friend);
        friend.setGame(game);
        gameRepository.save(game);
        userRepository.save(friend);
        return GameMapper.INSTANCE.mapToDto(game);
    }

    @Override
    public GameDto play() {
        Long currentId = authorizationService.getProfileOfCurrent().getId();
        User user = userRepository.findById(currentId)
                                  .orElseThrow(() -> new EntityNotFoundException("User with id " + currentId + " doesn't exists!"));
        Game game = user.getGame();
        game.setGameStatus(GameStatus.RUNNING);
        game.setText(questions.get((int) (Math.random() * (questions.size() - 1))));
        userRepository.save(user);
        gameRepository.save(game);
        return getGameDto(user, game);
    }

    @Override
    public GameDto select(String image) {
        Long currentId = authorizationService.getProfileOfCurrent().getId();
        User user = userRepository.findById(currentId)
                                  .orElseThrow(() -> new EntityNotFoundException("User with id " + currentId + " doesn't exists!"));
        Game game = user.getGame();
        user.setSelectedImage(image);
        userRepository.save(user);
        return GameMapper.INSTANCE.mapToDto(game);
    }

    @Override
    public GameDto vote(Long id) {
        User user = userRepository.findById(id)
                                  .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " doesn't exists!"));
        Game game = user.getGame();
        user.setScore(user.getScore() + 1);
        userRepository.save(user);
        return getGameDto(user, game);
    }

    private GameDto getGameDto(User user, Game game) {
        GameDto gameDto = GameMapper.INSTANCE.mapToDto(game);
        UserDto current = UserMapper.INSTANCE.mapToDto(user);
        current.getImages().add(user.getId().toString());
        current.getImages().add(user.getId().toString());
        current.getImages().add(user.getId().toString());
        current.getImages().add(user.getId().toString());
        current.getImages().add(user.getId().toString());
        gameDto.setCurrent(current);
        return gameDto;
    }
}
