package entity;

public record PieceEntity(
        Long id,
        Long chessBoardId,
        Integer rank,
        Integer file,
        String color,
        String role
) {
    public PieceEntity changedSymbol(final String color, final String role) {
        return new PieceEntity(id, chessBoardId, rank, file, color, role);
    }
}
