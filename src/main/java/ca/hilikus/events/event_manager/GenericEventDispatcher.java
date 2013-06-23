package ca.hilikus.events.event_manager;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.EventListener;
import java.util.EventObject;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ca.hilikus.events.event_manager.api.EventDispatcher;

/**
 * Event dispatcher for any kind of event
 * 
 * @param <T> the callback interface
 * 
 * @author hilikus
 */
public class GenericEventDispatcher<T extends EventListener> implements EventDispatcher {

    private static final String CALLBACK_NAME = "update";

    private Map<Class<? extends EventObject>, List<T>> listeners = new HashMap<>();
    private Map<Integer, Method> methodsCache = new HashMap<>();

    private static final Logger log = LoggerFactory.getLogger(GenericEventDispatcher.class);

    /**
     * Adds a listener if it doesn't already exist. The event handler methods must have its first
     * argument as a descendant of {@link EventObject} <br>
     * The event handling methods must be declared directly in the listener and not in one of its
     * super-classes. This is a performance limitation
     * 
     * @param listener the object to notify
     */
    @SuppressWarnings("unchecked")
    public void addListener(T listener) {
	Method[] declaredMethods = listener.getClass().getDeclaredMethods();
	boolean found = false;
	for (Method method : declaredMethods) { // loop only on declared methods to avoid
						// calling
						// empty Adapter methods on event fire
	    if (CALLBACK_NAME.equals(method.getName())) {
		found = true;
		Class<?>[] parameterTypes = method.getParameterTypes();
		if (parameterTypes.length >= 1 && EventObject.class.isAssignableFrom(parameterTypes[0])) { // since
													   // event
													   // methods
													   // have
													   // at
													   // least
													   // the
													   // event
													   // argument
		    // event argument must be the first
		    List<T> eventListeners = listeners.get(parameterTypes[0]);
		    if (eventListeners == null) {
			// first listener for this event type
			eventListeners = new CopyOnWriteArrayList<>();
			listeners.put((Class<? extends EventObject>) parameterTypes[0], eventListeners);
		    }
		    if (eventListeners.contains(listener)) {
			log.debug(
				"[addListener]Trying to register an already registered element: {} for event {}",
				listener, parameterTypes[0].getName());
		    } else {
			eventListeners.add(listener);
			methodsCache.clear();

			log.trace("[addListener] Registered {} for event {}", listener,
				parameterTypes[0].getName());

		    }
		}
	    }
	}
	
	if (!found) {
	    log.warn("[addListener] No callback method found in provided listener {}", listener);
	}

    }



    /**
     * @param listener the object to remove
     */
    public void removeListener(T listener) {
	Method[] declaredMethods = listener.getClass().getDeclaredMethods();
	for (Method method : declaredMethods) { // loop only on declared methods to avoid
						// calling
						// empty Adapter methods
	    if (CALLBACK_NAME.equals(method.getName())) {
		Class<?>[] parameterTypes = method.getParameterTypes();
		if (parameterTypes.length >= 1 && EventObject.class.isAssignableFrom(parameterTypes[0])) { // since
													   // event
													   // methods
													   // have
													   // at
													   // least
													   // the
													   // event
													   // argument
		    // event argument must be the first
		    List<T> eventListeners = listeners.get(parameterTypes[0]);
		    assert eventListeners != null : "Inconsistent state: Trying to remove a listener from a event that has no listeners";
		    eventListeners.remove(listener);
		    methodsCache.clear();
		}
	    }
	}

	log.trace("[removeListener] Unregistered " + listener);

    }

    /**
     * removes all listeners
     */
    public void removeListeners() {
	listeners.clear();
	methodsCache.clear();

	log.debug("[removeListeners] Cleared all listeners in {}", this);

    }

    @Override
    public void fireEvent(EventObject event) {

	log.trace("[fireEvent] Firing event {} coming from {}", event.getClass().getSimpleName(), event
		.getSource().getClass().getSimpleName());

	List<T> listenersToCall = listeners.get(event.getClass());
	Class<?> handlerParam = event.getClass();

	if (listenersToCall == null) {
	    Class<?> superClass = event.getClass().getSuperclass();

	    while (superClass != EventObject.class && superClass != Object.class) {
		listenersToCall = listeners.get(superClass);
		if (listenersToCall != null) {
		    // found a handler for event's super class
		    handlerParam = superClass;
		    break;
		}
		superClass = superClass.getSuperclass();
	    }
	    if (listenersToCall == null) {

		log.debug("[fireEvent] Lost event. No registered listeners for event " + event);

		return;
	    }
	}

	for (T listener : listenersToCall) {

	    int hash = (listener.getClass().hashCode() * 7) ^ (event.getClass().hashCode() * 13);
	    Method callback = methodsCache.get(hash);
	    if (callback == null) {
		// cache miss

		try {
		    callback = listener.getClass().getDeclaredMethod(CALLBACK_NAME, handlerParam);
		    methodsCache.put(hash, callback);

		} catch (NoSuchMethodException exc) {
		    log.error("[fireEvent] ", exc);
		}

	    }

	    assert callback != null;
	    try {
		callback.invoke(listener, event);
	    } catch (IllegalArgumentException exc) {
		log.error("[fireEvent] " + callback, exc);
	    } catch (IllegalAccessException exc) {
		log.error("[fireEvent] Event handler is not accessible: {}", callback, exc);
	    } catch (InvocationTargetException exc) {
		log.error("[fireEvent] Error invoking method " + callback, exc.getCause());
	    }
	}

    }
    
    /**
     * @return the number of listeners currently registered for all events
     */
    public int getListenersCount() {
	Set<Object> countingSet =new HashSet<>();
	for (List<?> listenersList : listeners.values()) {
	    countingSet.addAll(listenersList);
	}
	return countingSet.size();
    }
    
    /**
     * @param eventType the type of event interested in
     * @return the number of listeners for an event type
     */
    public int getListenersCount(Class<? extends EventObject> eventType) {
	return listeners.get(eventType).size();
    }

}
