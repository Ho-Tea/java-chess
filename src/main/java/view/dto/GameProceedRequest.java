package view.dto;

import view.GameCommand;

public record GameProceedRequest(GameCommand gameCommand, String sourcePosition, String targetPosition) {
    private static final String NOT_EXIST = "";

    public GameProceedRequest(GameCommand gameCommand) {
        this(gameCommand, NOT_EXIST, NOT_EXIST);
    }
}
