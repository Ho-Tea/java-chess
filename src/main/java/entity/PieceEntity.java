package entity;

public record PieceEntity(
        Long id,
        Integer rank,
        Integer file,
        String color,
        String role
) {
    public PieceEntity changedSymbol(final String color, final String role){
        return new PieceEntity(id, rank, file, color, role);
    }
}
