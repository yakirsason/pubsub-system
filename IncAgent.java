package test;

public class IncAgent implements Agent {

    private final String name = "inc";

    private final Topic in;
    private final Topic out;

    public IncAgent(String[] subs, String[] pubs) {
        if (subs == null || subs.length < 1) {
            throw new IllegalArgumentException("IncAgent needs 1 subs topic");
        }
        if (pubs == null || pubs.length < 1) {
            throw new IllegalArgumentException("IncAgent needs 1 pubs topic");
        }

        TopicManagerSingleton.TopicManager tm = TopicManagerSingleton.get();

        this.in = tm.getTopic(subs[0]);
        this.out = tm.getTopic(pubs[0]);

        // subscribe to input
        in.subscribe(this);

        // register as publisher to output
        out.addPublisher(this);
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void reset() {
        // nothing to reset
    }

    @Override
    public void callback(String topic, Message msg) {
        if (msg == null) return;

        double val = msg.asDouble;
        if (Double.isNaN(val)) return;

        out.publish(new Message(val + 1));
    }

    @Override
    public void close() {
        in.unsubscribe(this);
        out.removePublisher(this);
    }
}
