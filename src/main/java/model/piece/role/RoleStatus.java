package model.piece.role;

import java.util.Arrays;

public enum RoleStatus {
    BISHOP,
    KING,
    KNIGHT,
    PAWN,
    QUEEN,
    ROOK,
    SQUARE;

    public static RoleStatus from(final String role) {
        return Arrays.stream(values())
                     .filter(roleStatus -> roleStatus.name().equals(role))
                     .findFirst()
                     .orElseThrow(() -> new IllegalArgumentException("해당 문자열과 일치하는 Role이 존재하지 않습니다."));
    }
}
