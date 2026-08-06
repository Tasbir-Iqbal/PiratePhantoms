import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import java.util.ArrayList;
import java.util.List;

public class GameView {
    private Scene scene;
    private Canvas canvasP1Static, canvasP1Dynamic, canvasP2Static, canvasP2Dynamic;
    private static final int CELL_SIZE = 20; // Tile size for 30x30 grid (30*20=600)
    private static final int CANVAS_SIZE = 600; // Main canvas size for balanced layout
    private double animationFrame = 0.0; // For general animations
    private double weatherAnimationOffset = 0.0; // For weather animation effects
    private double lightingAlpha = 1.0; // For dynamic lighting effects
    private static final long gameDuration = 120 * 1_000_000_000L; // Set to 2 minutes in nanoseconds
    private static final long countdownDuration = 10 * 1_000_000_000L; // 10 seconds countdown in nanoseconds
    private Label scoreLabelP1, scoreLabelP2, weatherLabelP1, weatherLabelP2, timeLabel, countdownLabel;
    private String currentWeather = "Day"; // Track current weather for visual effects
    private List<ParticleSystem> particleSystems = new ArrayList<>(); // Manage particle effects
    private WritableImage cachedStaticP1, cachedStaticP2; // Cached static layer for performance
    private boolean mapDirty = true; // Flag to recache static layer when map changes
    private double playerAnimationFrame = 0.0; // For player sprite animation
    private double ambientAnimationFrame = 0.0; // For ambient background animations
    private boolean countdownActive = true; // Flag for countdown before game start
    private long countdownStartTime = 0; // Start time for countdown
    private HBox gameArea; // Store game area for layout switching

    public GameView() {
        // Initialize canvases with defined sizes for two players
        canvasP1Static = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
        canvasP1Dynamic = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
        canvasP2Static = new Canvas(CANVAS_SIZE, CANVAS_SIZE);
        canvasP2Dynamic = new Canvas(CANVAS_SIZE, CANVAS_SIZE);

        // Initialize HUD elements
        scoreLabelP1 = new Label("Player 1 Score: 0");
        scoreLabelP2 = new Label("Player 2 Score: 0");
        weatherLabelP1 = new Label("Weather: Day");
        weatherLabelP2 = new Label("Weather: Day");
        timeLabel = new Label("Time: 02:00");
        countdownLabel = new Label("Starting in: 10");

        // Style labels for better visibility and consistency with a refined pirate theme
        scoreLabelP1.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-background-color: rgba(0,0,0,0.75); -fx-padding: 10; -fx-effect: dropshadow(gaussian, rgba(255,215,0,0.6), 6, 0.5, 1, 1); -fx-font-family: 'Papyrus'; -fx-border-color: #DAA520; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");
        scoreLabelP2.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-background-color: rgba(0,0,0,0.75); -fx-padding: 10; -fx-effect: dropshadow(gaussian, rgba(255,215,0,0.6), 6, 0.5, 1, 1); -fx-font-family: 'Papyrus'; -fx-border-color: #DAA520; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");
        weatherLabelP1.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-background-color: rgba(0,0,0,0.75); -fx-padding: 10; -fx-effect: dropshadow(gaussian, rgba(255,215,0,0.6), 6, 0.5, 1, 1); -fx-font-family: 'Papyrus'; -fx-border-color: #DAA520; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");
        weatherLabelP2.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-background-color: rgba(0,0,0,0.75); -fx-padding: 10; -fx-effect: dropshadow(gaussian, rgba(255,215,0,0.6), 6, 0.5, 1, 1); -fx-font-family: 'Papyrus'; -fx-border-color: #DAA520; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");
        timeLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-background-color: rgba(0,0,0,0.75); -fx-padding: 10; -fx-effect: dropshadow(gaussian, rgba(255,215,0,0.6), 6, 0.5, 1, 1); -fx-font-family: 'Papyrus'; -fx-border-color: #DAA520; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");
        countdownLabel.setStyle("-fx-font-size: 24; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-background-color: rgba(0,0,0,0.8); -fx-padding: 15; -fx-effect: dropshadow(gaussian, rgba(255,215,0,0.7), 8, 0.5, 1, 1); -fx-font-family: 'Papyrus'; -fx-border-color: #DAA520; -fx-border-width: 2; -fx-border-radius: 5; -fx-background-radius: 5;");

        // HUD layout for Player 1 and Player 2 with time in center
        HBox hud = new HBox(30, scoreLabelP1, weatherLabelP1, timeLabel, scoreLabelP2, weatherLabelP2);
        hud.setAlignment(Pos.CENTER);
        hud.setPadding(new Insets(10));
        hud.setStyle("-fx-background-color: linear-gradient(to bottom, #3C2F2F, #2C1F1F); -fx-border-color: #DAA520; -fx-border-width: 2; -fx-border-radius: 5; -fx-background-radius: 5;");

        // Main maps with layered canvases for each player
        StackPane mapPane1 = new StackPane(canvasP1Static, canvasP1Dynamic);
        mapPane1.setStyle("-fx-border-color: #1E90FF; -fx-border-width: 5; -fx-background-color: transparent; -fx-effect: dropshadow(gaussian, #1E90FF, 20, 0.4, 0, 0); -fx-border-radius: 8; -fx-background-radius: 8;");
        StackPane mapPane2 = new StackPane(canvasP2Static, canvasP2Dynamic);
        mapPane2.setStyle("-fx-border-color: #FF4040; -fx-border-width: 5; -fx-background-color: transparent; -fx-effect: dropshadow(gaussian, #FF4040, 20, 0.4, 0, 0); -fx-border-radius: 8; -fx-background-radius: 8;");

        // Main game area (HBox: two player maps side by side)
        gameArea = new HBox(40, mapPane1, mapPane2);
        gameArea.setAlignment(Pos.CENTER);
        gameArea.setPadding(new Insets(20, 0, 20, 0));

        // Add a legend for game elements with pirate-themed styling
        HBox legend = new HBox(12,
                makeLegendCircle(Color.GOLD, "Gold Coin"),
                makeLegendRect(Color.SILVER, "Silver Chest"),
                makeLegendCircle(Color.GOLDENROD, "Golden Skull"),
                makeLegendRect(Color.DARKGRAY, "Obstacle"),
                makeLegendRect(Color.LIMEGREEN, "Speed Boots"),
                makeLegendCircle(Color.CYAN, "Radar"),
                makeLegendRect(Color.MAGENTA, "Potion")
        );
        legend.setAlignment(Pos.CENTER);
        legend.setPadding(new Insets(10, 0, 10, 0));
        legend.setStyle("-fx-background-color: rgba(0,0,0,0.6); -fx-border-color: #DAA520; -fx-border-width: 2; -fx-border-radius: 5; -fx-background-radius: 5;");

        // Use BorderPane for clear separation of components
        BorderPane root = new BorderPane();
        root.setTop(hud);
        // Initially set game area with countdown overlay
        StackPane gameWithCountdown = new StackPane(gameArea, countdownLabel);
        gameWithCountdown.setAlignment(Pos.CENTER);
        root.setCenter(gameWithCountdown);
        root.setBottom(legend);
        root.setStyle("-fx-background-color: linear-gradient(to bottom, #87CEEB, #318CE7);");
        root.setPadding(new Insets(10));

        scene = new Scene(root, CANVAS_SIZE * 2 + 100, CANVAS_SIZE + 150); // Adjusted dimensions for focused layout
        countdownStartTime = System.nanoTime(); // Initialize countdown start time
    }

