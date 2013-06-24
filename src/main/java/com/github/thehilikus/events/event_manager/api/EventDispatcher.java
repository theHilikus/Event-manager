package com.github.thehilikus.events.event_manager.api;

import java.util.EventObject;

/**
 * Interface used by event publishers to send events
 * 
 * @author hilikus
 */
public interface EventDispatcher {

    /**
     * triggers an event
     * 
     * @param event event object used to resolve which method to call. It will also be passed in the
     *            call
     */
    public void fireEvent(EventObject event);
}
