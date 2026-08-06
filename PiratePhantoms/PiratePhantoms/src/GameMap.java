import java.util.Random;

public class GameMap {
    private static final int SIZE = 30; // 30x30 grid
    private int[][] grid;
    private String biome;

    public GameMap(String biome) {
        this.biome = biome;
        grid = new int[SIZE][SIZE];
        initializeMap();
    }

    private void initializeMap() {
        Random rand = new Random();
        // Initialize map with some obstacles and treasures for testing
        for (int x = 0; x < SIZE; x++) {
            for (int y = 0; y < SIZE; y++) {
                if (rand.nextDouble() < 0.1) { // 10% chance for obstacle
                    grid[x][y] = 1; // Obstacle
                } else if (rand.nextDouble() < 0.05) { // 5% chance for treasure
                    grid[x][y] = rand.nextInt(3) + 2; // Random treasure (2=coin, 3=chest, 4=skull)
                } else if (rand.nextDouble() < 0.03) { // 3% chance for trap
                    grid[x][y] = rand.nextInt(3) + 5; // Random trap (5=quicksand, 6=fake treasure, 7=boulder)
                } else if (rand.nextDouble() < 0.02) { // 2% chance for power-up
                    grid[x][y] = rand.nextInt(3) + 8; // Random power-up (8=boots, 9=radar, 10=potion)
                } else {
                    grid[x][y] = 0; // Empty
                }
            }
        }
        // Ensure starting positions for players are clear
        grid[0][0] = 0; // Player 1 start
        grid[SIZE-1][SIZE-1] = 0; // Player 2 start
    }

    public void updateDynamicObstacles(String biome) {
        // Placeholder for dynamic obstacle updates if needed
    }

    public int[][] getGrid() {
        return grid;
    }

    public int getSize() {
        return SIZE;
    }

    public String getBiome() {
        return biome;
    }
}