    private Node makeLegendCircle(Color color, String label) {
        Circle c = new Circle(12, color);
        Label l = new Label(label);
        l.setFont(Font.font("Papyrus", 14));
        l.setTextFill(Color.web("#FFD700"));
        HBox box = new HBox(5, c, l);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    private Node makeLegendRect(Color color, String label) {
        Rectangle r = new Rectangle(24, 16, color);
        Label l = new Label(label);
        l.setFont(Font.font("Papyrus", 14));
        l.setTextFill(Color.web("#FFD700"));
        HBox box = new HBox(5, r, l);
        box.setAlignment(Pos.CENTER_LEFT);
        return box;
    }

    public Scene getScene() {
        return scene;
    }

    public void render(GameMap map, Player p1, Player p2, String weather, long startTime, long currentTime, boolean suddenDeathActive) {
        animationFrame += 0.1; // Increment for general animation effects
        weatherAnimationOffset += 0.05; // Increment for weather animation
        playerAnimationFrame += 0.2; // Increment for player animation
        ambientAnimationFrame += 0.05; // Increment for ambient background animations
        if (animationFrame > 10) animationFrame = 0;
        if (weatherAnimationOffset > 100) weatherAnimationOffset = 0;
        if (playerAnimationFrame > 4) playerAnimationFrame = 0;
        if (ambientAnimationFrame > 20) ambientAnimationFrame = 0;

        // Handle 10-second countdown before game starts
        if (countdownActive) {
            long now = System.nanoTime();
            if (now - countdownStartTime >= countdownDuration) {
                countdownActive = false;
                countdownLabel.setVisible(false);
            } else {
                long remainingCountdown = (countdownDuration - (now - countdownStartTime)) / 1_000_000_000L;
                countdownLabel.setText("Starting in: " + remainingCountdown);
                // Pulse effect for countdown
                double pulse = Math.sin(now / 500_000_000.0) * 0.2 + 0.8;
                countdownLabel.setScaleX(pulse);
                countdownLabel.setScaleY(pulse);
                return; // Skip rendering game content during countdown, but keep game area visible
            }
        }

        // Update HUD elements with combo info and dynamic feedback
        scoreLabelP1.setText("Player 1 Score: " + p1.getScore() + (p1.getComboCount() > 1 ? " (Combo x" + String.format("%.1f", p1.getComboMultiplier()) + ")" : ""));
        scoreLabelP2.setText("Player 2 Score: " + p2.getScore() + (p2.getComboCount() > 1 ? " (Combo x" + String.format("%.1f", p2.getComboMultiplier()) + ")" : ""));
        weatherLabelP1.setText("Weather: " + weather);
        weatherLabelP2.setText("Weather: " + weather);
        currentWeather = weather; // Update current weather for visual effects
        long elapsedTime = (currentTime - startTime) / 1_000_000_000L; // Convert to seconds
        long remainingTime = (gameDuration / 1_000_000_000L) - elapsedTime;
        if (remainingTime < 0) remainingTime = 0; // Prevent negative time display
        long minutes = remainingTime / 60;
        long seconds = remainingTime % 60;
        String timeStr = String.format("%02d:%02d", minutes, seconds);
        if (suddenDeathActive) {
            timeLabel.setText("Sudden Death! Time: " + timeStr);
            timeLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #FF4500; -fx-background-color: rgba(0,0,0,0.75); -fx-padding: 10; -fx-effect: dropshadow(gaussian, rgba(255,69,0,0.6), 6, 0.5, 1, 1); -fx-font-family: 'Papyrus'; -fx-border-color: #FF4500; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");
        } else if (remainingTime < 30) {
            timeLabel.setText("Time: " + timeStr);
            timeLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #FF0000; -fx-background-color: rgba(0,0,0,0.75); -fx-padding: 10; -fx-effect: dropshadow(gaussian, rgba(255,0,0,0.6), 6, 0.5, 1, 1); -fx-font-family: 'Papyrus'; -fx-border-color: #FF0000; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");
        } else {
            timeLabel.setText("Time: " + timeStr);
            timeLabel.setStyle("-fx-font-size: 20; -fx-font-weight: bold; -fx-text-fill: #FFD700; -fx-background-color: rgba(0,0,0,0.75); -fx-padding: 10; -fx-effect: dropshadow(gaussian, rgba(255,215,0,0.6), 6, 0.5, 1, 1); -fx-font-family: 'Papyrus'; -fx-border-color: #DAA520; -fx-border-width: 1; -fx-border-radius: 5; -fx-background-radius: 5;");
        }
        // Update particle systems
        updateParticleSystems();
        // Update lighting based on weather
        updateLighting();
        // Render static layer if map is dirty
        if (mapDirty) {
            // Use biome from GameMap or a default if not available
            String biomeP1 = map.getBiome() != null ? map.getBiome() : "Default";
            String biomeP2 = map.getBiome() != null ? map.getBiome() : "Default";
            cacheStaticLayer(map, canvasP1Static.getGraphicsContext2D(), biomeP1);
            cacheStaticLayer(map, canvasP2Static.getGraphicsContext2D(), biomeP2);
            mapDirty = false;
        }
        // Render dynamic layer
        renderPlayerViewDynamic(canvasP1Dynamic.getGraphicsContext2D(), map, p1);
        renderPlayerViewDynamic(canvasP2Dynamic.getGraphicsContext2D(), map, p2);
    }

    public void flagMapDirty() {
        mapDirty = true;
    }

    private void updateParticleSystems() {
        particleSystems.removeIf(ParticleSystem::isExpired);
        for (ParticleSystem ps : particleSystems) {
            ps.update();
        }
    }

    private void updateLighting() {
        if (currentWeather.equals("Night")) {
            lightingAlpha = 0.45 + Math.sin(weatherAnimationOffset * 0.1) * 0.15; // More pronounced flicker
        } else if (currentWeather.equals("Storm")) {
            lightingAlpha = Math.random() < 0.05 ? 1.0 : 0.5; // More frequent lightning flash
        } else if (currentWeather.equals("Fog")) {
            lightingAlpha = 0.65 + Math.sin(weatherAnimationOffset * 0.05) * 0.2; // Stronger fog density variation
        } else {
            lightingAlpha = 1.0; // Full brightness for Day and Rain
        }
    }

    public void addParticleEffect(int x, int y, String type, Color color) {
        particleSystems.add(new ParticleSystem(x * CELL_SIZE + CELL_SIZE / 2, y * CELL_SIZE + CELL_SIZE / 2, type, color));
    }

    private void cacheStaticLayer(GameMap map, GraphicsContext gc, String biome) {
        gc.clearRect(0, 0, CANVAS_SIZE, CANVAS_SIZE);
        int mapSize = map.getSize();

        // Draw background gradient based on biome with enhanced details
        drawBackgroundImage(gc, biome);

        // Draw subtle biome-specific texture patterns and ambient elements
        drawBiomeTexture(gc, biome);

        // Draw grid lines with a refined pirate map aesthetic
        gc.setStroke(Color.rgb(92, 51, 23, 0.25)); // Dark brown for aged map look
        gc.setLineWidth(0.6);
        for (int i = 0; i <= mapSize; i++) {
            double pos = i * CELL_SIZE;
            gc.strokeLine(pos, 0, pos, CANVAS_SIZE);
            gc.strokeLine(0, pos, CANVAS_SIZE, pos);
        }
    }

    private void drawBackgroundImage(GraphicsContext gc, String biome) {
        Color c1, c2;
        switch (biome) {
            case "Jungle":
                c1 = Color.web("#1b5e20"); c2 = Color.web("#388e3c"); break;
            case "Desert":
                c1 = Color.web("#ffe082"); c2 = Color.web("#ffd54f"); break;
            case "Ruins":
                c1 = Color.web("#757575"); c2 = Color.web("#bdbdbd"); break;
            default:
                c1 = Color.web("#90caf9"); c2 = Color.web("#e3f2fd"); break;
        }
        LinearGradient gradient = new LinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new Stop(0, c1), new Stop(1, c2));
        gc.setFill(gradient);
        gc.fillRect(0, 0, CANVAS_SIZE, CANVAS_SIZE);
    }

