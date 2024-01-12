package com.example.project2ai;


import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;

public class HelloApplication extends Application {

    private static GridPane gameBoard;
    private static Board board;
    private AnimationTimer gameTimer;
    private MenuBar menuBar;
    private Menu gameMenu;
    private MenuItem newGameOption;
    private BorderPane root;
    private int playerWins;
    private int aiWins;
    private int gamesPlayed;


    public final static class Tile extends Button {

        private final int row;
        private final int col;
        private Mark mark;

        public Tile(int initRow, int initCol, Mark initMark) {
            row = initRow;
            col = initCol;
            mark = initMark;
            initialiseTile();
        }

        private void initialiseTile() {
            this.setOnMouseClicked(e -> {
                if (!board.isCrossTurn()) {
                    board.placeMark(this.row, this.col);
                    this.update();
                }
            });
            this.setStyle("-fx-font-size:70");
            this.setTextAlignment(TextAlignment.CENTER);
            this.setMinSize(150.0, 150.0);
            this.setText("" + this.mark);
        }

        public void update() {
            this.mark = board.getMarkAt(this.row, this.col);
            this.setText("" + mark);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        root = new BorderPane();

        root.setCenter(generateGUI());
        root.setTop(initialiseMenu());

        Scene scene = new Scene(root);
        primaryStage.setTitle("Tic Tac Toe");
        primaryStage.setScene(scene);

        runGameLoop();

        primaryStage.show();
    }

    private static GridPane generateGUI() {

        gameBoard = new GridPane();
        board = new Board();
        gameBoard.setAlignment(Pos.CENTER);

        for (int row = 0; row < board.getWidth(); row++) {
            for (int col = 0; col < board.getWidth(); col++) {
                Tile tile = new Tile(row, col, board.getMarkAt(row, col));
                GridPane.setConstraints(tile, col, row);
                gameBoard.getChildren().add(tile);
            }
        }
        return gameBoard;
    }

    private MenuBar initialiseMenu() {
        menuBar = new MenuBar();
        gameMenu = new Menu("game");
        newGameOption = new MenuItem("New Game");

        gameMenu.getItems().add(newGameOption);
        menuBar.getMenus().add(gameMenu);
        newGameOption.setOnAction(e -> {
            resetGame();
        });
        return menuBar;
    }

    private void runGameLoop() {
        gameTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                if (board.isGameOver()) {
                    endGame();
                } else {
                    if (board.isCrossTurn()) {
                        playAI();
                    }
                }
            }
        };
        gameTimer.start();
    }

    private static void playAI() {
        int[] move = MiniMax.getBestMove(board);
        int row = move[0];
        int col = move[1];
        board.placeMark(row, col);
        for (Node child : gameBoard.getChildren()) {
            if (GridPane.getRowIndex(child) == row
                    && GridPane.getColumnIndex(child) == col) {
                Tile t = (Tile) child;
                t.update();
                return;
            }
        }
    }

    private void resetGame() {
        // Check if 5 games have been played
        if (gamesPlayed < 5) {
            root.setCenter(generateGUI());
            runGameLoop();
        } else {
            // Display the overall winner when 5 games are played
            displayOverallWinner();
        }
    }
    private void endGame() {
        gamesPlayed++;

        // Increment the respective winner's count
        if (board.getWinningMark() == Mark.X) {
            playerWins++;
        } else if (board.getWinningMark() == Mark.O) {
            aiWins++;
        }

        // Check if any player has won 3 out of 5 games
        if (playerWins >= 3 || aiWins >= 3) {
            displayOverallWinner();
        } else {
            // Continue to the next game
            resetGame();
        }
    }

    private void displayOverallWinner() {
        String overallWinner = (playerWins >= 3) ? "Player" : "AI";
        Alert overallWinnerAlert = new Alert(Alert.AlertType.INFORMATION, "",
                new ButtonType("Exit"));

        overallWinnerAlert.setTitle("Overall Winner");
        overallWinnerAlert.setHeaderText(null);
        overallWinnerAlert.setContentText(overallWinner + " is the overall winner!");

        overallWinnerAlert.setOnHidden(e -> {
            overallWinnerAlert.close();
            System.exit(0);
        });

        overallWinnerAlert.show();
    }




}