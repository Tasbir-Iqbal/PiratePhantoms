import javafx.scene.input.KeyEvent;

public class InputHandler {
    private Player player1, player2;
    private GameController controller;

    public InputHandler(Player p1, Player p2, GameController controller) {
        this.player1 = p1;
        this.player2 = p2;
        this.controller = controller;
    }

    public void handleKeyPress(KeyEvent event) {
        long currentTime = System.nanoTime();
        String key = event.getCode().toString();
        // Player 1 controls (WASD)
        if (player1.getSpeed() > 0 && player1.canMove(currentTime)) { // Check if player can move based on speed
            switch (key) {
                case "W":
                    controller.movePlayer(player1, 0, -1);
                    player1.updateMoveTime(currentTime);
                    break;
                case "S":
                    controller.movePlayer(player1, 0, 1);
                    player1.updateMoveTime(currentTime);
                    break;
                case "A":
                    controller.movePlayer(player1, -1, 0);
                    player1.updateMoveTime(currentTime);
                    break;
                case "D":
                    controller.movePlayer(player1, 1, 0);
                    player1.updateMoveTime(currentTime);
                    break;
            }
        }
        // Player 2 controls (Arrow keys)
        if (player2.getSpeed() > 0 && player2.canMove(currentTime)) { // Check if player can move based on speed
            switch (key) {
                case "UP":
                    controller.movePlayer(player2, 0, -1);
                    player2.updateMoveTime(currentTime);
                    break;
                case "DOWN":
                    controller.movePlayer(player2, 0, 1);
                    player2.updateMoveTime(currentTime);
                    break;
                case "LEFT":
                    controller.movePlayer(player2, -1, 0);
                    player2.updateMoveTime(currentTime);
                    break;
                case "RIGHT":
                    controller.movePlayer(player2, 1, 0);
                    player2.updateMoveTime(currentTime);
                    break;
            }
        }
    }
}