    private void drawBiomeTexture(GraphicsContext gc, String biome) {
        if (biome.equals("Jungle")) {
            // Add denser vine-like patterns with foliage
            gc.setStroke(Color.web("#2d5a27", 0.25));
            gc.setLineWidth(1.8);
            for (int i = 0; i < CANVAS_SIZE; i += 50) {
                for (int j = 0; j < CANVAS_SIZE; j += 50) {
                    double offset = Math.sin(i * 0.05 + j * 0.05 + ambientAnimationFrame * 0.02) * 12;
                    gc.strokeLine(i, j, i + offset, j + 25);
                    // Add small leaves
                    gc.setFill(Color.web("#2d5a27", 0.3));
                    gc.fillOval(i + offset / 2, j + 10, 3, 3);
                }
            }
        } else if (biome.equals("Desert")) {
            // Add animated sand dune ripples with heat haze effect
            gc.setStroke(Color.web("#e6b800", 0.2));
            gc.setLineWidth(1.2);
            for (int i = 0; i < CANVAS_SIZE; i += 35) {
                double wave = Math.sin(i * 0.1 + ambientAnimationFrame * 0.03) * 6;
                gc.strokeLine(i, CANVAS_SIZE / 2 + wave, i, CANVAS_SIZE / 2 + wave + 25);
            }
            // Add subtle heat shimmer
            gc.setFill(Color.web("#FFD700", 0.05));
            gc.fillRect(0, CANVAS_SIZE / 3, CANVAS_SIZE, CANVAS_SIZE / 3);
        } else if (biome.equals("Ruins")) {
            // Add detailed cracked stone patterns with moss
            gc.setStroke(Color.web("#5a5a5a", 0.25));
            gc.setLineWidth(0.9);
            for (int i = 0; i < CANVAS_SIZE; i += 45) {
                for (int j = 0; j < CANVAS_SIZE; j += 45) {
                    gc.strokeLine(i, j, i + 12, j + 6);
                    gc.strokeLine(i + 12, j + 6, i + 6, j + 18);
                    // Add moss patches
                    gc.setFill(Color.web("#2d5a27", 0.2));
                    gc.fillOval(i + 5, j + 5, 4, 4);
                }
            }
        } else {
            // Add animated wave patterns for default (ocean-like) with foam
            gc.setStroke(Color.web("#1e90ff", 0.2));
            gc.setLineWidth(1.2);
            for (int i = 0; i < CANVAS_SIZE; i += 25) {
                double wave = Math.sin(i * 0.1 + ambientAnimationFrame * 0.04) * 4;
                gc.strokeLine(i, CANVAS_SIZE / 2 + wave, i, CANVAS_SIZE / 2 + wave + 15);
                // Add foam on waves
                gc.setFill(Color.web("#FFFFFF", 0.1));
                gc.fillOval(i + 5, CANVAS_SIZE / 2 + wave - 2, 3, 2);
            }
        }
    }

