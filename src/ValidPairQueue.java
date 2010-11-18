
// -------------------------------------------------------------------------
/**
 *  ValidPairQueue is a queue based data structure that allows for two items to
 *  be popped off the front of the queue at a time.
 *
 *  The ValidPairQueue will also loop through the items and remove any invalid
 *  items from itself.
 *
 *  Apart from those details, the queue works as you would otherwise expect
 *  a queue to work, that is FIFO.
 *
 *  ValidairQueue is implemented using a doubly linked structure with dummy
 *  nodes on either end.
 *
 *  @author ram
 *  @version 2010.11.16
 *  @param <Item> The type of the objects stored in the pair queue.
 */

public class ValidPairQueue<Item extends Validity>
{
    private Node<Item> head;
    private Node<Item> tail;
    private int size;

    /**
     * Create a new empty PairQueue.
     */
    public ValidPairQueue()
    {
        // Create the dummy nodes.
        head = new Node<Item>( null );
        tail = new Node<Item>( null );

        // Link the dummy nodes together.
        head.setNext(tail);
        tail.setPrev(head);

        // Set the size.
        size = 0;

        // Check the queue.
        assert saneQueue();
    }

    /**
     * Add an item into the queue.
     * @param item
     *        The item to add to the queue.
     */
    public void push( Item item )
    {
        // Make sure everything is valid.
        removeInvalid();

        // Allocate a node.
        Node<Item> newNode = new Node<Item>( item );

        // Insert the new node at the end of the queue.
        newNode.setNext(tail);
        newNode.setPrev(tail.getPrev());

        tail.getPrev().setNext(newNode);
        tail.setPrev(newNode);

        // Update the queue size;
        size++;

        // Make sure the queue is still proper.
        assert saneQueue();
    }

    /**
     * Can we pop off the queue. That is to say are there at least 2 items in
     * the queue which we can pop.
     * @return are there at least 2 item in the PairQueue.
     */
    public boolean canPop()
    {
        removeInvalid();

        return (size >= 2);
    }

    /**
     * Return an array of size 2 containing the front two elements from the
     * queue. Remove these elements from the queue.
     * @return the first two elements from the queue.
     */
    public Pair<Item> pop()
    {
        if ( !canPop() )
        {
            throw new IllegalStateException("Must have at least 2 items in " +
                "Queue");
        }

        // Get the data from the queue.
        Item first = head.getNext().getData();
        Item second = head.getNext().getNext().getData();

        // Remove the nodes from the queue.
        removeNode(head.getNext());
        removeNode(head.getNext());

        // Why, Oh Why, Does Java not support creating generic arrays...
        Pair<Item> ret = new Pair<Item>(first, second);

        // Make sure we are still good.
        assert saneQueue();

        // Return the array.
        return ret;
    }

    /**
     * Remove a particular node from the list.
     * @param node
     *        The node to remove.
     */
    private void removeNode( Node<Item> node )
    {
        node.getPrev().setNext(node.getNext());
        node.getNext().setPrev(node.getPrev());
        size--;

        assert saneQueue();
    }

    /**
     * Get the number of items in the queue.
     * @return the size of the queue.
     */
    public int size()
    {
        return size;
    }

    /**
     * Remove all invalid entries from the queue.
     */
    private void removeInvalid()
    {
        Node<Item> current = head;

        while ( current.getNext() != tail )
        {
            current = current.getNext();
            if (!current.getData().isValid())
            {
                removeNode(current);
            }
        }
    }

    // -------------------------------------------------------------------------
    /**
     *  The internal nodes of the PairQueue. They are doubly linked.
     *  @param <T> The type of the data represented by the node.
     *
     *  @author ram
     *  @version 2010.11.16
     */
    private class Node<T extends Validity>
    {
        private T data;
        private Node<T> next;
        private Node<T> prev;

        /**
         * Create a new node.
         * @param nodeData the data to hold in the node.
         */
        public Node( T nodeData )
        {
            data = nodeData;
            next = null;
            prev = null;
        }

        /**
         * Get the data from the node.
         * @return The data represented by the node.
         */
        public T getData()
        {
            return data;
        }

        /**
         * Get the next node in the chain.
         * @return The next node.
         */
        public Node<T> getNext()
        {
            return next;
        }

        /**
         * Set the next node in the chain.
         * @param newNext
         *        The new next node to be placed in the chain.
         */
        public void setNext(Node<T> newNext)
        {
            next = newNext;
        }

        /**
         * Get the previous node in the chain.
         * @return the prev node.
         */
        public Node<T> getPrev()
        {
            return prev;
        }

        /**
         * Set the previous node in the chain.
         * @param newPrev
         *        The new prev node to be placed in the chain.
         */
        public void setPrev(Node<T> newPrev)
        {
            prev = newPrev;
        }
    }

    /**
     * Sanity checking method. Calls other sanity checking methods and returns
     * true. Uses assert on other sanity checking methods, so this will fail
     * if one of them fails.
     *
     * Called through assert, so this should not impact the performance of
     * production code.
     *
     * @returns true. (Will only reach this state if all other sanity checks
     * pass).
     */
    private boolean saneQueue()
    {
        assert headAndTailGood();
        assert checkHeadToTail();
        assert checkTailToHead();

        return true;
    }

    /**
     * Internal sanity checking method. Makes sure that head and tail have
     * null data and that they are the beginning and end of the list
     * respectively.
     * @return If the test passes.
     */
    private boolean headAndTailGood()
    {
        return ((head.getData() == null) && (tail.getData() == null) &&
             (head.getPrev() == null) && (tail.getNext() == null));
    }

    /**
     * Internal sanity checking method. Makes sure there are size number of
     * links between head and tail going forwards. (Will also fail if we reach
     * size jumps and haven't found tail yet).
     * @return If the test passes.
     */
    private boolean checkHeadToTail()
    {
        Node<Item> current = head;
        int jumps = 0;

        while (current.getNext() != tail)
        {
            jumps++;
            current = current.getNext();

            if (jumps > size)
            {
                return false;
            }
        }

        return (jumps == size);
    }

    /**
     * Internal sanity checking method. Makes sure there are size number of
     * links between tail and head going backwards. (Will also fail if we reach
     * size jumps and haven't found head yet).
     * @return If the test passes.
     */
    private boolean checkTailToHead()
    {
        Node<Item> current = tail;
        int jumps = 0;

        while (current.getPrev() != head)
        {
            jumps++;
            current = current.getPrev();

            if (jumps > size)
            {
                return false;
            }
        }

        return (jumps == size);
    }

    // -------------------------------------------------------------------------
    /**
     *  The Valid pair queue returns a pair of some type.
     *  @param <Type>
     *
     *  @author ram
     *  @version 2010.11.17
     */
    public static class Pair<Type>
    {
        private Type f;
        private Type s;

        /**
         * Create a new pair.
         * @param first
         *        The first item in the pair.
         * @param second
         *        The second item in the pair.
         */
        public Pair(Type first, Type second)
        {
            f = first;
            s = second;
        }

        /**
         * Get the first item in the pair.
         * @return the first item in the pair.
         */
        public Type first()
        {
            return f;
        }

        /**
         * Get the second item in the pair.
         * @return the second item in the pair.
         */
        public Type second()
        {
            return s;
        }
    }
}
