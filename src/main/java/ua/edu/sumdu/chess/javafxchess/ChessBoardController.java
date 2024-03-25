package ua.edu.sumdu.chess.javafxchess;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.Node;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import lombok.Setter;
import ua.edu.sumdu.chess.javafxchess.backend.Board;
import ua.edu.sumdu.chess.javafxchess.backend.Game;
import ua.edu.sumdu.chess.javafxchess.backend.Position;
import ua.edu.sumdu.chess.javafxchess.backend.moves.Move;
import ua.edu.sumdu.chess.javafxchess.backend.moves.PromotionMove;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.Piece;
import ua.edu.sumdu.chess.javafxchess.backend.pieces.PieceColor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ChessBoardController {
    @FXML
    private BorderPane mainPane;
    @FXML
    private GridPane boardGridPane;
    @FXML
    private VBox mainColumn;
    @FXML
    private HBox topButtonRow;
    @FXML
    private HBox bottomButtonRow;

    private StackPane[][] squares;
    private Position selectedPos;
    private final List<StackPane> changedSquares = new ArrayList<>();
    private List<Move> currentLegalMoves = new ArrayList<>();
    @Setter
    private Game game;

    @FXML
    public void initialize() {
        initializeSquares();
        addMainPaneSizeChangeListeners();
    }

    private void initializeSquares() {
        squares = new StackPane[8][8];
        for (int row = 0; row < 8; row++) {
            for (int col = 0; col < 8; col++) {
                StackPane square = createSquare(row, col);
                boardGridPane.add(square, col, row);
                squares[row][col] = square;
            }
        }
    }

    private StackPane createSquare(int row, int col) {
        StackPane square = new StackPane();
        Rectangle rectangle = new Rectangle();

        if ((row + col) % 2 == 0) {
            rectangle.getStyleClass().add("whiteSquare");
        } else {
            rectangle.getStyleClass().add("blackSquare");
        }

        square.getChildren().add(rectangle);
        return square;
    }

    private void addMainPaneSizeChangeListeners() {
        mainPane.widthProperty().addListener(e -> handleMainPaneSizeChange());
        mainPane.heightProperty().addListener(e -> handleMainPaneSizeChange());
    }

    private void handleMainPaneSizeChange() {
        updateAllSquaresSize();

        Platform.runLater(() -> {
            mainColumn.setMaxWidth(boardGridPane.getWidth());
            mainColumn.setMaxHeight(boardGridPane.getHeight()
                + topButtonRow.getHeight() + bottomButtonRow.getHeight());
        });
    }

    public void setupGameEventsHandlers() {
        game.onMoveMade(e -> drawBoard());
    }

    public void drawBoard() {
        Board board = game.getBoard();

        Position lastMoveFromPos = null, lastMoveToPos = null;
        if (board.getLastMove() != null) {
            lastMoveFromPos = board.getLastMove().getFrom();
            lastMoveToPos = board.getLastMove().getTo();
        }

        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 8; j++) {
                StackPane square = squares[i][j];
                Position position = new Position(i, j);
                Piece piece = board.getPiece(position);
                updateSquare(square, piece, position, lastMoveFromPos, lastMoveToPos);
            }
        }

        updateChangedSquaresSize();
    }

    private void updateSquare(StackPane square, Piece piece, Position position,
                              Position lastMoveFromPos, Position lastMoveToPos) {
        clearSquare(square);
        highlightLastMove(square, position, lastMoveFromPos, lastMoveToPos);
        drawPiece(square, piece);
        changedSquares.add(square);
    }

    private void clearSquare(StackPane square) {
        if (square.getChildren().size() > 1) {
            square.getChildren().remove(1, square.getChildren().size());
        }
    }

    private void highlightLastMove(StackPane square, Position thisPos,
                                   Position fromPos, Position toPos) {
        if (thisPos.equals(fromPos) || thisPos.equals(toPos)) {
            Rectangle lastMoveRectangle = new Rectangle();
            lastMoveRectangle.getStyleClass().add("highlightedSquare");

            square.getChildren().add(lastMoveRectangle);
        }
    }

    private void drawPiece(StackPane square, Piece piece) {
        if (piece != null) {
            ImageView pieceImageView = new ImageView();
            pieceImageView.setFitWidth(1);
            pieceImageView.setFitHeight(1);
            pieceImageView.setImage(new Image(getPieceImagePath(piece)));

            square.getChildren().add(pieceImageView);
        }
    }

    private String getPieceImagePath(Piece piece) {
        String symbol = switch (piece.getType()) {
            case KNIGHT -> "n";
            default -> piece.getType().name().substring(0, 1).toLowerCase();
        };

        return (piece.getColor() == PieceColor.WHITE ? "w" : "b")
            + symbol + ".png";
    }

    @FXML
    public void handleSquareClick(MouseEvent event) {
        Position pos = getPositionFromMouseEvent(event);

        if (selectedPos == null) {
            handleSelection(pos);
        } else {
            Move chosenMove = currentLegalMoves.stream()
                .filter(move -> move.getTo().equals(pos))
                .findFirst()
                .orElse(null);

            if (chosenMove != null) {
                if (chosenMove instanceof PromotionMove) {
                    System.out.println("This is a promotion move");
                }

                handleMove(pos);
            } else {
                Piece selectedPiece = game.getBoard().getPiece(selectedPos);

                clearSelection();

                Piece pieceAtPos = game.getBoard().getPiece(pos);

                if (pieceAtPos != null
                        && pieceAtPos.getColor() == selectedPiece.getColor()) {
                    // select another piece
                    handleSelection(pos);
                }
            }
        }
    }

    private Position getPositionFromMouseEvent(MouseEvent event) {
        int row = (int) (event.getY() / (boardGridPane.getHeight() / 8));
        int col = (int) (event.getX() / (boardGridPane.getWidth() / 8));

        return new Position(row, col);
    }

    private void handleSelection(Position pos) {
        currentLegalMoves = new ArrayList<>(game.getLegalMoves(pos));

        if (!currentLegalMoves.isEmpty()) {
            selectedPos = pos;
            highlightSelectedPosition();

            for (Move move : currentLegalMoves) {
                highlightLegalMove(move);
            }

            updateChangedSquaresSize();
        }
    }

    private void highlightSelectedPosition() {
        StackPane selectedSquare = squares[selectedPos.row()][selectedPos.col()];
        Rectangle selectedSquareHighlight = new Rectangle();
        selectedSquareHighlight.getStyleClass().add("highlightedSquare");

        selectedSquare.getChildren().add(1, selectedSquareHighlight);
        changedSquares.add(selectedSquare);
    }

    private void highlightLegalMove(Move move) {
        int legalMoveRow = move.getTo().row();
        int legalMoveCol = move.getTo().col();

        StackPane square = squares[legalMoveRow][legalMoveCol];
        Circle lastMoveCircle = createLegalMove(move);

        square.getChildren().add(lastMoveCircle);

        changedSquares.add(square);
    }

    private Circle createLegalMove(Move move) {
        int legalMoveRow = move.getTo().row();
        int legalMoveCol = move.getTo().col();

        StackPane square = squares[legalMoveRow][legalMoveCol];
        Circle lastMoveCircle = new Circle();

        if ((square.getChildren().size() > 1
                && !(square.getChildren().get(1) instanceof Rectangle)
                || square.getChildren().size() > 2)) {
            lastMoveCircle.getStyleClass().add("legalMoveWithCapture");
        } else {
            lastMoveCircle.getStyleClass().add("legalMove");
        }

        return lastMoveCircle;
    }

    private void handleMove(Position pos) {
        game.makeMove(selectedPos, pos);
        selectedPos = null;
        currentLegalMoves.clear();
    }

    private void clearSelection() {
        selectedPos = null;
        currentLegalMoves.clear();
        drawBoard();
    }

    private void updateAllSquaresSize() {
        for (int row = 0; row < 8; row++) {
            changedSquares.addAll(Arrays.asList(squares[row]).subList(0, 8));
        }

        updateChangedSquaresSize();
    }

    private void updateChangedSquaresSize() {
        double squareSize = getSquareSize();

        for (StackPane changedSquare : changedSquares) {
            changedSquare.setPrefSize(squareSize, squareSize);
            updateElementsSizeInSquare(changedSquare, squareSize);
        }

        changedSquares.clear();
    }

    private double getSquareSize() {
        double availableWidth = mainPane.getWidth()
            - mainPane.getPadding().getLeft()
            - mainPane.getPadding().getRight();

        double availableHeight = mainPane.getHeight()
            - mainPane.getPadding().getTop()
            - mainPane.getPadding().getBottom()
            - topButtonRow.getHeight()
            - bottomButtonRow.getHeight();

        return Math.min(availableWidth, availableHeight) / 8;
    }

    private void updateElementsSizeInSquare(StackPane square, double squareSize) {
        for (Node child : square.getChildren()) {
            if (child instanceof Rectangle rectangle) {
                rectangle.setWidth(squareSize);
                rectangle.setHeight(squareSize);
            } else if (child instanceof Circle circle) {
                boolean isWithCapture = circle.getStyleClass().stream()
                    .anyMatch(styleClass -> styleClass.equals("legalMoveWithCapture"));

                if (isWithCapture) {
                    double strokeWidth = squareSize * 0.07;
                    circle.setStrokeWidth(strokeWidth);
                    circle.setRadius((squareSize - strokeWidth) / 2);
                } else {
                    circle.setRadius(squareSize / 2);
                }
            } else if (child instanceof ImageView imageView) {
                imageView.setFitWidth(squareSize);
                imageView.setFitHeight(squareSize);
            }
        }
    }
}
