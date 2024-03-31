package model.piece.role;

import model.direction.Direction;
import model.direction.Route;
import model.direction.ShiftPattern;
import model.direction.WayPoints;
import model.piece.Color;
import model.position.Position;
import model.position.Rank;
import model.score.PieceScore;
import model.shift.SingleShift;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public final class Pawn extends Role {
    private static final Rank WHITE_INITIAL_RANK = Rank.TWO;
    private static final Rank BLACK_INITIAL_RANK = Rank.SEVEN;
    private static final int INITIAL_POSSIBLE_MOVEMENT = 2;

    public Pawn(final Color color) {
        super(color, new SingleShift(matchShiftPatternBy(color)));
    }

    private static ShiftPattern matchShiftPatternBy(final Color color) {
        if (color == Color.BLACK) {
            return ShiftPattern.BLACK_PAWN;
        }
        return ShiftPattern.WHITE_PAWN;
    }

    @Override
    public Set<Route> findPossibleAllRoute(final Position position) {
        Set<Route> routes = super.findPossibleAllRoute(position);
        if (position.rank() == WHITE_INITIAL_RANK) {
            routes.add(route(Direction.N, position));
        }
        if (position.rank() == BLACK_INITIAL_RANK) {
            routes.add(route(Direction.S, position));
        }
        return routes;
    }

    private Route route(final Direction direction, final Position position) {
        List<Position> sequentialPositions = new ArrayList<>();
        Position movingPosition = position;
        for (int i = 0; i < INITIAL_POSSIBLE_MOVEMENT; i++) {
            movingPosition = movingPosition.getNextPosition(direction);
            sequentialPositions.add(movingPosition);
        }
        return new Route(direction, sequentialPositions);
    }

    @Override
    public RoleStatus status() {
        return RoleStatus.PAWN;
    }

    @Override
    public void validateMoving(final WayPoints wayPoints, final Role target) {
        super.validateMoving(wayPoints, target);
        Direction direction = wayPoints.direction();
        validateCanTakeOtherPiece(direction, target);
        validateCanMoveForward(direction, target);
    }

    private void validateCanTakeOtherPiece(final Direction direction, final Role target) {
        if (ShiftPattern.isNotForward(direction) && !target.isOccupied()) {
            throw new IllegalArgumentException("해당 방향으로 이동 시 도착지에 상대편의 기물이 존재해야 합니다.");
        }
    }

    private void validateCanMoveForward(final Direction direction, final Role target) {
        if (ShiftPattern.isForward(direction) && target.isOccupied()) {
            throw new IllegalArgumentException("해당 방향으로 이동 시 도착지에 상대편의 기물이 존재할 경우 이동이 불가 합니다.");
        }
    }

    @Override
    public PieceScore score() {
        return PieceScore.PAWN;
    }
}
