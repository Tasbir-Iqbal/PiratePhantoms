public class Player {
    private String name;
    private int x, y;
    private int score;
    private int playerId;
    private String avatarPath;
    private double speed = 1.5; // Updated initial speed to 1.5
    private boolean invincible = false;
    private boolean radarActive = false;
    private long radarStartTime = 0;
    private long invincibilityStartTime = 0;
    private long speedEffectStartTime = 0;
    private static final long RADAR_DURATION = 10_000_000_000L; // 10 seconds in nanoseconds
    private static final long INVINCIBILITY_DURATION = 10_000_000_000L; // 10 seconds
    private static final long SPEED_EFFECT_DURATION = 5_000_000_000L; // 5 seconds for speed effects
    private long lastMoveTime = 0;
    private String currentDirection = "idle"; // Tracks direction for animation
    private int comboCount = 0; // Tracks consecutive treasure collections
    private long lastTreasureTime = 0; // Time of last treasure collection
    private static final long COMBO_WINDOW = 5_000_000_000L; // 5 seconds for combo

    public Player(String name, int x, int y, int playerId, String avatarPath) {
        this.name = name;
        this.x = x;
        this.y = y;
        this.score = 0;
        this.playerId = playerId;
        this.avatarPath = avatarPath;
    }

    public String getName() {
        return name;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getScore() {
        return score;
    }

    public void addScore(int points) {
        this.score += points;
    }

    public int getPlayerId() {
        return playerId;
    }

    public String getAvatarPath() {
        return avatarPath;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed, long startTime) {
        this.speed = speed;
        this.speedEffectStartTime = startTime;
    }

    public boolean canMove(long currentTime) {
        // Calculate delay based on speed (higher speed = lower delay)
        long moveDelay = (long) (500_000_000L / speed); // 0.5 seconds base delay adjusted by speed
        return (currentTime - lastMoveTime) >= moveDelay;
    }

    public void updateMoveTime(long currentTime) {
        this.lastMoveTime = currentTime;
    }

    public boolean isInvincible() {
        if (invincible) {
            long currentTime = System.nanoTime();
            if (currentTime - invincibilityStartTime > INVINCIBILITY_DURATION) {
                invincible = false;
            }
        }
        return invincible;
    }

    public void setInvincible(boolean invincible) {
        this.invincible = invincible;
        if (invincible) {
            this.invincibilityStartTime = System.nanoTime();
        }
    }

    public boolean isRadarActive() {
        if (radarActive) {
            long currentTime = System.nanoTime();
            if (currentTime - radarStartTime > RADAR_DURATION) {
                radarActive = false;
            }
        }
        return radarActive;
    }

    public void activateRadar() {
        radarActive = true;
        radarStartTime = System.nanoTime();
    }

    public void updateSpeedDuration(long currentTime) {
        if (currentTime - speedEffectStartTime > SPEED_EFFECT_DURATION && speed != 1.5) {
            speed = 1.5; // Updated reset speed to 1.5
        }
    }

    // Methods for animation and combo system
    public String getCurrentDirection() {
        return currentDirection;
    }

    public void setCurrentDirection(String direction) {
        this.currentDirection = direction;
    }

    public int getComboCount() {
        return comboCount;
    }

    public void updateCombo(long currentTime) {
        if (currentTime - lastTreasureTime <= COMBO_WINDOW) {
            comboCount++;
        } else {
            comboCount = 1; // Reset to 1 for the current collection
        }
        lastTreasureTime = currentTime;
    }

    public void resetCombo() {
        comboCount = 0;
    }

    public double getComboMultiplier() {
        return 1.0 + (comboCount - 1) * 0.2; // 20% bonus per consecutive collection
    }
}
