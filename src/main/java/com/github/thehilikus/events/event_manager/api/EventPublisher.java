package com.github.thehilikus.events.event_manager.api;

/**
 * Contract for classes that generate events
 * 
 * 
 * @author hilikus
 */
public interface EventPublisher {

    /**
     * Injects the dependency to fire events
     * 
     * @param dispatcher object to dispatch events
     */
    public void setEventDispatcher(EventDispatcher dispatcher);

}
