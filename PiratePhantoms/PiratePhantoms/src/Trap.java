public class Trap {
    private int x, y;
    private String type;

    public Trap(int x, int y, String type) {
        this.x = x;
        this.y = y;
        this.type = type;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public String getType() { return type; }
}
