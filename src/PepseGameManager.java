package pepse.util.src;

import danogl.GameManager;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;

public class PepseGameManager extends GameManager {
    //override initializeGame method in gamemanger
    @Override
    public void initializeGame(ImageReader imageReader, SoundReader soundReader,
                               UserInputListener inputListener, WindowController windowController) {
        super.initializeGame(imageReader, soundReader, inputListener, windowController);
    }
    //main function
    public static void main(String[] args) {
        new PepseGameManager().run();
    }
}
