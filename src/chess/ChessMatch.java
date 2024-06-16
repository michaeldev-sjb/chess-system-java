package chess;

import boardgame.Board;
import boardgame.Piece;
import boardgame.Position;
import chess.pieces.King;
import chess.pieces.Rook;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ChessMatch {

    private int turn;
    private Color currentPlayer;
    private Board board;
    private boolean check;

    private List<Piece> piecesOnTheBorad = new ArrayList<>();
    private List<Piece> capturedPieces = new ArrayList<>();

    public ChessMatch() {
        board = new Board(8, 8);
        turn = 1;
        currentPlayer = Color.WHITE;
        initialSetup();
    }

    public ChessPiece[][] getPieces() {
        ChessPiece[][] mat = new ChessPiece[board.getRows()][board.getColumns()];
        for (int x = 0; x < board.getRows(); x++) {
            for (int y = 0; y < board.getColumns(); y++) {
                mat[x][y] = (ChessPiece) board.piece(x, y);
            }
        }
        return mat;
    }

    public int getTurn() {
        return turn;
    }

    public Color getCurrentPlayer() {
        return currentPlayer;
    }

    public boolean getCheck(){
        return check;
    }

    public boolean[][] possibleMoves(ChessPosition sourcePosition) {
        Position position = sourcePosition.toPositon();
        validadeSourcePosition(position);
        return board.piece(position).possibleMovies();
    }

    public ChessPiece performChessMove(ChessPosition sourcePosition, ChessPosition targetPosition) {
        Position source = sourcePosition.toPositon();
        Position target = targetPosition.toPositon();
        validadeSourcePosition(source);
        validadeTargetPosition(source, target);
        Piece capturedPiece = makeMove(source, target);

        if (testCheck(currentPlayer)){
            undoMove(source, target, capturedPiece);
            throw new ChessException("You can't put yourself in check");
        }

        check = (testCheck(opponent(currentPlayer))) ? true : false;

        nextTurn();
        return (ChessPiece) capturedPiece;
    }

    private void validadeSourcePosition(Position position) {
        if (!board.thereIsAPiece(position)) {
            throw new ChessException("There is no piece on source position");
        }
        if (currentPlayer != ((ChessPiece) board.piece(position)).getColor()) {
            throw new ChessException("The chosen piece is not yours");
        }
        if (!board.piece(position).isThereAnyPossibleMove()) {
            throw new ChessException("There is no possible moves for the chosen piece");
        }
    }

    private void validadeTargetPosition(Position source, Position target) {
        if (!board.piece(source).possibleMovie(target)) {
            throw new ChessException("The chosen piece can't move to target position");
        }
    }

    private Piece makeMove(Position source, Position target) {
        Piece p = board.removePiece(source);
        Piece capturadPiece = board.removePiece(target);
        board.placePiece(p, target);

        if (capturadPiece != null) {
            piecesOnTheBorad.remove(capturadPiece);
            capturedPieces.add(capturadPiece);
        }

        return capturadPiece;
    }

    private void undoMove(Position source, Position target, Piece capturedPiece) {
        Piece p = board.removePiece(target);
        board.placePiece(p, source);
        if (capturedPiece != null) {
            board.placePiece(capturedPiece, target);
            capturedPieces.remove(capturedPiece);
            piecesOnTheBorad.add(capturedPiece);
        }
    }

    private void nextTurn() {
        turn++;
        currentPlayer = (currentPlayer == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    private Color opponent(Color color) {
        return (color == Color.WHITE) ? Color.BLACK : Color.WHITE;
    }

    private ChessPiece king(Color color) {
        List<Piece> list = piecesOnTheBorad.stream()
                .filter(x -> ((ChessPiece) x).getColor() == color).collect(Collectors.toList());
                for(Piece p : list){
                    if (p instanceof King){
                        return (ChessPiece)p;
                    }
                }
            throw new IllegalStateException("There is no " + color + " King on the board");
    }

    private boolean testCheck(Color color){
        Position kingPosition = king(color).getChessPosition().toPositon();
        List<Piece> opponentPieces = piecesOnTheBorad.stream()
                .filter(x -> ((ChessPiece) x).getColor() == opponent(color)).collect(Collectors.toList());
        for (Piece p : opponentPieces){
            boolean[][] mat = p.possibleMovies();
            if (mat[kingPosition.getRow()][kingPosition.getColumn()]){
                return true;
            }
        }
        return false;
    }

    private void placeNewPiece(char column, int row, ChessPiece piece) {
        board.placePiece(piece, new ChessPosition(column, row).toPositon());
        piecesOnTheBorad.add(piece);
    }

    private void initialSetup() {

        placeNewPiece('c', 1, new Rook(board, Color.WHITE));
        placeNewPiece('c', 2, new Rook(board, Color.WHITE));
        placeNewPiece('d', 2, new Rook(board, Color.WHITE));
        placeNewPiece('e', 2, new Rook(board, Color.WHITE));
        placeNewPiece('e', 1, new Rook(board, Color.WHITE));
        placeNewPiece('d', 1, new King(board, Color.WHITE));

        placeNewPiece('c', 7, new Rook(board, Color.BLACK));
        placeNewPiece('c', 8, new Rook(board, Color.BLACK));
        placeNewPiece('d', 7, new Rook(board, Color.BLACK));
        placeNewPiece('e', 7, new Rook(board, Color.BLACK));
        placeNewPiece('e', 8, new Rook(board, Color.BLACK));
        placeNewPiece('d', 8, new King(board, Color.BLACK));

    }
}
