import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ParticleSystem {
    private List<Particle> particles;
    private int x, y;
    private String type;
    private Color color;
    private long creationTime;
    private static final long LIFETIME = 2_000_000_000L; // 2 seconds in nanoseconds
    private static final int MAX_PARTICLES = 10; // Reduced for performance

    public ParticleSystem(int x, int y, String type, Color color) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.color = color;
        this.creationTime = System.nanoTime();
        this.particles = new ArrayList<>();
        initializeParticles();
    }

    private void initializeParticles() {
        Random rand = new Random();
        for (int i = 0; i < MAX_PARTICLES; i++) {
            double vx = (rand.nextDouble() - 0.5) * 2; // Velocity x
            double vy = (rand.nextDouble() - 0.5) * 2; // Velocity y
            double size = rand.nextDouble() * 3 + 1; // Size between 1 and 4
            long lifespan = (long) (rand.nextDouble() * LIFETIME * 0.8 + LIFETIME * 0.2); // Random lifespan
            particles.add(new Particle(x, y, vx, vy, size, lifespan));
        }
    }

    public void update() {
        long currentTime = System.nanoTime();
        for (Particle p : particles) {
            if (currentTime - creationTime < p.lifespan) {
                p.x += p.vx;
                p.y += p.vy;
            }
        }
    }

    public void render(GraphicsContext gc) {
        long currentTime = System.nanoTime();
        gc.setFill(color);
        for (Particle p : particles) {
            if (currentTime - creationTime < p.lifespan) {
                double alpha = 1.0 - ((double)(currentTime - creationTime) / p.lifespan);
                gc.setGlobalAlpha(alpha * 0.7);
                gc.fillOval(p.x, p.y, p.size, p.size);
            }
        }
        gc.setGlobalAlpha(1.0);
    }

    public boolean isExpired() {
        return System.nanoTime() - creationTime > LIFETIME;
    }

    private static class Particle {
        double x, y;
        double vx, vy;
        double size;
        long lifespan;

        Particle(double x, double y, double vx, double vy, double size, long lifespan) {
            this.x = x;
            this.y = y;
            this.vx = vx;
            this.vy = vy;
            this.size = size;
            this.lifespan = lifespan;
        }
    }
}
