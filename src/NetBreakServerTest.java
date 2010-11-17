import student.TestCase;

// -------------------------------------------------------------------------
/**
 *  Test running the NetBreakServer
 *  This test suite will attempt to execute all paths through NetBreakServer.
 *
 *  @author ram
 *  @version 2010.11.17
 */

public class NetBreakServerTest
    extends TestCase
{
    // A private thread we can use to launch the server in a separate thread.
    // Testing in a multi threaded context is rather difficult.
    private Thread runServer = new Thread()
    {
        public void run()
        {
            new NetBreakServer(2114);
        }
    };

    /**
     * Test opening a port less than 1024 (which you need to be root to do
     * legally). (This test will fail if it is run as root, but at that point
     * there are much larger problems ;])
     */
    public void testIllegalPort()
    {
        boolean exceptionCaught = false;
        try
        {
            new NetBreakServer(1);
        }
        catch (IllegalStateException e)
        {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
    }

    /**
     * Test trying to launch two instances on the same port.
     */
    public void testSamePort()
    {
        boolean exceptionCaught = false;

        try {
            runServer.start();
            runServer.start();
        }
        catch (IllegalThreadStateException e)
        {
            exceptionCaught = true;
        }
        assertTrue(exceptionCaught);
    }

}
