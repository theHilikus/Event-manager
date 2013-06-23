package ca.hilikus.events.event_manager.dummies;

import java.util.EventListener;

/**
 * Event handler used in testing
 * 
 * @author hilikus
 */
public class DummyListener implements EventListener {
    /**
     * @param event dummy event
     */
    public void update(DummyEvent event) {
    }

    /**
     * @param event other dummy event
     */
    public void update(DummyEventTwo event) {
    }
}