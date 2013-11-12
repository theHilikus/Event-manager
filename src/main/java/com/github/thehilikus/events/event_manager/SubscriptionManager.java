package com.github.thehilikus.events.event_manager;

import java.util.EventListener;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.thehilikus.events.event_manager.api.EventDispatcher;
import com.github.thehilikus.events.event_manager.api.EventPublisher;


/**
 * Handles mappings between event producers and event consumers
 * 
 * @author hilikus
 */
public class SubscriptionManager {

    private Map<EventPublisher, GenericEventDispatcher<?>> dispatchers = new HashMap<>();
    private static final Logger log = LoggerFactory.getLogger(SubscriptionManager.class);

    /**
     * Binds a listener to a publisher
     * 
     * @param source the event publisher
     * @param listener the event receiver
     */
    public <T extends EventListener> void subscribe(EventPublisher source, T listener) {
	if (source == null || listener == null) {
	    throw new IllegalArgumentException("Parameters cannot be null");
	}
	log.debug("[subscribe] Adding {} --> {}", source.getClass().getName(), listener.getClass().getName());

	GenericEventDispatcher<T> dispatcher = (GenericEventDispatcher<T>) dispatchers.get(source);
	if (dispatcher == null) {
	    log.warn("[subscribe] Registering with a disconnected source");
	    dispatcher = createDispatcher(source);
	}

	dispatcher.addListener(listener);

    }

    private <T extends EventListener> GenericEventDispatcher<T> createDispatcher(EventPublisher source) {
	GenericEventDispatcher<T> dispatcher = new GenericEventDispatcher<>();
	log.trace("[createDispatcher] Creating event dispatcher for {}", source);
	dispatchers.put(source, dispatcher);
	return dispatcher;
    }

    /**
     * Unbinds a listener to a publisher
     * 
     * @param source the event publisher
     * @param listener the event receiver
     */
    public <T extends EventListener> void unsubscribe(EventPublisher source, T listener) {
	log.debug("[unsubscribe] Removing {} --> {}", source.getClass().getName(), listener.getClass().getName());
	GenericEventDispatcher<T> dispatcher = (GenericEventDispatcher<T>) dispatchers.get(source);
	dispatcher.removeListener(listener);
    }

    /**
     * Gets the object used to fire events
     * 
     * @param source the event publisher
     * @return the object used to fire events
     */
    public EventDispatcher getEventDispatcher(EventPublisher source) {
	EventDispatcher ret = dispatchers.get(source);
	if (ret == null) {
	    ret = createDispatcher(source);
	}
	return ret;
    }

    /**
     * Removes all the listeners for a <b>single</b> event generator
     * 
     * @param source the event publisher
     */
    public void unsubscribeAll(EventPublisher source) {
	log.debug("[unsubscribeAll] Cleaning all listeners for {}", source.getClass().getName());
	GenericEventDispatcher<?> dispatcher = dispatchers.get(source);
	if (dispatcher != null) {
	    dispatcher.removeListeners();
	}

    }
    
    /**
     * Removes all the listeners for <b>all</b> the event generators
     */
    public void unsubscribeAll() {
	for (EventPublisher publisher : dispatchers.keySet()) {
	    unsubscribeAll(publisher);
	}
    }

    @Override
    public String toString() {
	// TODO print a nice list of connections
	return super.toString();
    }
    
    /**
     * Gets the number of listeners registered for a publisher
     * @param source the event publisher
     * @return the number of listeners registered for a publisher
     */
    public int getSubscribersCount(EventPublisher source) {
	GenericEventDispatcher<?> dispatcherObject = dispatchers.get(source);
	
	if (dispatcherObject == null) {
	    return 0;
	} else {
	    return dispatcherObject.getListenersCount();
	}
    }
    
}
