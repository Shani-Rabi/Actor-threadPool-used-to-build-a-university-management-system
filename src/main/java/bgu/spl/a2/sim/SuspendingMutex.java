package bgu.spl.a2.sim;

import bgu.spl.a2.Promise;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * this class is related to {@link Computer}
 * it indicates if a computer is free or not
 * Note: this class can be implemented without any synchronization.
 * However, using synchronization will be accepted as long as the implementation is blocking free.
 */
public class SuspendingMutex {
    private AtomicBoolean theFlag = new AtomicBoolean(true);
    private Queue<Promise<Computer>> theQueue = new ConcurrentLinkedQueue<>();
    private Computer _computer;

    public SuspendingMutex(Computer computer) {
        _computer = computer;
    }

    /**
     * Computer acquisition procedure
     * Note that this procedure is non-blocking and should return immediatly
     * computer's type
     *
     * @return a promise for the requested computer
     */
    public Promise<Computer> down() {
        Promise<Computer> promise = new Promise<>();
        if (theFlag.compareAndSet(true, false)) { //success
            promise.resolve(_computer);
            return promise;

        } else {
            theQueue.add(promise);
            return promise;
        }
    }

    /**
     * Computer return procedure
     * releases a computer which becomes available in the warehouse upon completion
     */
    public void up() {
        theFlag.set(true);
        if (!theQueue.isEmpty()) {
            if (theFlag.compareAndSet(true, false)) { //success
                Promise<Computer> temp = theQueue.poll();
                temp.resolve(_computer);

            }
        }
    }
}