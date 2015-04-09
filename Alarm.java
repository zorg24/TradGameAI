import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Created by saslavns on 4/8/2015.
 */
public class Alarm extends Thread {

    private long sleepFor;
    private AtomicBoolean done;

    public Alarm(long time) {
        sleepFor = time;
        done = new AtomicBoolean(false);
    }

    @Override
    public void run() {
        done.set(false);
        try {
            sleep(sleepFor * 1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        done.set(true);
    }

    public boolean isDone() {
        return done.get();
    }
}
