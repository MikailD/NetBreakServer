import student.TestCase;

// -------------------------------------------------------------------------
/**
 *  TestSuite for the ValidPairQueue
 *  Test all paths of code execution through the ValidPairQueue structure.
 *
 *  @author ram
 *  @version 2010.11.17
 */

public class ValidPairQueueTest
    extends TestCase
{
    private ValidPairQueue<TestItem> vpq;
    private TestItem item1;
    private TestItem item2;
    private TestItem item3;
    private TestItem item4;

    /**
     * Setup method called before all tests.
     * Implicitly tests push and size.
     */
    public void setUp()
    {
        vpq = new ValidPairQueue<TestItem>();
        item1 = new TestItem();
        item2 = new TestItem();
        item3 = new TestItem();
        item4 = new TestItem();

        assertEquals(0, vpq.size());

        vpq.push(item1);
        vpq.push(item2);
        vpq.push(item3);
        vpq.push(item4);

        assertEquals(4, vpq.size());
    }

    /**
     * Test pop
     */
    public void testPop()
    {
        ValidPairQueue.Pair<TestItem> pair;

        assertTrue(vpq.canPop());
        pair = vpq.pop();
        assertEquals(item1, pair.first());
        assertEquals(item2, pair.second());

        assertTrue(vpq.canPop());
        pair = vpq.pop();
        assertEquals(item3, pair.first());
        assertEquals(item4, pair.second());

        assertFalse(vpq.canPop());
        boolean caught = false;
        try
        {
            pair = vpq.pop();
        }
        catch ( IllegalStateException e )
        {
            caught = true;
        }
        assertTrue(caught);
    }

    /**
     * Test invalidate removing.
     */
    public void testInvalidRemove()
    {
        item2.setValid(false);
        item3.setValid(false);

        assertTrue(vpq.canPop());

        ValidPairQueue.Pair<TestItem> pair = vpq.pop();
        assertEquals(item1, pair.first());
        assertEquals(item4, pair.second());
    }


    /**
     * Test class used for testing the ValidPairQueue
     */
    private class TestItem implements Validity
    {
        private boolean valid;

        /**
         * Create a new valid TestItem.
         */
        public TestItem()
        {
            valid = true;
        }

        /**
         * Test the validity.
         * @return if the item is valid.
         */
        public boolean isValid()
        {
            return valid;
        }

        /**
         * Set the validity.
         * @param val the new validity.
         */
        public void setValid( boolean val)
        {
            valid = val;
        }

    }
}
