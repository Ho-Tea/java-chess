package view.dto;

import model.position.File;
import model.position.Rank;

public record ChessPiece(Long id, Rank rank, File file, String symbol) {
}
