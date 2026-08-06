public class Treasure {
    private int x, y;
    private int value;
    private String type;

    public Treasure(int x, int y, int value, String type) {
        this.x = x;
        this.y = y;
        this.value = value;
        this.type = type;
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getValue() { return value; }
    public String getType() { return type; }
}
