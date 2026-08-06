import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import java.util.Random;

public class GameController {
    private GameMap gameMap;
    private Player player1, player2;
    private GameView gameView;
    private String weather = "Day";
    private long startTime;
    private long gameDuration = 2 * 60 * 1_000_000_000L; // 2 minutes in nanoseconds
    private long lastWeatherChange = 0;
    private long lastTreasureTimeP1 = 0, lastTreasureTimeP2 = 0;
    private boolean gameEnded = false;
    private Thread gameLoopThread;
    private Thread renderThread;
    private Stage primaryStage;
    private boolean suddenDeathActive = false;
    private String lastWeather = "Day"; // Track previous weather for sound triggers

    public GameController(GameView view, String biome, Player p1, Player p2, Stage stage) {
        this.gameView = view;
        this.gameMap = new GameMap(biome);
        this.player1 = p1;
        this.player2 = p2;
        this.primaryStage = stage;
        this.startTime = System.nanoTime();
        startMultithreadedGame();
    }

    private void startMultithreadedGame() {
        // Game logic thread
        gameLoopThread = new Thread(() -> {
            AnimationTimer gameTimer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if (!gameEnded) {
                        updateGameState(now);
                    }
                }
            };
            gameTimer.start();
        });
        gameLoopThread.setName("GameLoopThread");
        gameLoopThread.start();

        // Rendering thread
        renderThread = new Thread(() -> {
            AnimationTimer renderTimer = new AnimationTimer() {
                @Override
                public void handle(long now) {
                    if (!gameEnded) {
                        gameView.render(gameMap, player1, player2, weather, startTime, now, suddenDeathActive);
                    }
                }
            };
            renderTimer.start();
        });
        renderThread.setName("RenderThread");
        renderThread.start();
    }

    private void updateGameState(long now) {
        // Update weather every 30 seconds
        if (now - lastWeatherChange > 30_000_000_000L) {
            changeWeather();
            lastWeatherChange = now;
            // Placeholder for weather change sound effect
            System.out.println("Sound: Weather changed to " + weather);
        }
        // Update dynamic obstacles
        gameMap.updateDynamicObstacles(gameMap.getBiome());
        // Check game time for victory
        if (now - startTime > gameDuration && !gameEnded) {
            endGame();
        }
        // Update player combo status if needed
        if (player1.getComboCount() > 0 && now - lastTreasureTimeP1 > 5_000_000_000L) {
            player1.resetCombo();
            System.out.println("Sound: Player 1 combo ended");
        }
        if (player2.getComboCount() > 0 && now - lastTreasureTimeP2 > 5_000_000_000L) {
            player2.resetCombo();
            System.out.println("Sound: Player 2 combo ended");
        }
        // Update speed effect durations for power-ups and traps
        player1.updateSpeedDuration(now);
        player2.updateSpeedDuration(now);
    }

    private void changeWeather() {
        String[] weathers = {"Day", "Night", "Rain", "Fog", "Storm"};
        Random rand = new Random();
        lastWeather = weather;
        weather = weathers[rand.nextInt(weathers.length)];
        if (weather.equals("Storm")) {
            player1.setSpeed(1.5, System.nanoTime()); // Updated reset speed to 1.5
            player2.setSpeed(1.5, System.nanoTime()); // Updated reset speed to 1.5
            player1.setInvincible(false);
            player2.setInvincible(false);
            // Placeholder for storm sound effect
            System.out.println("Sound: Storm weather started");
        }
    }

    public void handleTreasureCollection(Player player, int treasureValue, long currentTime) {
        player.updateCombo(currentTime);
        double multiplier = player.getComboMultiplier();
        int points = (int) (treasureValue * multiplier);
        player.addScore(points);
        if (player.getPlayerId() == 1) {
            lastTreasureTimeP1 = currentTime;
        } else {
            lastTreasureTimeP2 = currentTime;
        }
        // Placeholder for treasure collection sound with combo feedback
        System.out.println("Sound: Player " + player.getPlayerId() + " collected treasure worth " + points + " points (Combo x" + String.format("%.1f", multiplier) + ")");
        // Add particle effect for treasure collection
        gameView.addParticleEffect(player.getX(), player.getY(), "treasure", Color.GOLD);
    }

    public void handlePowerUpCollection(Player player, int powerUpType) {
        if (powerUpType == 8) { // Speed Boots
            player.setSpeed(2.5, System.nanoTime()); // Remains at 2.5 as previously updated
            // Placeholder for speed boost sound
            System.out.println("Sound: Player " + player.getPlayerId() + " collected Speed Boots");
        } else if (powerUpType == 9) { // Treasure Radar
            player.activateRadar();
            // Placeholder for radar activation sound
            System.out.println("Sound: Player " + player.getPlayerId() + " activated Treasure Radar");
        } else if (powerUpType == 10) { // Invincibility Potion
            player.setInvincible(true);
            // Placeholder for invincibility sound
            System.out.println("Sound: Player " + player.getPlayerId() + " gained Invincibility");
        }
        // Add particle effect for power-up collection
        gameView.addParticleEffect(player.getX(), player.getY(), "powerup", Color.CYAN);
    }

    public void handleTrapActivation(Player player, int trapType) {
        if (!player.isInvincible()) {
            if (trapType == 5) { // Quicksand
                player.setSpeed(0.5, System.nanoTime()); // Unchanged as requested
                // Placeholder for quicksand trap sound
                System.out.println("Sound: Player " + player.getPlayerId() + " stuck in Quicksand");
            } else if (trapType == 6) { // Fake Treasure
                player.addScore(-15);
                // Placeholder for fake treasure penalty sound
                System.out.println("Sound: Player " + player.getPlayerId() + " lost points to Fake Treasure");
            } else if (trapType == 7) { // Boulder
                player.setSpeed(0.0, System.nanoTime());
                // Placeholder for boulder immobilization sound
                System.out.println("Sound: Player " + player.getPlayerId() + " immobilized by Boulder");
            }
            // Add particle effect for trap activation
            gameView.addParticleEffect(player.getX(), player.getY(), "trap", Color.RED);
        }
    }

    private void endGame() {
        gameEnded = true;
        // Placeholder for game end sound
        System.out.println("Sound: Game ended");
        // Transition to end screen
        EndScreen endScreen = new EndScreen(primaryStage, player1, player2);
        primaryStage.setScene(endScreen.getScene());
    }

    public void movePlayer(Player player, int dx, int dy) {
        int newX = player.getX() + dx;
        int newY = player.getY() + dy;
        if (isValidMove(newX, newY)) {
            player.setX(newX);
            player.setY(newY);
            // Update direction for animation
            if (dx > 0) {
                player.setCurrentDirection("right");
            } else if (dx < 0) {
                player.setCurrentDirection("left");
            } else if (dy > 0) {
                player.setCurrentDirection("down");
            } else if (dy < 0) {
                player.setCurrentDirection("up");
            }
            // Check cell content for treasure, power-up, or trap
            int cell = gameMap.getGrid()[newX][newY];
            long currentTime = System.nanoTime();
            if (cell >= 2 && cell <= 4) { // Treasure
                int value = cell == 2 ? 10 : cell == 3 ? 25 : 50;
                handleTreasureCollection(player, value, currentTime);
                gameMap.getGrid()[newX][newY] = 0; // Remove treasure
            } else if (cell >= 5 && cell <= 7) { // Trap
                handleTrapActivation(player, cell);
            } else if (cell >= 8 && cell <= 10) { // Power-up
                handlePowerUpCollection(player, cell);
                gameMap.getGrid()[newX][newY] = 0; // Remove power-up
            }
        }
    }

    private boolean isValidMove(int x, int y) {
        return x >= 0 && x < gameMap.getSize() && y >= 0 && y < gameMap.getSize() && gameMap.getGrid()[x][y] != 1;
    }
}
