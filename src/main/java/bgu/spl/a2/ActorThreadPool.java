package bgu.spl.a2;

import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.Semaphore;


/**
 * represents an actor thread pool - to understand what this class does please
 * refer to your assignment.
 * <p>
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 */
public class ActorThreadPool {
    private Thread[] threadsarray;
    private VersionMonitor versionMonitor;
    ConcurrentHashMap<String, PrivateState> stateMap;
    ConcurrentHashMap<String, Queue<Action>> actionMap;
    ConcurrentHashMap<String, Semaphore> semaphoreMap;

    /**
     * creates a {@link ActorThreadPool} which has nthreads. Note, threads
     * should not get started until calling to the {@link #start()} method.
     * <p>
     * Implementors note: you may not add other constructors to this class nor
     * you allowed to add any other parameter to this constructor - changing
     * this may cause automatic tests to fail..
     *
     * @param nthreads the number of threads that should be started by this thread
     *                 pool
     */
    public ActorThreadPool(int nthreads) {  //CONSTRUCTOR
        threadsarray = new Thread[nthreads];
        stateMap = new ConcurrentHashMap<String, PrivateState>(); // map for the states of the actor
        actionMap = new ConcurrentHashMap<String, Queue<Action>>(); // map for the actions queue of the actor
        semaphoreMap = new ConcurrentHashMap<String, Semaphore>(); // map for the semaphore of each actor, indicates if someone is using the actor private state(only one can at a time)
        versionMonitor = new VersionMonitor();

        for (int i = 0; i < nthreads; i++) {
            threadsarray[i] = new Thread(() -> { //THE EVENT LOOP FOR THREADS
                int currentVersion;
                while (!Thread.currentThread().isInterrupted()) {
                    currentVersion = versionMonitor.getVersion(); // we saves the current version, in order to check if nothing has changes so the thread should sleep
                    for (Map.Entry<String, Semaphore> entry : semaphoreMap.entrySet()) {
                        if (entry.getValue().tryAcquire()) //this thread try to catch actor
                        {
                            boolean needToNotify = false;
                            String tempcurrId = entry.getKey();
                            Queue<Action> tempQueue = actionMap.get(tempcurrId);
                            PrivateState tempState = stateMap.get(tempcurrId);
                            if (tempQueue.size() > 0) { // if the actor has actions
                                Action tempAction = tempQueue.poll();
                                tempAction.handle(this, tempcurrId, tempState);
                                needToNotify=true;
                            }
                            entry.getValue().release();
                            if (needToNotify)
                                versionMonitor.inc();
                        }
                        if (Thread.currentThread().isInterrupted()) {
                            break;
                        }
                    }
                    if (versionMonitor.getVersion() == currentVersion) {
                        try {
                            versionMonitor.await(currentVersion);
                        } catch (InterruptedException ex) {
                            break;
                        }
                    }
                }
            });

        }


    }

    /**
     * submits an action into an actor to be executed by a thread belongs to
     * this thread pool
     *
     * @param action     the action to execute
     * @param actorId    corresponding actor's id
     * @param actorState actor's private state (actor's information)
     */
    public void submit(Action<?> action, String actorId, PrivateState actorState) {
        actionMap.putIfAbsent(actorId, new ConcurrentLinkedQueue<>());
        semaphoreMap.putIfAbsent(actorId, new Semaphore(1));
        stateMap.putIfAbsent(actorId, actorState);
        actionMap.get(actorId).add(action);
        versionMonitor.inc();

    }

    /**
     * closes the thread pool - this method interrupts all the threads and waits
     * for them to stop - it is returns *only* when there are no live threads in
     * the queue.
     * <p>
     * after calling this method - one should not use the queue anymore.
     *
     * @throws InterruptedException if the thread that shut down the threads is interrupted
     */
    public void shutdown() throws InterruptedException {
        for (Thread thread : threadsarray) {
            thread.interrupt();
            thread.join();
        }
    }

    /**
     * start the threads belongs to this thread pool
     */
    public void start() {
        for (int i = 0; i < threadsarray.length; i++)
            threadsarray[i].start();
    }

    /**
     * getter for actors
     *
     * @return actors
     */

    public Map<String, PrivateState> getActors() {
        return stateMap;
    }

    /**
     * getter for actor's private state
     *
     * @param actorId actor's id
     * @return actor's private state
     */
    public PrivateState getPrivaetState(String actorId) {
        return stateMap.get(actorId);
    }
}