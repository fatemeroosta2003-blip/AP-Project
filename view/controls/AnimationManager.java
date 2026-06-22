package view.controls;

import javafx.animation.Animation;
import javafx.animation.Timeline;

import java.util.ArrayList;

public class AnimationManager {
    public static ArrayList<Animation> animations = new ArrayList<>();
    public static ArrayList<Timeline> timers = new ArrayList<>();

    private static ArrayList<Boolean> animationStatus = new ArrayList<>();
    private static ArrayList<Boolean> timerStatus = new ArrayList<>();

    public static void freezeTime() {
        for (Animation animation : animations) {
            if(animation.getStatus() == Animation.Status.RUNNING)
                animationStatus.add(true);
            else animationStatus.add(false);
            animation.stop();
        }
        for (Timeline timer : timers) {
            if(timer.getStatus() == Timeline.Status.RUNNING)
                timerStatus.add(true);
            else timerStatus.add(false);
            timer.stop();
        }
    }

    public static void startTime() {
        for (Animation animation : animations) {
            if(animationStatus.get(animations.indexOf(animation)))
                animation.play();
        }
        for (Timeline timer : timers) {
            if(timerStatus.get(timers.indexOf(timer)))
                timer.play();
        }
        animationStatus = new ArrayList<>();
        timerStatus = new ArrayList<>();
    }
}
