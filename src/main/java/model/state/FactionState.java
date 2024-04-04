package model.state;

import model.direction.Destination;
import model.direction.Route;
import model.direction.WayPoints;
import model.piece.Color;
import model.piece.Piece;
import model.piece.role.RoleStatus;
import model.position.Position;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public sealed interface FactionState permits BlackFaction, WhiteFaction {

    void checkSameFaction(final Piece piece);

    FactionState pass();

    boolean isCheck(final Map<Position, Piece> chessBoard);

    FactionState check();

    boolean defeat(final Map<Position, Piece> chessBoard);

    Color oppositeFaction();

    default Map<Position, Piece> factionOf(final Map<Position, Piece> chessBoard, final Color color) {
        Map<Position, Piece> faction = new HashMap<>();
        chessBoard.keySet()
                  .stream()
                  .filter(position -> chessBoard.get(position).color() == color)
                  .forEach(position -> faction.put(position, chessBoard.get(position)));
        return faction;
    }

    default boolean possibleCheckMate(final Map<Position, Piece> chessBoard, final Color ourColor, final Color enemyColor) {
        if (kingNonExist(chessBoard, ourColor)) {
            return true;
        }
        Position kingPosition = positionOfKing(chessBoard, ourColor);
        Map<Position, Piece> enemyFaction = factionOf(chessBoard, enemyColor);
        Map<Position, Piece> ourFaction = factionOf(chessBoard, ourColor);
        ourFaction.remove(kingPosition);
        List<Position> enemiesPosition = enemyFaction.keySet()
                                                     .stream()
                                                     .filter(enemy -> possibleAttacked(chessBoard, kingPosition, enemy))
                                                     .toList();
        boolean canTakeEnemy = canTakeEnemy(chessBoard, ourFaction, enemiesPosition);
        boolean canDefence = enemiesPosition.stream()
                                            .map(enemy -> chessBoard.get(enemy).findRoute(enemy, kingPosition))
                                            .allMatch(route -> rotate(route, kingPosition, ourFaction));
        return !(canTakeEnemy || canDefence);
    }

    private boolean rotate(final Route route, final Position kingPosition, final Map<Position, Piece> ourFaction) {
        return route.positions()
                    .stream()
                    .filter(position -> !position.equals(kingPosition))
                    .anyMatch(position -> canDefence(ourFaction, position));
    }

    private boolean canTakeEnemy(final Map<Position, Piece> chessBoard, final Map<Position, Piece> ourFaction, final List<Position> enemiesPosition) {
        return enemiesPosition.stream()
                              .allMatch(enemy -> possibleAttacking(chessBoard, ourFaction, enemy));
    }

    private boolean canDefence(final Map<Position, Piece> ourFaction, final Position destination) {
        try {
            return ourFaction.keySet()
                             .stream()
                             .map(position -> ourFaction.get(position).findRoute(position, destination))
                             .findAny()
                             .isPresent();
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    default boolean kingNonExist(final Map<Position, Piece> chessBoard, final Color color) {
        return chessBoard.values()
                         .stream()
                         .noneMatch(piece -> piece.roleStatus() == RoleStatus.KING
                                 && piece.color() == color);
    }

    private boolean possibleAttacking(final Map<Position, Piece> chessBoard, final Map<Position, Piece> ourFaction, final Position enemyPosition) {
        return ourFaction.keySet()
                         .stream()
                         .anyMatch(ourEntry -> possibleAttacked(chessBoard, enemyPosition, ourEntry));
    }

    default Position positionOfKing(final Map<Position, Piece> chessBoard, final Color color) {
        return chessBoard.keySet()
                         .stream()
                         .filter(position -> chessBoard.get(position).roleStatus() == RoleStatus.KING
                                 && chessBoard.get(position).color() == color)
                         .findFirst()
                         .orElseThrow(() -> new IllegalArgumentException("해당 색깔의 King이 존재하지 않습니다."));
    }

    default boolean possibleAttacked(final Map<Position, Piece> chessBoard, final Position kingPosition, final Position position) {
        Piece piece = chessBoard.get(position);
        try {
            Route route = piece.findRoute(position, kingPosition);
            piece.validateMoving(WayPoints.of(chessBoard, route, kingPosition), new Destination(kingPosition, chessBoard.get(kingPosition)));
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
