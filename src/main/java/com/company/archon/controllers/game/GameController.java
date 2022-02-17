package com.company.archon.controllers.game;

import com.company.archon.dto.GameDto;
import com.company.archon.services.GameService;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.AllArgsConstructor;

import javax.validation.Valid;

@Controller
@AllArgsConstructor
@RequestMapping("games")
public class GameController {
    private final GameService gameService;

    @PostMapping("start")
    public String start(Model model) {
        GameDto game = gameService.start();
        model.addAttribute("game", game);
        return "lobby";
    }

    @PostMapping("invite")
    public String invite(@Valid Long id, Model model) {
        GameDto game = gameService.invite(id);
        model.addAttribute("game", game);
        return "lobby";
    }

    @GetMapping("play")
    public String play(Model model) {
        GameDto game = gameService.play();
        model.addAttribute("game", game);
        return "game";
    }

    @PostMapping("select/{image}")
    public String select(@PathVariable String image, Model model) {
        GameDto game = gameService.select(image);
        model.addAttribute("game", game);
        return "vote";
    }

    @PostMapping("vote/{id}")
    public String vote(@PathVariable Long id, Model model) {
        GameDto game = gameService.vote(id);
        model.addAttribute("game", game);
        return "game";
    }
}
