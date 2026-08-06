import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EndScreen {
    private Scene scene;
    private Stage primaryStage;
    private Player player1, player2;
    private static final String HIGH_SCORE_FILE = "highscores.txt";
    private static final int MAX_HIGH_SCORES = 5;

    public EndScreen(Stage stage, Player p1, Player p2) {
        this.primaryStage = stage;
        this.player1 = p1;
        this.player2 = p2;
        saveHighScores(); // Save scores to file when end screen is created
        createEndScreen();
    }

    public Scene getScene() {
        return scene;
    }

    private void createEndScreen() {
        VBox layout = new VBox(20);
        layout.setAlignment(javafx.geometry.Pos.CENTER);
        layout.setStyle("-fx-background-color: #2C3E50; -fx-padding: 20;");

        // Determine winner
        String winnerText;
        if (player1.getScore() > player2.getScore()) {
            winnerText = player1.getName() + " Wins!";
        } else if (player2.getScore() > player1.getScore()) {
            winnerText = player2.getName() + " Wins!";
        } else {
            winnerText = "It's a Tie!";
        }

        // Title with animation
        Label titleLabel = new Label(winnerText);
        titleLabel.setStyle("-fx-font-size: 36; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 10, 0.5, 5, 5);");
        ScaleTransition titleSt = new ScaleTransition(Duration.millis(2000), titleLabel);
        titleSt.setByX(0.2);
        titleSt.setByY(0.2);
        titleSt.setCycleCount(ScaleTransition.INDEFINITE);
        titleSt.setAutoReverse(true);
        titleSt.play();

        // Score display
        Label scoreLabel = new Label("Final Scores - " + player1.getName() + ": " + player1.getScore() + " | " + player2.getName() + ": " + player2.getScore());
        scoreLabel.setStyle("-fx-font-size: 20; -fx-text-fill: white; -fx-background-color: rgba(0,0,0,0.6); -fx-padding: 10;");

        // High scores display
        Label highScoreTitle = new Label("High Scores:");
        highScoreTitle.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-padding: 5;");
        Label highScoreList = new Label(loadHighScores());
        highScoreList.setStyle("-fx-font-size: 16; -fx-text-fill: white; -fx-background-color: rgba(0,0,0,0.6); -fx-padding: 10; -fx-wrap-text: true;");

        // Winner image or trophy placeholder
        ImageView winnerImage = new ImageView();
        Image trophyImg = AssetManager.loadImage("ui/trophy.png");
        if (trophyImg != null) {
            winnerImage.setImage(trophyImg);
            winnerImage.setFitWidth(100);
            winnerImage.setFitHeight(100);
            FadeTransition trophyFt = new FadeTransition(Duration.millis(1500), winnerImage);
            trophyFt.setFromValue(0.5);
            trophyFt.setToValue(1.0);
            trophyFt.setCycleCount(FadeTransition.INDEFINITE);
            trophyFt.setAutoReverse(true);
            trophyFt.play();
        }

        // Play again and exit buttons with hover effects
        Button playAgainBtn = new Button("Play Again");
        playAgainBtn.setStyle("-fx-font-size: 18; -fx-background-color: #2ECC71; -fx-text-fill: white; -fx-pref-width: 200; -fx-background-radius: 5; -fx-padding: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0.5, 2, 2);");
        playAgainBtn.setOnMouseEntered(e -> playAgainBtn.setStyle("-fx-font-size: 18; -fx-background-color: #27AE60; -fx-text-fill: white; -fx-pref-width: 200; -fx-background-radius: 5; -fx-padding: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 8, 0.5, 3, 3);"));
        playAgainBtn.setOnMouseExited(e -> playAgainBtn.setStyle("-fx-font-size: 18; -fx-background-color: #2ECC71; -fx-text-fill: white; -fx-pref-width: 200; -fx-background-radius: 5; -fx-padding: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0.5, 2, 2);"));
        playAgainBtn.setOnAction(e -> {
            // Placeholder for play again sound
            System.out.println("Sound: Play Again selected");
            StartScreen startScreen = new StartScreen(primaryStage);
            primaryStage.setScene(startScreen.getScene());
        });

        Button exitBtn = new Button("Exit");
        exitBtn.setStyle("-fx-font-size: 18; -fx-background-color: #E74C3C; -fx-text-fill: white; -fx-pref-width: 200; -fx-background-radius: 5; -fx-padding: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0.5, 2, 2);");
        exitBtn.setOnMouseEntered(e -> exitBtn.setStyle("-fx-font-size: 18; -fx-background-color: #C0392B; -fx-text-fill: white; -fx-pref-width: 200; -fx-background-radius: 5; -fx-padding: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 8, 0.5, 3, 3);"));
        exitBtn.setOnMouseExited(e -> exitBtn.setStyle("-fx-font-size: 18; -fx-background-color: #E74C3C; -fx-text-fill: white; -fx-pref-width: 200; -fx-background-radius: 5; -fx-padding: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0.5, 2, 2);"));
        exitBtn.setOnAction(e -> {
            // Placeholder for exit sound
            System.out.println("Sound: Exit game");
            primaryStage.close();
        });

        // Add elements to layout
        layout.getChildren().addAll(titleLabel, scoreLabel, highScoreTitle, highScoreList, winnerImage, playAgainBtn, exitBtn);
        scene = new Scene(layout, 600, 500);
    }

    private void saveHighScores() {
        List<String> highScores = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(HIGH_SCORE_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                highScores.add(line);
            }
        } catch (IOException e) {
            System.err.println("Error reading high scores: " + e.getMessage());
        }

        // Add current game scores
        highScores.add(player1.getName() + ": " + player1.getScore());
        highScores.add(player2.getName() + ": " + player2.getScore());

        // Sort by score (assuming format "Name: Score")
        Collections.sort(highScores, new Comparator<String>() {
            @Override
            public int compare(String s1, String s2) {
                try {
                    int score1 = Integer.parseInt(s1.split(": ")[1]);
                    int score2 = Integer.parseInt(s2.split(": ")[1]);
                    return Integer.compare(score2, score1); // Descending order
                } catch (Exception e) {
                    return 0;
                }
            }
        });

        // Keep only top scores
        if (highScores.size() > MAX_HIGH_SCORES) {
            highScores = highScores.subList(0, MAX_HIGH_SCORES);
        }

        // Write back to file
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGH_SCORE_FILE))) {
            for (String score : highScores) {
                writer.write(score);
                writer.newLine();
            }
        } catch (IOException e) {
            System.err.println("Error saving high scores: " + e.getMessage());
        }
    }

    private String loadHighScores() {
        List<String> highScores = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(HIGH_SCORE_FILE))) {
            String line;
            int count = 0;
            while ((line = reader.readLine()) != null && count < MAX_HIGH_SCORES) {
                highScores.add((count + 1) + ". " + line);
                count++;
            }
        } catch (IOException e) {
            System.err.println("Error loading high scores: " + e.getMessage());
            return "No high scores available.";
        }
        return highScores.isEmpty() ? "No high scores available." : String.join("\n", highScores);
    }
}
