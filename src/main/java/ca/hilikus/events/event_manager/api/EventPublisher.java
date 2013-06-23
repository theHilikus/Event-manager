package ca.hilikus.events.event_manager.api;

/**
 * Interface for classes that generate events
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
