
import java.util.concurrent.atomic.AtomicBoolean;

public class TTAS implements Lock {

    private final AtomicBoolean state = new AtomicBoolean(false);
    @Override
    public void lock() {
        while (true) { 
            while(state.get()) {/*Wait for the lock to be released be fore you attempt to get it */}
            
            if(!state.getAndSet(true)) return; //the lock was released and you were able to acquire it, yeh!
        }
    }

    /**
     * @brief thread releases the lock
     */
    @Override
    public void unlock() {
        this.state.set(false);
    }

}