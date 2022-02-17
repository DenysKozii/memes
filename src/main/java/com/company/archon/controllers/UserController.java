package com.company.archon.controllers;

import com.company.archon.services.UserService;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import lombok.AllArgsConstructor;

import javax.validation.Valid;

@Controller
@AllArgsConstructor
@RequestMapping
public class UserController {
    private final UserService userService;

    @PostMapping
    public String addUser(Model model, @Valid String username) {
        model.addAttribute("id", userService.addUser(username));
        return "profile";
    }

    @GetMapping
    public String login() {
        return "login";
    }

}
