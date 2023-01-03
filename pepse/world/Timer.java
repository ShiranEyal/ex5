package pepse.util.pepse.world;
import danogl.GameObject;
import danogl.components.ScheduledTask;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Counter;
import danogl.util.Vector2;

public class Timer extends GameObject {
    private static final int MINUTE_LENGTH = 60;
    private final Runnable endingRunnable;
    private Counter timeCounter;
    private final TextRenderable timeDisplay;

    public Timer(Vector2 topLeftCorner, Vector2 dimensions, int timeInSeconds,
                 Runnable runWhenTimeEnds) {
        super(topLeftCorner, dimensions, null);
        timeDisplay = new TextRenderable(secondsToText(timeInSeconds));
        renderer().setRenderable(timeDisplay);

        timeCounter = new Counter(timeInSeconds);
        endingRunnable = runWhenTimeEnds;

        new ScheduledTask(this, 1, true,
                this::updateTimeDisplay);


    }

    private static String secondsToText(int timeInSeconds) {
        String minutesString = Integer.toString(timeInSeconds / MINUTE_LENGTH);
        if (minutesString.length() == 1) {
            minutesString = "0" + minutesString;
        }
        String secondsString = Integer.toString(timeInSeconds % MINUTE_LENGTH);
        if (secondsString.length() == 1) {
            secondsString = "0" + secondsString;
        }
        return String.format("%s : %s", minutesString, secondsString);
    }

    private void updateTimeDisplay() {
        if (timeCounter.value() > 0) {
            timeCounter.decrement();
            timeDisplay.setString(secondsToText(timeCounter.value()));
            return;
        }
        if (endingRunnable != null) {
            endingRunnable.run();
        }
    }
}



