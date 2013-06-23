package ca.hilikus.events.event_manager.api;

import java.util.EventObject;

/**
 * Interface used by event publishers
 *
 * @author hilikus
 */
public interface EventDispatcher {

    public void fireEvent(EventObject event);
}
