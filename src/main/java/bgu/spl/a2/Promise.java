package bgu.spl.a2;

import java.util.ArrayList;
import java.util.List;

/**
 * this class represents a deferred result i.e., an object that eventually will
 * be resolved to hold a result of some operation, the class allows for getting
 * the result once it is available and registering a callback that will be
 * called once the result is available.
 * <p>
 * Note for implementors: you may add methods and synchronize any of the
 * existing methods in this class *BUT* you must be able to explain why the
 * synchronization is needed. In addition, the methods you add can only be
 * private, protected or package protected - in other words, no new public
 * methods
 *
 * @param <T> the result type, <boolean> resolved - initialized ;
 */
public class Promise<T> {
    private List<callback> _callist = new ArrayList<callback>();
    private boolean _isresolve = false;
    private T _result = null;

    /**
     * @return the resolved value if such exists (i.e., if this object has been
     * {@link #resolve(java.lang.Object)}ed
     * @throwsIllegalStateException in the case where this method is called and this object is
     * not yet resolved
     */

    public T get() {// getter of the result
        if (!isResolved())
            throw new IllegalStateException();
        else
            return _result;
    }

    /**
     * @return true if this object has been resolved - i.e., if the method
     * {@link #resolve(java.lang.Object)} has been called on this object
     * before.
     */
    public synchronized boolean isResolved() {
        return _isresolve;
    }

    /**
     * resolve this promise object - from now on, any call to the method
     * {@link #get()} should return the given value
     *
     * Any callbacks that were registered to be notified when this object is
     * resolved via the {@link #subscribe(callback)} method should
     * be executed before this method returns
     *
     * @throws IllegalStateException
     * 			in the case where this object is already resolved
     * @param value
     *            - the value to resolve this promise object with
     */
    public synchronized void resolve(T value) {
        if (isResolved())
            throw new IllegalStateException();
        else {
            _result = value;
            _isresolve = true;
            for (callback temp : _callist) { //call the call method
                temp.call();

            }
            _callist= new ArrayList<callback>();
        }

    }

    /**
     * add a callback to be called when this object is resolved. If while
     * calling this method the object is already resolved - the callback should
     * be called immediately
     * <p>
     * Note that in any case, the given callback should never get called more
     * than once, in addition, in order to avoid memory leaks - once the
     * callback got called, this object should not hold its reference any
     * longer.
     *
     * @param callback the callback to be called when the promise object is resolved
     */
    public synchronized void subscribe(callback callback) {
            if (isResolved())
                callback.call(); // if already resolved - the callback is called immediately
        else
            _callist.add(callback);
    }
}
