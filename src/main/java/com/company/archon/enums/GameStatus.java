package com.company.archon.enums;

public enum GameStatus {
    WAITING("waiting"),
    RUNNING("running"),
    COMPLETED("completed");

    public final String value;

    GameStatus(String value) {
        this.value = value;
    }

}
