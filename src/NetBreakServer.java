import java.io.IOException;
import java.net.ServerSocket;

// -------------------------------------------------------------------------
/**
 *  This is the main entry point for the game server.
 *  The server will be instantiated and run listening on port 2114, or a port
 *  passed in as a command line argument. The Server will accept incoming
 *  connections and push those clients into a queue, allowing them to get
 *  matched with each other. When a game is started, two users are popped
 *  off the queue and matched with each other.
 *
 *  The server is implemented using a multi-threaded model. This isn't much
 *  of a problem because we don't expect to handle many connections. If the
 *  server were intended to scale larger than a few hundred to a few thousand
 *  connections then a more stable model would be to use asynchronous polling
 *  of the client connections.
 *
 *  @author ram
 *  @version 2010.11.16
 */

public class NetBreakServer
{
    private static int DEFAULTPORT = 2114;

    private ServerSocket server;
    private ValidPairQueue<ClientConnection> clients;

    /**
     * Initialize the game server on the default port or on the port passed in
     * as an argument.
     * @param argv The command line arguments.
     */
    public static void main( String[] argv )
    {
        // Determine the port.
        int port = (argv.length == 0 ? DEFAULTPORT : Integer.valueOf(argv[0]));

        // Launch the game server.
        new NetBreakServer(port);
    }

    /**
     * Create a new NetBreakServer instance. Launch on the port passed in as
     * an argument.
     * @param port
     *        The port that the NetBreakServer instance should listen on.
     */
    public NetBreakServer( int port )
    {
        // Set up the clients queue.
        clients = new ValidPairQueue<ClientConnection>();

        // Create the socket listening on the specified port.
        try
        {
            server = new ServerSocket(port);
        }
        catch (IOException e)
        {
            System.err.println("Could not listen on port " + port);
            //System.exit(-1);
            throw new IllegalStateException(e);
        }

        // Continually listen for clients and accept them.
        // We add the client connection to our internal game queue of waiting
        // clients, then run the client connection thread so that we can
        // communicate with the client.
        while ( true )
        {
            ClientConnection client;
            try
            {
                // Accept the new client.
                client = new ClientConnection( server.accept() );

                // Add the client to the queue.
                clients.push(client);

                // Create a thread to handle the client and run it.
                Thread cThread = new Thread(client);
                cThread.start();

                // Tell the client we have them, and to wait for an IP.
                client.sendData("WAIT");

                // Try to create a new game (if we can).
                createGame();
            }
            catch (IOException e)
            {
                System.err.println("Failed to accept a connection.");
            }
        }
    }

    /**
     * Create a new game.
     * If we can, take the first two clients in the queue, remove them, and
     * start a game by sending each the ip of the other. After this point, it
     * is the job of the clients to set up the game in a p2p fashion.
     */
    private void createGame()
    {
        if (!clients.canPop())
        {
            return;
        }

        // Get the clients from the queue.
        ValidPairQueue.Pair<ClientConnection> pair = clients.pop();

        // Get the client information.
        String ipFirst = pair.first().getIPAddress();
        String ipSecond = pair.second().getIPAddress();

        // Send the client information to the clients.
        pair.first().sendData(ipSecond);
        pair.second().sendData(ipFirst);

        try
        {
            pair.first().close();
            pair.second().close();
        }
        catch (IOException e)
        {
            System.err.println("Error closing connection.");
        }
    }

    /**
     * Clean up method when the JVM shuts down the program and in doing so the
     * game server object gets collected.
     */
    protected void finalize()
    {
        try
        {
            server.close();
            super.finalize();
        }
        catch (Throwable e)
        {
            System.err.println("Error collecting the server.");
        }

    }
}

