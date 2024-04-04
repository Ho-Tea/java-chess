package view;

import java.util.Arrays;

public enum GameCommand {
    START("start"), END("end"), MOVE("move"), STATUS("status"), LOAD("load");

    private final String command;

    GameCommand(String command) {
        this.command = command;
    }

    public static GameCommand from(String command) {
        return Arrays.stream(values())
                     .filter(gameCommand -> gameCommand.command.equals(command))
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException("입력하신 Command는 적합하지 않습니다."));
    }
}
