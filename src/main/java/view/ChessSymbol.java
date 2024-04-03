package view;

public enum ChessSymbol {
    KING('K', 'k'),
    QUEEN('Q', 'q'),
    PAWN('P', 'p'),
    BISHOP('B', 'b'),
    KNIGHT('N', 'n'),
    ROOK('R', 'r'),
    SQUARE('.', '.');

    private final char blackFactionAbbreviation;
    private final char whiteFactionAbbreviation;

    ChessSymbol(final char blackFactionAbbreviation, final char whiteFactionAbbreviation) {
        this.blackFactionAbbreviation = blackFactionAbbreviation;
        this.whiteFactionAbbreviation = whiteFactionAbbreviation;
    }

    public char getBlackFactionAbbreviation() {
        return blackFactionAbbreviation;
    }

    public char getWhiteFactionAbbreviation() {
        return whiteFactionAbbreviation;
    }
}
