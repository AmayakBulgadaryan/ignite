/* @java.file.header */

/*  _________        _____ __________________        _____
 *  __  ____/___________(_)______  /__  ____/______ ____(_)_______
 *  _  / __  __  ___/__  / _  __  / _  / __  _  __ `/__  / __  __ \
 *  / /_/ /  _  /    _  /  / /_/ /  / /_/ /  / /_/ / _  /  _  / / /
 *  \____/   /_/     /_/   \_,__/   \____/   \__,_/  /_/   /_/ /_/
 */

package org.gridgain.grid.kernal.managers.events;

import org.apache.ignite.*;
import org.apache.ignite.configuration.*;
import org.apache.ignite.events.*;
import org.apache.ignite.lang.*;
import org.gridgain.grid.kernal.managers.eventstorage.*;
import org.gridgain.grid.util.typedef.*;
import org.gridgain.testframework.junits.common.*;

import static org.apache.ignite.events.IgniteEventType.*;

/**
 * Tests for {@link GridEventStorageManager}.
 */
public class GridEventStorageManagerSelfTest extends GridCommonAbstractTest {
    /**
     *
     */
    public GridEventStorageManagerSelfTest() {
        super(/* start grid */true);
    }

    /** {@inheritDoc} */
    @Override protected IgniteConfiguration getConfiguration(String gridName) throws Exception {
        IgniteConfiguration cfg = super.getConfiguration(gridName);

        int[] evts = new int[EVTS_ALL.length + 1];

        evts[0] = Integer.MAX_VALUE - 1;

        System.arraycopy(EVTS_ALL, 0, evts, 1, EVTS_ALL.length);

        cfg.setIncludeEventTypes(evts);

        return cfg;
    }

    /**
     * @throws Exception If failed.
     */
    public void testWaitForEvent() throws Exception {
        Ignite ignite = grid();

        final int usrType = Integer.MAX_VALUE - 1;

        IgniteFuture<IgniteEvent> fut = waitForLocalEvent(ignite.events(), new IgnitePredicate<IgniteEvent>() {
            @Override public boolean apply(IgniteEvent e) {
                return e.type() == usrType;
            }
        }, usrType);

        try {
            fut.get(500);

            fail("GridFutureTimeoutException must have been thrown.");
        }
        catch (IgniteFutureTimeoutException e) {
            info("Caught expected exception: " + e);
        }

        ignite.events().recordLocal(new IgniteEventAdapter(null, "Test message.", usrType) {
            // No-op.
        });

        assert fut.get() != null;

        assertEquals(usrType, fut.get().type());
    }

    /**
     * @throws Exception If failed.
     */
    public void testWaitForEventContinuationTimeout() throws Exception {
        Ignite ignite = grid();

        try {
            // We'll never wait for nonexistent type of event.
            int usrType = Integer.MAX_VALUE - 1;

            waitForLocalEvent(ignite.events(), F.<IgniteEvent>alwaysTrue(), usrType).get(1000);

            fail("GridFutureTimeoutException must have been thrown.");
        }
        catch (IgniteFutureTimeoutException e) {
            info("Caught expected exception: " + e);
        }
    }

    /**
     * @throws Exception If failed.
     */
    public void testUserEvent() throws Exception {
        Ignite ignite = grid();

        try {
            ignite.events().recordLocal(new IgniteEventAdapter(null, "Test message.", IgniteEventType.EVT_NODE_FAILED) {
                // No-op.
            });

            assert false : "Exception should have been thrown.";

        }
        catch (IllegalArgumentException e) {
            info("Caught expected exception: " + e);
        }
    }
}
