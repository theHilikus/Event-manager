package ca.hilikus.events.event_manager;

import java.util.EventObject;

/**
 * Another Dummy Event for unit testing
 *
 * @author hilikus
 */
public class DummyEventTwo extends EventObject {

    private static final long serialVersionUID = -9069416570482328091L;

    /**
     * Constructor
     */
    public DummyEventTwo() {
        super(new Object());
    }
    
}