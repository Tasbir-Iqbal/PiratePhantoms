import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.animation.ScaleTransition;
import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.util.Duration;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;

public class StartScreen {
    private Scene scene;
    private Stage primaryStage;

    @FXML
    private TextField player1Name;
    @FXML
    private TextField player2Name;
    @FXML
    private ChoiceBox<String> biomeChoice;
    @FXML
    private Button startButton;
    @FXML
    private Button helpButton;
    @FXML
    private ImageView backgroundImage;
    @FXML
    private ImageView titleImage;

    private static final String PLAYER_CONFIG_FILE = "player_config.txt";
    private static final String SETTINGS_FILE = "game_settings.txt";

    public StartScreen(Stage stage) {
        this.primaryStage = stage;
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("start_screen.fxml"));
            loader.setController(this);
            scene = new Scene(loader.load(), 900, 700); // Default size as fallback
            initializeComponents();
            loadPlayerConfig(); // Load saved player data
            loadGameSettings(); // Load saved game settings
            applyAnimations();
        } catch (IOException e) {
            System.err.println("Failed to load FXML: " + e.getMessage());
            createFallbackUI();
        }
    }

    public Scene getScene() {
        return scene;
    }

    private void initializeComponents() {
        if (biomeChoice != null) {
            biomeChoice.getItems().addAll("Jungle", "Desert", "Ruins");
            biomeChoice.setValue("Jungle");
        }
        if (startButton != null) {
            startButton.setOnAction(e -> startGame());
        }
        if (helpButton != null) {
            helpButton.setOnAction(e -> showHelp());
        }
        // Load background image
        if (backgroundImage != null) {
            // Placeholder for background image; can be updated with AssetManager if available
            // Image bgImage = AssetManager.loadImage("backgrounds/pirate_start_screen.png");
            // if (bgImage != null) backgroundImage.setImage(bgImage);
        }
        // Load title image or placeholder
        if (titleImage != null) {
            // Placeholder for title image; can be updated with AssetManager if available
            // Image titleImg = AssetManager.loadImage("ui/title.png");
            // if (titleImg != null) titleImage.setImage(titleImg);
        }
    }

    private void applyAnimations() {
        if (startButton != null) {
            ScaleTransition st = new ScaleTransition(Duration.millis(1000), startButton);
            st.setByX(0.15);
            st.setByY(0.15);
            st.setCycleCount(ScaleTransition.INDEFINITE);
            st.setAutoReverse(true);
            st.play();
            // Add hover effect with vibrant color change
            startButton.setOnMouseEntered(e -> startButton.setStyle("-fx-font-size: 20; -fx-background-color: #27AE60; -fx-text-fill: white; -fx-pref-width: 320; -fx-background-radius: 8; -fx-padding: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 10, 0.5, 3, 3);"));
            startButton.setOnMouseExited(e -> startButton.setStyle("-fx-font-size: 20; -fx-background-color: #2ECC71; -fx-text-fill: white; -fx-pref-width: 300; -fx-background-radius: 8; -fx-padding: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 6, 0.5, 2, 2);"));
        }
        if (helpButton != null) {
            FadeTransition ft = new FadeTransition(Duration.millis(1500), helpButton);
            ft.setFromValue(1.0);
            ft.setToValue(0.6);
            ft.setCycleCount(FadeTransition.INDEFINITE);
            ft.setAutoReverse(true);
            ft.play();
            // Add hover effect for help button
            helpButton.setOnMouseEntered(e -> helpButton.setStyle("-fx-font-size: 16; -fx-background-color: #F39C12; -fx-text-fill: white; -fx-pref-width: 320; -fx-background-radius: 8; -fx-padding: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.6), 8, 0.5, 3, 3);"));
            helpButton.setOnMouseExited(e -> helpButton.setStyle("-fx-font-size: 16; -fx-background-color: #F1C40F; -fx-text-fill: white; -fx-pref-width: 300; -fx-background-radius: 8; -fx-padding: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.4), 5, 0.5, 2, 2);"));
        }
        if (backgroundImage != null) {
            FadeTransition bgFt = new FadeTransition(Duration.millis(4000), backgroundImage);
            bgFt.setFromValue(0.3);
            bgFt.setToValue(0.5);
            bgFt.setCycleCount(FadeTransition.INDEFINITE);
            bgFt.setAutoReverse(true);
            bgFt.play();
        }
        if (titleImage != null) {
            RotateTransition rt = new RotateTransition(Duration.millis(5000), titleImage);
            rt.setByAngle(8);
            rt.setCycleCount(RotateTransition.INDEFINITE);
            rt.setAutoReverse(true);
            rt.play();
            ScaleTransition titleSt = new ScaleTransition(Duration.millis(2000), titleImage);
            titleSt.setByX(0.1);
            titleSt.setByY(0.1);
            titleSt.setCycleCount(ScaleTransition.INDEFINITE);
            titleSt.setAutoReverse(true);
            titleSt.play();
        }
    }

    private void createFallbackUI() {
        VBox layout = new VBox(10);
        layout.setAlignment(javafx.geometry.Pos.CENTER);
        player1Name = new TextField("Player 1");
        player2Name = new TextField("Player 2");
        biomeChoice = new ChoiceBox<>();
        biomeChoice.getItems().addAll("Jungle", "Desert", "Ruins");
        biomeChoice.setValue("Jungle");
        Button startBtn = new Button("Start Game");
        startBtn.setOnAction(e -> startGame());
        Button helpBtn = new Button("Help / Instructions");
        helpBtn.setOnAction(e -> showHelp());
        layout.getChildren().addAll(player1Name, player2Name, biomeChoice, startBtn, helpBtn);
        scene = new Scene(layout, 900, 700);
        initializeComponents();
    }

    private void startGame() {
        Player p1 = new Player(player1Name.getText(), 0, 0, 1, null);
        Player p2 = new Player(player2Name.getText(), 29, 29, 2, null);
        GameView gameView = new GameView();
        GameController controller = new GameController(gameView, biomeChoice.getValue(), p1, p2, primaryStage);
        primaryStage.setScene(gameView.getScene());
        gameView.getScene().setOnKeyPressed(event -> {
            InputHandler handler = new InputHandler(p1, p2, controller);
            handler.handleKeyPress(event);
        });
        savePlayerConfig(); // Save player data for next session
        saveGameSettings(); // Save game settings like biome
        System.out.println("Sound: Game started");
    }

    private void showHelp() {
        VBox helpLayout = new VBox(10);
        helpLayout.setAlignment(javafx.geometry.Pos.CENTER);
        helpLayout.setStyle("-fx-background-color: linear-gradient(to bottom, #87CEEB, #318CE7); -fx-padding: 20; -fx-background-radius: 10; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0.5, 3, 3);");
        javafx.scene.control.Label helpText = new javafx.scene.control.Label(
                "How to Play PiratePhantoms: Multiplayer Treasure Hunt\n\n" +
                        "1. Game Objective:\n" +
                        "   - Compete against another player to collect the most treasures within a 2-minute time limit.\n" +
                        "   - Treasures are scattered across a 30x30 grid map in various biomes (Jungle, Desert, Ruins).\n\n" +
                        "2. Scoring System:\n" +
                        "   - Gold Coins: 10 points each (common, small reward).\n" +
                        "   - Silver Chests: 25 points each (less common, medium reward).\n" +
                        "   - Golden Skulls: 50 points each (rare, high reward).\n" +
                        "   - Combo Bonus: Collect treasures consecutively within 5 seconds to build a combo multiplier (adds 20% bonus per consecutive collection).\n\n" +
                        "3. Controls:\n" +
                        "   - Player 1: Use WASD keys to move (W: Up, S: Down, A: Left, D: Right).\n" +
                        "   - Player 2: Use Arrow keys to move (Up, Down, Left, Right).\n" +
                        "   - Movement speed can be affected by power-ups or traps (see below).\n\n" +
                        "4. Challenges and Traps:\n" +
                        "   - Obstacles: Impassable rocks or barriers that block paths; plan your route carefully.\n" +
                        "   - Quicksand: Slows movement speed to half (0.5) for 5 seconds, making you vulnerable to being overtaken.\n" +
                        "   - Fake Treasures: Disguised as Golden Skulls, deduct 15 points when collected; beware of deceptive appearances.\n" +
                        "   - Boulders: Immobilize you completely (speed 0) for 5 seconds, halting all movement.\n\n" +
                        "5. Power-Ups:\n" +
                        "   - Speed Boots: Increase movement speed to 2.5 for 5 seconds, allowing faster navigation and treasure collection.\n" +
                        "   - Treasure Radar: Highlights nearby treasures (within a 5-cell radius) for 10 seconds; does not reveal Fake Treasures.\n" +
                        "   - Invincibility Potion: Grants immunity to all traps for 10 seconds, letting you pass through Quicksand or Boulders unharmed.\n\n" +
                        "6. Weather Effects:\n" +
                        "   - Weather changes every 30 seconds, affecting visibility and gameplay:\n" +
                        "     - Day: Normal visibility, no effects.\n" +
                        "     - Night: Reduced visibility with a dark overlay.\n" +
                        "     - Rain: Slight visual obstruction with rain effects.\n" +
                        "     - Fog: Dense overlay reducing map clarity.\n" +
                        "     - Storm: Darkened screen with lightning flashes; resets speed to normal (1.5) and disables active power-ups.\n\n" +
                        "7. Winning the Game:\n" +
                        "   - The game ends after 2 minutes, and the player with the highest score wins.\n" +
                        "   - If scores are equal, the match results in a tie; scores are recorded in the high score list.\n" +
                        "   - After the game, choose 'Play Again' to start a new match or 'Exit' to close the game.\n\n" +
                        "8. Tips for Success:\n" +
                        "   - Plan your path to avoid traps or use Invincibility strategically to bypass them.\n" +
                        "   - Time Speed Boots to reach high-value treasures like Golden Skulls quickly.\n" +
                        "   - Build combos by collecting treasures rapidly to maximize points.\n" +
                        "   - Watch the timer and weather changes to adapt your strategy in the final moments.\n\n" +
                        "Embark on your pirate adventure and claim the most treasure to become the ultimate Phantom Pirate!"
        );
        helpText.setWrapText(true);
        helpText.setStyle("-fx-font-size: 14; -fx-text-fill: #2C3E50; -fx-background-color: rgba(255,255,255,0.8); -fx-padding: 15; -fx-background-radius: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0.5, 2, 2);");
        Button closeBtn = new Button("Close");
        closeBtn.setStyle("-fx-font-size: 16; -fx-background-color: #2ECC71; -fx-text-fill: white; -fx-pref-width: 200; -fx-background-radius: 5; -fx-padding: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0.5, 2, 2);");
        closeBtn.setOnMouseEntered(e -> closeBtn.setStyle("-fx-font-size: 16; -fx-background-color: #27AE60; -fx-text-fill: white; -fx-pref-width: 200; -fx-background-radius: 5; -fx-padding: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.5), 8, 0.5, 3, 3);"));
        closeBtn.setOnMouseExited(e -> closeBtn.setStyle("-fx-font-size: 16; -fx-background-color: #2ECC71; -fx-text-fill: white; -fx-pref-width: 200; -fx-background-radius: 5; -fx-padding: 8; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 5, 0.5, 2, 2);"));
        closeBtn.setOnAction(e -> primaryStage.setScene(scene));
        helpLayout.getChildren().addAll(helpText, closeBtn);
        Scene helpScene = new Scene(helpLayout, 850, 650);
        primaryStage.setScene(helpScene);
    }

    private void savePlayerConfig() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(PLAYER_CONFIG_FILE))) {
            writer.write("Player1Name=" + player1Name.getText());
            writer.newLine();
            writer.write("Player2Name=" + player2Name.getText());
        } catch (IOException e) {
            System.err.println("Error saving player config: " + e.getMessage());
        }
    }

    private void loadPlayerConfig() {
        try (BufferedReader reader = new BufferedReader(new FileReader(PLAYER_CONFIG_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=", 2); // Split with limit to handle empty values
                if (parts.length == 2) { // Ensure there are two parts after split
                    String key = parts[0];
                    String value = parts[1];
                    if (key.equals("Player1Name") && player1Name != null) {
                        player1Name.setText(value);
                    } else if (key.equals("Player2Name") && player2Name != null) {
                        player2Name.setText(value);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading player config: " + e.getMessage());
            // Default values are already set in FXML or initializeComponents, so no action needed
        }
    }

    private void saveGameSettings() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(SETTINGS_FILE))) {
            writer.write("LastBiome=" + biomeChoice.getValue());
        } catch (IOException e) {
            System.err.println("Error saving game settings: " + e.getMessage());
        }
    }

    private void loadGameSettings() {
        try (BufferedReader reader = new BufferedReader(new FileReader(SETTINGS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("=", 2); // Split with limit to handle empty values
                if (parts.length == 2 && parts[0].equals("LastBiome") && biomeChoice != null) {
                    String biome = parts[1];
                    if (biomeChoice.getItems().contains(biome)) {
                        biomeChoice.setValue(biome);
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error loading game settings: " + e.getMessage());
            // Default value "Jungle" is already set, so no action needed
        }
    }
}
