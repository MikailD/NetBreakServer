import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;


// -------------------------------------------------------------------------
/**
 *  ClientConnection manages a single connection from the client.
 *  Each ClientConnection is a thread which handles a single connection
 *  coming into the server from a client program.
 *
 *  @author ram
 *  @version 2010.11.16
 */

public class ClientConnection
    implements Runnable, Validity
{
    private Socket conn;
    private BufferedReader in;
    private PrintWriter out;
    private boolean stillValid;

    /**
     * Create a new client connection.
     * @param socket
     *        The socket of the incoming connection.
     */
    public ClientConnection( Socket socket )
    {
        conn = socket;
        stillValid = true;

        try
        {
            in = new BufferedReader( new InputStreamReader(
                conn.getInputStream() ));

            out = new PrintWriter( conn.getOutputStream(), true );
        }
        catch (IOException e)
        {
            System.err.println("Failed allocating in/out.");
        }

    }

    // ----------------------------------------------------------
    /**
     * Run a new thread to manage the client connection.
     */
    @Override
    public void run()
    {
        String line;

        while (true)
        {
            try
            {
                line = in.readLine();
                if (line == null)
                {
                    // Client has left.
                    close();
                    return;
                }
                System.out.println(line);
            }
            catch (IOException e)
            {
                try
                {
                    close();
                }
                catch (IOException e1)
                {
                    return;
                }

                return;
            }
        }

    }

    /**
     * Send data to this client.
     * @param line
     *        The line of data to send to the client.
     */
    public synchronized void sendData( String line )
    {
        out.println(line);
    }

    /**
     * Get the IP address of this client.
     * @return the IP address of this client in String form.
     */
    public String getIPAddress()
    {
        return conn.getInetAddress().getHostAddress();
    }

    /**
     * Close the connection when this object gets garbage collected.
     */
    protected void finalize()
    {
        try
        {
            conn.close();
        }
        catch (IOException e)
        {
            System.err.println("Error closing client socket.");
        }

    }

    /**
     * Check if the client connection is still valid, this is so that the queue
     * can check and remove any invalid connections.
     * @return is the connection valid?.
     */
    public boolean isValid()
    {
        return stillValid;
    }

    /**
     * Close this connection.
     * @throws IOException
     */
    public void close() throws IOException
    {
        conn.close();
        stillValid = false;
    }
}