    private void renderPlayerViewDynamic(GraphicsContext gc, GameMap map, Player player) {
        gc.clearRect(0, 0, CANVAS_SIZE, CANVAS_SIZE);
        int mapSize = map.getSize();

        // Draw grid contents (dynamic elements)
        for (int x = 0; x < mapSize; x++) {
            for (int y = 0; y < mapSize; y++) {
                int screenX = x * CELL_SIZE;
                int screenY = y * CELL_SIZE;
                int cell = map.getGrid()[x][y];
                if (cell != 0) { // Draw only non-empty cells
                    drawTile(gc, screenX, screenY, cell, x, y, player);
                }
            }
        }

        // Draw player with animation (pirate-themed sprite simulation)
        drawPlayer(gc, player);

        // Draw particle effects
        for (ParticleSystem ps : particleSystems) {
            ps.render(gc);
        }

        // Apply weather overlay effects with dynamic lighting last
        applyWeatherOverlay(gc);
    }

    private void drawPlayer(GraphicsContext gc, Player player) {
        int px = player.getX() * CELL_SIZE;
        int py = player.getY() * CELL_SIZE;
        // Draw shadow with depth
        gc.setFill(Color.BLACK);
        gc.setGlobalAlpha(0.4);
        gc.fillOval(px + 6, py + CELL_SIZE - 10, CELL_SIZE - 12, 8);
        gc.setGlobalAlpha(1.0);

        // Base player color
        Color playerColor = player.getPlayerId() == 1 ? Color.BLUE : Color.RED;
        // Draw player body with animation bounce
        double bounceOffset = Math.sin(playerAnimationFrame) * 2;
        gc.setFill(playerColor);
        gc.fillOval(px + 2, py + 2 + bounceOffset, CELL_SIZE - 4, CELL_SIZE - 4);
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(2);
        gc.strokeOval(px + 2, py + 2 + bounceOffset, CELL_SIZE - 4, CELL_SIZE - 4);

        // Add pirate hat detail with texture
        gc.setFill(Color.web("#3C2F2F")); // Dark brown hat
        gc.fillArc(px + 2, py - 2 + bounceOffset, CELL_SIZE - 4, CELL_SIZE / 2, 0, 180, javafx.scene.shape.ArcType.ROUND);
        gc.setStroke(Color.web("#DAA520")); // Gold trim
        gc.setLineWidth(1.5);
        gc.strokeArc(px + 2, py - 2 + bounceOffset, CELL_SIZE - 4, CELL_SIZE / 2, 0, 180, javafx.scene.shape.ArcType.OPEN);
        // Add hat feather detail
        gc.setFill(player.getPlayerId() == 1 ? Color.web("#FF4500") : Color.web("#00CED1"));
        gc.fillOval(px + CELL_SIZE - 6, py + bounceOffset, 3, 5);

        // Add eye patch or bandana detail with texture
        gc.setFill(Color.BLACK);
        if (player.getPlayerId() == 1) {
            gc.fillOval(px + 8, py + 8 + bounceOffset, 5, 5); // Eye patch
            gc.setStroke(Color.web("#696969", 0.5));
            gc.setLineWidth(0.5);
            gc.strokeOval(px + 8, py + 8 + bounceOffset, 5, 5); // Eye patch strap
        } else {
            gc.fillRect(px + 5, py + 5 + bounceOffset, CELL_SIZE - 10, 3); // Bandana
            gc.setFill(Color.web("#8B0000", 0.5));
            gc.fillOval(px + CELL_SIZE / 2 - 2, py + 6 + bounceOffset, 4, 2); // Bandana knot
        }

        // Add sword or cutlass detail for pirate flair
        gc.setFill(Color.web("#C0C0C0"));
        gc.fillRect(px + CELL_SIZE - 3, py + 5 + bounceOffset, 2, CELL_SIZE - 10);
        gc.setFill(Color.web("#8B4513"));
        gc.fillRect(px + CELL_SIZE - 5, py + 5 + bounceOffset, 4, 2); // Sword hilt
    }

