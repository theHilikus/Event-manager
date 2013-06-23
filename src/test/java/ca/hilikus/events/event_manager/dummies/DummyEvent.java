package ca.hilikus.events.event_manager.dummies;

import java.util.EventObject;

/**
 * Event for unit testing
 * 
 */
public class DummyEvent extends EventObject {

    private static final long serialVersionUID = -2828105162401247403L;

    /**
     * Constructor
     */
    public DummyEvent() {
	super(new Object());
    }

}