package com.company.archon.services;


import com.company.archon.dto.GameDto;

public interface GameService {
    GameDto start();

    GameDto invite(Long id);

    GameDto play();

    GameDto select(String image);

    GameDto vote(Long id);
}
