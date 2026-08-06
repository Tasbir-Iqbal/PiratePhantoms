import javafx.scene.image.Image;
import java.util.HashMap;
import java.util.Map;

public class AssetManager {
    private static final String ASSETS_PATH = "../assets/";
    private static Map<String, Image> imageCache = new HashMap<>();

    public static Image loadImage(String path) {
        String fullPath = ASSETS_PATH + path;
        if (!imageCache.containsKey(fullPath)) {
            try {
                imageCache.put(fullPath, new Image("file:" + fullPath));
            } catch (Exception e) {
                System.err.println("Failed to load image: " + fullPath + " - " + e.getMessage());
                imageCache.put(fullPath, null);
            }
        }
        return imageCache.get(fullPath);
    }

    public static Image[] loadAnimationFrames(String spriteType, String direction, int frameCount) {
        Image[] frames = new Image[frameCount];
        for (int i = 0; i < frameCount; i++) {
            String path = "sprites/" + spriteType + "/" + direction + "_" + (i + 1) + ".png";
            frames[i] = loadImage(path);
        }
        return frames;
    }
}
