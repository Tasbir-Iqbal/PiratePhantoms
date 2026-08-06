public class PowerUp {
    private int x, y;
    private String type;
    private int duration; // Duration in seconds

    public PowerUp(int x, int y, String type, int duration) {
        this.x = x;
        this.y = y;
        this.type = type;
        this.duration = duration;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public String getType() { return type; }
    public int getDuration() { return duration; }
}
