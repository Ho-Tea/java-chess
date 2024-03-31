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
        chessBoard.entrySet()
                  .stream()
                  .filter(entry -> entry.getValue().color() == color)
                  .forEach(entry -> faction.put(entry.getKey(), entry.getValue()));
        return faction;
    }

    default boolean possibleCheckMate(final Map<Position, Piece> chessBoard, final Color ourColor, final Color enemyColor) {
        if (checkMate(chessBoard, ourColor)) {
            return true;
        }
        Position kingPosition = positionOfKing(chessBoard, ourColor);
        Map<Position, Piece> enemyFaction = factionOf(chessBoard, enemyColor);
        Map<Position, Piece> ourFaction = factionOf(chessBoard, ourColor);
        ourFaction.remove(kingPosition);
        List<Map.Entry<Position, Piece>> enemies = enemyFaction.entrySet()
                                                               .stream()
                                                               .filter(enemy -> possibleAttacked(chessBoard, kingPosition, enemy))
                                                               .toList();
        boolean canTakeEnemy = canTakeEnemy(chessBoard, ourFaction, enemies);
        boolean canDefence = enemies.stream()
                                    .map(enemy -> enemy.getValue().findRoute(enemy.getKey(), kingPosition))
                                    .allMatch(route -> rotate(route, kingPosition, ourFaction));
        return !(canTakeEnemy || canDefence);
    }

    private boolean rotate(final Route route, final Position kingPosition, final Map<Position, Piece> ourFaction) {
        return route.positions()
                    .stream()
                    .filter(position -> !position.equals(kingPosition))
                    .anyMatch(position -> canDefence(ourFaction, position));
    }

    private boolean canTakeEnemy(final Map<Position, Piece> chessBoard, final Map<Position, Piece> ourFaction, final List<Map.Entry<Position, Piece>> enemies) {
        return enemies.stream()
                      .map(Map.Entry::getKey)
                      .allMatch(enemy -> possibleAttacking(chessBoard, ourFaction, enemy));
    }

    private boolean canDefence(final Map<Position, Piece> ourFaction, final Position destination) {
        try {
            return ourFaction.entrySet()
                             .stream()
                             .map(entry -> entry.getValue().findRoute(entry.getKey(), destination))
                             .findAny()
                             .isPresent();
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    default boolean checkMate(final Map<Position, Piece> chessBoard, final Color color) {
        return chessBoard.entrySet()
                         .stream()
                         .noneMatch(entry -> entry.getValue().roleStatus() == RoleStatus.KING
                                 && entry.getValue().color() == color);
    }

    private boolean possibleAttacking(final Map<Position, Piece> chessBoard, final Map<Position, Piece> ourFaction, final Position enemyPosition) {
        return ourFaction.entrySet()
                         .stream()
                         .anyMatch(ourEntry -> possibleAttacked(chessBoard, enemyPosition, ourEntry));
    }

    default Position positionOfKing(final Map<Position, Piece> chessBoard, final Color color) {
        return chessBoard.entrySet()
                         .stream()
                         .filter(entry -> entry.getValue().roleStatus() == RoleStatus.KING
                                 && entry.getValue().color() == color)
                         .map(Map.Entry::getKey)
                         .findFirst()
                         .orElseThrow(() -> new IllegalArgumentException("해당 색깔의 King이 존재하지 않습니다."));
    }

    default boolean possibleAttacked(final Map<Position, Piece> chessBoard, final Position kingPosition, final Map.Entry<Position, Piece> entry) {
        Position position = entry.getKey();
        Piece piece = entry.getValue();
        try {
            Route route = piece.findRoute(position, kingPosition);
            piece.validateMoving(WayPoints.of(chessBoard, route, kingPosition), new Destination(kingPosition, chessBoard.get(kingPosition)));
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
