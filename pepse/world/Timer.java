package pepse.util.pepse.world;
import danogl.GameObject;
import danogl.components.ScheduledTask;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Counter;
import danogl.util.Vector2;

public class Timer extends GameObject {
    private static final int MINUTE_LENGTH = 60;
    private static final String TIMER_TEXT = "TIME LEFT: ";
    private final TextRenderable timeDisplay;
    Counter timeCounter;
    private Runnable runWhenTimeEnds;

    public Timer(Vector2 topLeftCorner, Vector2 dimensions, int timeInSeconds,
                 Runnable runWhenTimeEnds) {
        super(topLeftCorner, dimensions, null);
        this.runWhenTimeEnds = runWhenTimeEnds;
        timeDisplay = new TextRenderable(secondsToText(timeInSeconds));
        renderer().setRenderable(timeDisplay);
        timeCounter = new Counter(timeInSeconds);
        new ScheduledTask(this, 1, true,
                this::updateTime);
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

    private void updateTime() {
        if (timeCounter.value() > 0) {
            timeCounter.decrement();
            timeDisplay.setString(TIMER_TEXT + secondsToText(timeCounter.value()));
            return;
        }
        if (runWhenTimeEnds != null) {
            runWhenTimeEnds.run();
        }
    }
}



