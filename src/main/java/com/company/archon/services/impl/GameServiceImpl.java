package com.company.archon.services.impl;

import com.company.archon.dto.GameDto;
import com.company.archon.dto.ImageDto;
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
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
@Slf4j
@Transactional
@AllArgsConstructor
public class GameServiceImpl implements GameService {

    private final GameRepository gameRepository;
    private final UserRepository userRepository;
    private final AuthorizationService authorizationService;

    private List<String> questions;

    private static final String DIRECTORY = "src/main/resources/images";

    @EventListener(ApplicationReadyEvent.class)
    public void initialise() {
        questions.add("Ты выучил 19 из 20 билетов и на экзамене попался тот самый");
        questions.add("Когда пришел пьяным на философию и получил 5");
        questions.add("Когда учишься на программиста но не понял какй-то мем о программировании");
        questions.add("Папа настолько круто играет в прятки, что ты не можешь найти его уже 20 лет");
        questions.add("Нельзя просто так взять и стать интернет-мемом");
        questions.add("Когда дропнул продовскую бд");
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
        if (game.getText() == null) {
            game.setText(questions.get((int) (Math.random() * (questions.size() - 1))));
        }
        userRepository.save(user);
        gameRepository.save(game);
        return getGameDto(user, game);
    }

    @Override
    public GameDto select(Integer imageId) {
        Long currentId = authorizationService.getProfileOfCurrent().getId();
        User user = userRepository.findById(currentId)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + currentId + " doesn't exists!"));
        Game game = user.getGame();
        user.setImageId(imageId);
        userRepository.save(user);
        game.setVoted(false);
        gameRepository.save(game);
        return setImages(game);
    }

    private GameDto setImages(Game game) {
        Set<User> users = userRepository.findAllByGame(game);
        GameDto gameDto = GameMapper.INSTANCE.mapToDto(game);
        gameDto.setUsers(new HashSet<>());
        for (User user : users) {
            if (user.getImageId() != null) {
                UserDto userDto = UserMapper.INSTANCE.mapToDto(user);
                userDto.setSelectedImage(readeImage(user.getImageId()));
                gameDto.getUsers().add(userDto);
            }
        }
        return gameDto;
    }

    @Override
    public GameDto vote(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("User with id " + id + " doesn't exists!"));
        Game game = user.getGame();
        user.setScore(user.getScore() + 1);
        user.setImageId(null);
        userRepository.save(user);
        if (!game.isVoted()) {
            game.setText(questions.get((int) (Math.random() * (questions.size() - 1))));
            game.setVoted(true);
        }
        gameRepository.save(game);
        return getGameDto(user, game);
    }

    private GameDto getGameDto(User user, Game game) {
        GameDto gameDto = GameMapper.INSTANCE.mapToDto(game);
        UserDto current = UserMapper.INSTANCE.mapToDto(user);
        List<Integer> ids = IntStream.rangeClosed(0, 6)
                .boxed().collect(Collectors.toList());
        Collections.shuffle(ids);
        int id1 = ids.get(0);
        int id2 = ids.get(2);
        int id3 = ids.get(3);
        int id4 = ids.get(4);
        int id5 = ids.get(5);
        current.getImages().add(new ImageDto(id1, readeImage(id1)));
        current.getImages().add(new ImageDto(id2, readeImage(id2)));
        current.getImages().add(new ImageDto(id3, readeImage(id3)));
        current.getImages().add(new ImageDto(id4, readeImage(id4)));
        current.getImages().add(new ImageDto(id5, readeImage(id5)));
        gameDto.setCurrent(current);
        return gameDto;
    }

    private String readeImage(int id) {
        String path = DIRECTORY + "/meme" + id;
        byte[] fileContent = new byte[0];
        try {
            fileContent = FileUtils.readFileToByteArray(new File(path + ".jpg"));
        } catch (IOException e) {
            try {
                fileContent = FileUtils.readFileToByteArray(new File(path + ".png"));
            } catch (IOException ex) {
                try {
                    fileContent = FileUtils.readFileToByteArray(new File(path + ".jpeg"));
                } catch (IOException exc) {
                    exc.printStackTrace();
                }
            }
        }
        return Base64.getEncoder().encodeToString(fileContent);
    }

}
