import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("PiratePhantoms: Multiplayer Treasure Hunt");
        primaryStage.setMaximized(true); // Start in maximized mode for full-screen experience
        StartScreen startScreen = new StartScreen(primaryStage);
        primaryStage.setScene(startScreen.getScene());
        primaryStage.setResizable(true); // Allow resizing to adapt to different screens
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
