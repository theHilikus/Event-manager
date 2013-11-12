package com.github.thehilikus.events.event_manager;

import static org.mockito.Mockito.mock;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNotSame;

import java.util.EventListener;

import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import com.github.thehilikus.events.event_manager.SubscriptionManager;
import com.github.thehilikus.events.event_manager.api.EventDispatcher;
import com.github.thehilikus.events.event_manager.api.EventPublisher;

import com.github.thehilikus.events.event_manager.dummies.DummyListener;
import ch.qos.logback.classic.Level;

/**
 * Tests {@link SubscriptionManager}
 * 
 * @author hilikus
 */
public class SubscriptionManagerTest {

    private SubscriptionManager TU;

    /**
     * Configures tests
     */
    @BeforeMethod
    public void setUp() {
	ch.qos.logback.classic.Logger TULog = (ch.qos.logback.classic.Logger) LoggerFactory
		.getLogger(SubscriptionManager.class);
	TULog.setLevel(Level.TRACE);

	TU = new SubscriptionManager();
    }

    /**
     * Checks removing all listeners of a particular publisher
     */
    @Test(dependsOnMethods = "subscribe")
    public void unsubscribeAllSingle() {
	EventPublisher publisher = mock(EventPublisher.class);
	TU.subscribe(publisher, new DummyListener());
	TU.subscribe(publisher, new DummyListener());

	EventPublisher publisherTwo = mock(EventPublisher.class);
	TU.subscribe(publisherTwo, new DummyListener());

	TU.unsubscribeAll(publisher);
	assertEquals(TU.getSubscribersCount(publisher), 0, "Check all listeners were removed");
	assertEquals(TU.getSubscribersCount(publisherTwo), 1, "Check other publishers were untouched");

    }

    /**
     * Checks removing all the listeners of all the publishers
     */
    @Test
    public void unsubscribeAllAll() {
	EventPublisher publisher = mock(EventPublisher.class);
	TU.subscribe(publisher, new DummyListener());
	TU.subscribe(publisher, new DummyListener());

	EventPublisher publisherTwo = mock(EventPublisher.class);
	TU.subscribe(publisherTwo, new DummyListener());

	TU.unsubscribeAll();
	assertEquals(TU.getSubscribersCount(publisher), 0, "Publisher one was not cleaned");
	assertEquals(TU.getSubscribersCount(publisherTwo), 0, "Publisher two was not cleaned");

    }

    
    /**
     * Tests that different objects get different dispatchers
     */
    @Test
    public void getEventDispatcher() {
	EventDispatcher disp1 = TU.getEventDispatcher(mock(EventPublisher.class));
	EventDispatcher disp2 = TU.getEventDispatcher(mock(EventPublisher.class));
	
	assertNotSame(disp1, disp2, "Different objects get different dispatchers");

    }

    /**
     * Tests subscribing multiple listeners to multiple publishers
     */
    @Test
    public void subscribe() {
	EventPublisher publisher = mock(EventPublisher.class);
	EventPublisher publisherTwo = mock(EventPublisher.class);
	EventListener listener = new DummyListener();
	TU.subscribe(publisher, listener);
	TU.subscribe(publisher, new DummyListener());
	TU.subscribe(publisherTwo, listener);

	assertEquals(TU.getSubscribersCount(publisher), 2, "Check both listeners were subscribed");
	assertEquals(TU.getSubscribersCount(publisherTwo), 1, "Check usbscriptions are kept separated");

    }

    /**
     * Tests subscribing the same listener to the same publisher twice
     */
    @Test(dependsOnMethods = "subscribe")
    public void subscribeDuplicates() {
	EventPublisher publisher = mock(EventPublisher.class);
	EventListener listener = new DummyListener();
	TU.subscribe(publisher, listener);
	TU.subscribe(publisher, listener);

	assertEquals(TU.getSubscribersCount(publisher), 1, "Check duplicate listener was ignored");

    }

    /**
     * Checks removing a subscription
     */
    @Test(dependsOnMethods = "subscribe")
    public void unsubscribe() {
	EventPublisher publisher = mock(EventPublisher.class);
	DummyListener listener = new DummyListener();
	TU.subscribe(publisher, listener);
	TU.subscribe(publisher, new DummyListener());

	TU.unsubscribe(publisher, listener);
	assertEquals(TU.getSubscribersCount(publisher), 1, "Check only one listener left");
    }
}