    private void drawTile(GraphicsContext gc, int screenX, int screenY, int cell, int x, int y, Player player) {
        // Draw content by type with enhanced details for eye-catching visuals
        switch (cell) {
            case 1: // Obstacle - Craggy Rock
                gc.setFill(Color.DARKGRAY);
                gc.fillRoundRect(screenX + 3, screenY + 3, CELL_SIZE - 6, CELL_SIZE - 6, 5, 5);
                gc.setStroke(Color.web("#696969", 0.7));
                gc.setLineWidth(1);
                gc.strokeRoundRect(screenX + 3, screenY + 3, CELL_SIZE - 6, CELL_SIZE - 6, 5, 5);
                // Add cracks for detail
                gc.strokeLine(screenX + 5, screenY + 5, screenX + 10, screenY + 10);
                gc.strokeLine(screenX + 10, screenY + 5, screenX + 15, screenY + 10);
                // Add subtle moss or shadow
                gc.setFill(Color.web("#2d5a27", 0.2));
                gc.fillOval(screenX + 4, screenY + CELL_SIZE - 6, 3, 3);
                break;
            case 2: // Gold Coin - Shiny Treasure
                gc.setFill(Color.GOLD);
                gc.fillOval(screenX + 6, screenY + 6, CELL_SIZE - 12, CELL_SIZE - 12);
                gc.setStroke(Color.web("#FFA500", 0.8));
                gc.setLineWidth(1.5);
                gc.strokeOval(screenX + 6, screenY + 6, CELL_SIZE - 12, CELL_SIZE - 12);
                // Add inner glow with animation
                gc.setFill(Color.web("#FFFF00", 0.2 + Math.sin(animationFrame * 0.2) * 0.1));
                gc.fillOval(screenX + 8, screenY + 8, CELL_SIZE - 16, CELL_SIZE - 16);
                // Add coin hole detail
                gc.setFill(Color.web("#CD7F32", 0.5));
                gc.fillOval(screenX + (CELL_SIZE / 2) - 2, screenY + (CELL_SIZE / 2) - 2, 4, 4);
                break;
            case 3: // Silver Chest - Ornate Treasure Chest
                gc.setFill(Color.SILVER);
                gc.fillRect(screenX + 5, screenY + 7, CELL_SIZE - 10, CELL_SIZE - 12);
                gc.setStroke(Color.web("#808080", 0.8));
                gc.setLineWidth(1);
                gc.strokeRect(screenX + 5, screenY + 7, CELL_SIZE - 10, CELL_SIZE - 12);
                // Add gold lock detail with shine
                gc.setFill(Color.GOLD);
                gc.fillOval(screenX + (CELL_SIZE / 2) - 3, screenY + 10, 6, 6);
                gc.setFill(Color.web("#FFFF00", 0.3));
                gc.fillOval(screenX + (CELL_SIZE / 2) - 2, screenY + 11, 2, 2);
                // Add wood texture lines
                gc.setStroke(Color.web("#696969", 0.3));
                gc.strokeLine(screenX + 5, screenY + 9, screenX + CELL_SIZE - 5, screenY + 9);
                break;
            case 4: // Golden Skull - Cursed Relic
                gc.setFill(Color.GOLDENROD);
                gc.fillOval(screenX + 5, screenY + 5, CELL_SIZE - 10, CELL_SIZE - 10);
                gc.setFill(Color.BLACK);
                gc.fillOval(screenX + 10, screenY + 10, 3, 3);
                gc.fillOval(screenX + CELL_SIZE - 13, screenY + 10, 3, 3);
                gc.setStroke(Color.ORANGE);
                gc.setLineWidth(1);
                gc.strokeArc(screenX + 9, screenY + 15, CELL_SIZE - 18, 5, 0, -180, javafx.scene.shape.ArcType.OPEN);
                // Add eerie pulsating glow
                gc.setFill(Color.web("#FF4500", 0.15 + Math.sin(animationFrame * 0.3) * 0.1));
                gc.fillOval(screenX + 4, screenY + 4, CELL_SIZE - 8, CELL_SIZE - 8);
                break;
            case 5: // Quicksand - Treacherous Pit
                gc.setFill(Color.SANDYBROWN);
                gc.fillOval(screenX + 5, screenY + 5, CELL_SIZE - 10, CELL_SIZE - 10);
                // Add swirling center with animation
                gc.setStroke(Color.web("#CD8500", 0.5));
                gc.setLineWidth(1);
                double swirlOffset = animationFrame * 0.2;
                gc.strokeArc(screenX + 8, screenY + 8, CELL_SIZE - 16, CELL_SIZE - 16, swirlOffset * 10, 180, javafx.scene.shape.ArcType.OPEN);
                break;
            case 6: // Fake Treasure - Disguised as Golden Skull
                gc.setFill(Color.GOLDENROD);
                gc.fillOval(screenX + 5, screenY + 5, CELL_SIZE - 10, CELL_SIZE - 10);
                gc.setFill(Color.BLACK);
                gc.fillOval(screenX + 10, screenY + 10, 3, 3);
                gc.fillOval(screenX + CELL_SIZE - 13, screenY + 10, 3, 3);
                gc.setStroke(Color.ORANGE);
                gc.setLineWidth(1);
                gc.strokeArc(screenX + 9, screenY + 15, CELL_SIZE - 18, 5, 0, -180, javafx.scene.shape.ArcType.OPEN);
                // Add eerie pulsating glow to mimic Golden Skull
                gc.setFill(Color.web("#FF4500", 0.15 + Math.sin(animationFrame * 0.3) * 0.1));
                gc.fillOval(screenX + 4, screenY + 4, CELL_SIZE - 8, CELL_SIZE - 8);
                break;
            case 7: // Boulder - Heavy Stone (Temporary Immobilization for 5 seconds)
                gc.setFill(Color.DARKSLATEGRAY);
                gc.fillOval(screenX + 5, screenY + 5, CELL_SIZE - 10, CELL_SIZE - 10);
                // Add shadow detail with depth
                gc.setFill(Color.web("#2F4F4F", 0.5));
                gc.fillOval(screenX + 7, screenY + CELL_SIZE - 7, CELL_SIZE - 14, 3);
                // Add small crack detail
                gc.setStroke(Color.web("#404040", 0.6));
                gc.strokeLine(screenX + 8, screenY + 8, screenX + 12, screenY + 12);
                // Note: Logic for 5-second immobilization handled in game logic, not rendering
                break;
            case 8: // Speed Boots - Swift Footwear
                gc.setFill(Color.LIMEGREEN);
                gc.fillRect(screenX + 7, screenY + 7, CELL_SIZE - 14, CELL_SIZE - 14);
                gc.setStroke(Color.DARKGREEN);
                gc.setLineWidth(1.5);
                gc.strokeLine(screenX + 9, screenY + 15, screenX + CELL_SIZE - 9, screenY + 15);
                // Add speed lines with animation
                double speedOffset = Math.sin(animationFrame * 0.4) * 2;
                gc.setStroke(Color.web("#32CD32", 0.5));
                gc.strokeLine(screenX + 5, screenY + 10 + speedOffset, screenX + 2, screenY + 10 + speedOffset);
                gc.strokeLine(screenX + CELL_SIZE - 5, screenY + 10 - speedOffset, screenX + CELL_SIZE - 2, screenY + 10 - speedOffset);
                break;
            case 9: // Radar - Mystic Compass
                gc.setFill(Color.CYAN);
                gc.fillOval(screenX + 7, screenY + 7, CELL_SIZE - 14, CELL_SIZE - 14);
                gc.setStroke(Color.web("#00B7EB", 0.8));
                gc.setLineWidth(1.5);
                gc.strokeOval(screenX + 5, screenY + 5, CELL_SIZE - 10, CELL_SIZE - 10);
                // Add rotating needle effect with smoother animation
                double angle = animationFrame * 0.5;
                gc.setStroke(Color.RED);
                gc.setLineWidth(1.2);
                gc.strokeLine(screenX + CELL_SIZE / 2, screenY + CELL_SIZE / 2,
                        screenX + CELL_SIZE / 2 + Math.cos(angle) * 6,
                        screenY + CELL_SIZE / 2 + Math.sin(angle) * 6);
                break;
            case 10: // Invincibility Potion - Magic Elixir
                gc.setFill(Color.MAGENTA);
                gc.fillRect(screenX + 8, screenY + 8, CELL_SIZE - 16, CELL_SIZE - 16);
                // Add sparkling effect with multiple sparkles
                gc.setFill(Color.web("#FF00FF", 0.3 + Math.sin(animationFrame * 0.3) * 0.2));
                double sparkleOffset = Math.sin(animationFrame * 0.3) * 2;
                gc.fillOval(screenX + 10 + sparkleOffset, screenY + 10, 3, 3);
                gc.fillOval(screenX + CELL_SIZE - 13 - sparkleOffset, screenY + CELL_SIZE - 13, 3, 3);
                gc.fillOval(screenX + 12 - sparkleOffset, screenY + CELL_SIZE - 10, 2, 2);
                break;
        }
        // Radar highlight for genuine treasures only (exclude Fake Treasures)
        if (player.isRadarActive() && (cell >= 2 && cell <= 4)) { // Only real treasures (2, 3, 4)
            int cellX = screenX / CELL_SIZE;
            int cellY = screenY / CELL_SIZE;
            double distance = Math.sqrt(Math.pow(cellX - player.getX(), 2) + Math.pow(cellY - player.getY(), 2));
            if (distance <= 5) { // Within 5-cell radius
                gc.setFill(Color.web("#FFFF00", 0.2 + Math.sin(animationFrame * 0.4) * 0.1));
                gc.fillRect(screenX, screenY, CELL_SIZE, CELL_SIZE);
                gc.setGlobalAlpha(1.0);
            }
        }
    }

