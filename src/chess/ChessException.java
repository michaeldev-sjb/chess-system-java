package chess;

import boardgame.BorderException;

public class ChessException extends BorderException {

    public ChessException(String message) {
        super(message);
    }
}
