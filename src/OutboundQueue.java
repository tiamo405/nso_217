import java.util.Vector;

/** One FIFO per connection generation. Closing wakes waiters and discards stale packets. */
final class OutboundQueue {
    static final class Entry {
        final Message message;
        final long enqueuedAt = System.currentTimeMillis();
        Entry(Message message) { this.message = message; }
    }

    private final Vector entries = new Vector();
    private long generation;
    private boolean open;
    private boolean ready;
    private long sent, waits, dropped, maxDelayMillis;

    synchronized long open() {
        close();
        open = true;
        return generation;
    }

    synchronized void close() {
        open = false;
        ready = false;
        ++generation;
        dropped += entries.size();
        entries.removeAllElements();
        notifyAll();
    }

    synchronized boolean current(long token) { return open && generation == token; }
    synchronized long generation() { return generation; }

    synchronized void ready() { ready = true; notifyAll(); }

    synchronized void add(Message message) {
        if (!open) { ++dropped; return; }
        entries.addElement(new Entry(message));
        notifyAll();
    }

    synchronized Entry take(long token) throws InterruptedException {
        while (current(token) && (!ready || entries.isEmpty())) {
            ++waits;
            wait();
        }
        if (!current(token)) return null;
        Entry entry = (Entry) entries.elementAt(0);
        entries.removeElementAt(0);
        return entry;
    }

    synchronized void sent(Entry entry) {
        ++sent;
        maxDelayMillis = Math.max(maxDelayMillis, System.currentTimeMillis() - entry.enqueuedAt);
    }

    synchronized long[] stats() {
        return new long[]{entries.size(), sent, waits, dropped, maxDelayMillis};
    }
}