    private void applyWeatherOverlay(GraphicsContext gc) {
        if (currentWeather.equals("Night")) {
            gc.setFill(Color.BLACK);
            gc.setGlobalAlpha(lightingAlpha * 0.35); // Reduced opacity for better visibility
            gc.fillRect(0, 0, CANVAS_SIZE, CANVAS_SIZE);
            gc.setGlobalAlpha(1.0);
            // Add subtle star effects for night with twinkling variation
            gc.setFill(Color.WHITE);
            for (int i = 0; i < 40; i++) { // Increased number of stars
                double starX = Math.random() * CANVAS_SIZE;
                double starY = Math.random() * CANVAS_SIZE / 2.5;
                double twinkle = Math.sin(animationFrame + i * 0.5) * 0.5 + 0.5;
                gc.setGlobalAlpha(twinkle * 0.3 + 0.2);
                double starSize = 1.0 + Math.random() * 1.5;
                gc.fillOval(starX, starY, starSize, starSize);
            }
            // Add subtle moon glow
            gc.setFill(Color.web("#E0E0E0", 0.1));
            gc.fillOval(CANVAS_SIZE - 100, 50, 60, 60);
            gc.setGlobalAlpha(1.0);
        } else if (currentWeather.equals("Rain")) {
            gc.setStroke(Color.BLUE.brighter());
            gc.setLineWidth(0.6); // Slightly thicker lines for visibility
            gc.setGlobalAlpha(0.2); // Slightly increased opacity
            for (int i = 0; i < CANVAS_SIZE; i += 30) { // Reduced spacing for denser rain
                for (int j = 0; j < CANVAS_SIZE; j += 30) {
                    double offset = Math.sin(weatherAnimationOffset + i * 0.05 + j * 0.05) * 6;
                    double length = 8 + Math.random() * 4; // Vary raindrop length
                    gc.strokeLine(i, j + offset, i, j + length + offset);
                }
            }
            // Add subtle puddle ripple effect on ground
            gc.setStroke(Color.web("#1E90FF", 0.1));
            for (int i = 0; i < 5; i++) {
                double rippleX = Math.random() * CANVAS_SIZE;
                double rippleY = CANVAS_SIZE - (Math.random() * 100);
                double rippleSize = 5 + Math.sin(weatherAnimationOffset + i) * 3;
                gc.strokeOval(rippleX, rippleY, rippleSize, rippleSize / 2);
            }
            gc.setGlobalAlpha(1.0);
        } else if (currentWeather.equals("Fog")) {
            gc.setFill(Color.GRAY);
            gc.setGlobalAlpha(lightingAlpha * 0.35); // Adjusted opacity for depth
            gc.fillRect(0, 0, CANVAS_SIZE, CANVAS_SIZE);
            // Add subtle fog wisps with animation
            gc.setFill(Color.web("#D3D3D3", 0.1 + Math.sin(weatherAnimationOffset * 0.1) * 0.05));
            for (int i = 0; i < 3; i++) {
                double wispX = Math.random() * CANVAS_SIZE;
                double wispY = Math.random() * CANVAS_SIZE;
                double wispOffset = Math.sin(weatherAnimationOffset + i * 0.2) * 20;
                gc.fillOval(wispX + wispOffset, wispY, 50, 30);
            }
            gc.setGlobalAlpha(1.0);
        } else if (currentWeather.equals("Storm")) {
            gc.setFill(Color.DARKGRAY);
            gc.setGlobalAlpha(lightingAlpha * 0.45); // Slightly darker for dramatic effect
            gc.fillRect(0, 0, CANVAS_SIZE, CANVAS_SIZE);
            // Lightning flash effect with increased frequency
            if (Math.random() < 0.05) {
                gc.setFill(Color.WHITE);
                gc.setGlobalAlpha(0.6); // Increased intensity for dramatic flash
                gc.fillRect(0, 0, CANVAS_SIZE, CANVAS_SIZE);
                gc.setGlobalAlpha(1.0);
                // Draw multiple lightning bolts for impact
                gc.setStroke(Color.YELLOW.brighter());
                gc.setLineWidth(2.5);
                for (int bolt = 0; bolt < 2; bolt++) {
                    double startX = Math.random() * CANVAS_SIZE;
                    double startY = 0;
                    double endY = CANVAS_SIZE / (2 + Math.random() * 1.5);
                    gc.beginPath();
                    gc.moveTo(startX, startY);
                    for (double y = startY; y < endY; y += 20) {
                        double xOffset = (Math.random() - 0.5) * 35;
                        gc.lineTo(startX + xOffset, y);
                        startX += xOffset;
                    }
                    gc.lineTo(startX, endY);
                    gc.stroke();
                }
            }
            // Rain effect during storm with increased density
            gc.setStroke(Color.BLUE.brighter());
            gc.setLineWidth(0.6);
            gc.setGlobalAlpha(0.25);
            for (int i = 0; i < CANVAS_SIZE; i += 25) {
                for (int j = 0; j < CANVAS_SIZE; j += 25) {
                    double offset = Math.sin(weatherAnimationOffset + i * 0.05 + j * 0.05) * 6;
                    double length = 10 + Math.random() * 5;
                    gc.strokeLine(i, j + offset, i, j + length + offset);
                }
            }
            gc.setGlobalAlpha(1.0);
        }
    }
}

