package model.position;

import model.direction.Direction;

import java.util.Objects;

public class Position {
    private final File file;
    private final Rank rank;

    private Position(final File file, final Rank rank) {
        this.file = file;
        this.rank = rank;
    }

    public static Position of(final File file, final Rank rank) {
        return new Position(file, rank);
    }

    public boolean isAvailablePosition(final Direction direction) {
        return file.canMoveTo(direction) && rank.canMoveTo(direction);
    }

    public Position getNextPosition(final Direction direction) {
        return Position.of(file.moving(direction), rank.moving(direction));
    }

    public File file() {
        return file;
    }

    public Rank rank() {
        return rank;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Position position = (Position) o;
        return file == position.file && rank == position.rank;
    }

    @Override
    public int hashCode() {
        return Objects.hash(file, rank);
    }
}
