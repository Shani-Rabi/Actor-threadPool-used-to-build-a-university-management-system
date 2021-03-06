package bgu.spl.a2;

import java.util.Collection;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * an abstract class that represents an action that may be executed using the
 * {@link ActorThreadPool}
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add to this class can
 * only be private!!!
 *
 * @param <R> the action result type
 */
public abstract class Action<R> {
    protected String _actionName;
    protected callback _callBack = null;
    protected Promise<R> _promise = new Promise<>();
    protected ActorThreadPool actorPool = null;
    protected String _actorId = null;
    protected PrivateState _privateState;
    protected AtomicInteger numOfResolved;


    /**
     * start handling the action - note that this method is protected, a thread
     * cannot call it directly.
     */
    protected abstract void start();


    /**
     * start/continue handling the action
     * <p>
     * this method should be called in order to start this action
     * or continue its execution in the case where it has been already started.
     * <p>
     * IMPORTANT: this method is package protected, i.e., only classes inside
     * the same package can access it - you should *not* change it to
     * public/private/protected
     */
    /**
     * @return action's name
     */
    public String getActionName() {
        return _actionName;

    }
    /**
     * set action's name
     * @param actionName
     */
    public void setActionName(String actionName) {
        _actionName = actionName;

    }

    /*package*/
    final void handle(ActorThreadPool pool, String actorId, PrivateState actorState) {
        actorPool = pool;
        _actorId = actorId;
        _privateState = actorState;

        if (_callBack == null) //that means this is the first time the action is in the queue
            start();
        else {
            _callBack.call();
        }
    }

    /**
     * add a callback to be executed once *all* the given actions results are
     * resolved
     * <p>
     * Implementors note: make sure that the callback is running only once when
     * all the given actions completed.
     *
     * @param actions
     * @param callback the callback to execute once all the results are resolved
     */
    protected final void then(Collection<? extends Action<?>> actions, callback callback) {
        _callBack = callback;
        numOfResolved = new AtomicInteger(actions.size());
        for (Action action : actions) {
            action.getResult().subscribe(() -> {
                int temp = numOfResolved.decrementAndGet();
                if (temp == 0) // if this is the last action we waited for, then we'll insert the waiting action back to queue
                    actorPool.submit(this, _actorId, _privateState);
            });
        }
    }

    /**
     * resolve the internal result - should be called by the action derivative
     * once it is done.
     *
     * @param result - the action calculated result
     */
    protected final void complete(R result) {
        _promise.resolve(result);

    }

    /**
     * @return action's promise (result)
     */
    public final Promise<R> getResult() {
        return _promise;
    }

    /**
     * send an action to an other actor
     *
     * @param action     the action
     * @param actorId    actor's id
     * @param actorState actor's private state (actor's information)
     * @return promise that will hold the result of the sent action
     */
    public Promise<?> sendMessage(Action<?> action, String actorId, PrivateState actorState) {

        actorPool.submit(action, actorId, actorState);
        return action.getResult();

    }
}


