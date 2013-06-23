package ca.hilikus.events.event_manager;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.testng.Assert.assertEquals;

import java.util.EventListener;

import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import ch.qos.logback.classic.Level;

/**
 * Tests {@link GenericEventDispatcher}
 * 
 * @author hilikus
 */
public class GenericEventDispatcherTest {

    private GenericEventDispatcher<EventListener> TU;

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

    /**
     * Configures tests
     */
    @BeforeMethod
    public void setUp() {
	ch.qos.logback.classic.Logger TULog = (ch.qos.logback.classic.Logger) LoggerFactory
		.getLogger(GenericEventDispatcher.class);
	TULog.setLevel(Level.TRACE);

	TU = new GenericEventDispatcher<>();
    }

    /**
     * tests adding listeners of different types to the dispatcher
     */
    @Test
    public void addListener() {
	TU.addListener(new DummyListener());

	assertEquals(TU.getListenersCount(), 1, "Adding a listener");

	TU.addListener(new DummyListener());

	assertEquals(TU.getListenersCount(), 2, "Adding a second listener failed");

	TU.addListener(new EventListener() {
	    @SuppressWarnings("unused")
	    public void update(DummyEvent event) {
	    }

	});

	assertEquals(TU.getListenersCount(), 3, "Adding a different listener failed");
    }

    /**
     * Tests firing an event
     */
    @Test
    public void fireEvent() {
	DummyListener listenerMock = mock(DummyListener.class);
	TU.addListener(listenerMock);
	
	DummyEvent event = new DummyEvent();
	TU.fireEvent(event);
	
	verify(listenerMock).update(event);
	verifyNoMoreInteractions(listenerMock);
    }

    /**
     * Tests checking for listeners of a particular event type
     */
    @Test
    public void getListenersCountWithType() {
	TU.addListener(new DummyListener());
	TU.addListener(new DummyListener());
	TU.addListener(new EventListener() {
	    @SuppressWarnings("unused")
	    public void update(DummyEvent event) {
	    }

	});

	assertEquals(TU.getListenersCount(DummyEvent.class), 3, "Check DummyEvent listeners");

	assertEquals(TU.getListenersCount(DummyEventTwo.class), 2, "Check DummyEventTwo listeners");
    }

    /**
     * Tests removing a listener
     */
    @Test(dependsOnMethods={"addListener", "getListenersCountWithType"})
    public void removeListener() {
	DummyListener listener = new DummyListener();
	TU.addListener(listener);
	TU.addListener(new EventListener() {
	    @SuppressWarnings("unused")
	    public void update(DummyEvent event) {
	    }

	});
	
	TU.removeListener(listener);
	assertEquals(TU.getListenersCount(), 1, "Check DummyListener was removed");
	assertEquals(TU.getListenersCount(DummyEventTwo.class), 0, "Check removed correct listener");
    }

    /**
     * 
     */
    @Test(dependsOnMethods="addListener")
    public void removeListeners() {
	DummyListener listener = new DummyListener();
	TU.addListener(listener);
	TU.addListener(new EventListener() {
	    @SuppressWarnings("unused")
	    public void update(DummyEvent event) {
	    }

	});
	
	TU.removeListeners();
	assertEquals(TU.getListenersCount(), 0, "Check all listeners were removed");
    }
}
