package test;

public class PlusAgent implements Agent {

    private final String name = "plus";

    private final String in1Name;
    private final String in2Name;

    private final Topic in1;
    private final Topic in2;
    private final Topic out;

    private double x;
    private double y;
    private boolean hasX;
    private boolean hasY;

    public PlusAgent(String[] subs, String[] pubs) {
        if (subs == null || subs.length < 2) {
            throw new IllegalArgumentException("PlusAgent needs 2 subs topics");
        }
        if (pubs == null || pubs.length < 1) {
            throw new IllegalArgumentException("PlusAgent needs 1 pubs topic");
        }

        this.in1Name = subs[0];
        this.in2Name = subs[1];

        TopicManagerSingleton.TopicManager tm = TopicManagerSingleton.get();

        this.in1 = tm.getTopic(subs[0]);
        this.in2 = tm.getTopic(subs[1]);
        this.out = tm.getTopic(pubs[0]);

        // subscribe to inputs
        in1.subscribe(this);
        in2.subscribe(this);

        // register as publisher to output
        out.addPublisher(this);

        reset();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void reset() {
        x = 0.0;
        y = 0.0;
        hasX = false;
        hasY = false;
    }

    @Override
    public void callback(String topic, Message msg) {
        if (msg == null) return;
        double val = msg.asDouble;
        if (Double.isNaN(val)) return;

        if (topic.equals(in1Name)) {
            x = val;
            hasX = true;
        } else if (topic.equals(in2Name)) {
            y = val;
            hasY = true;
        } else {
            return;
        }

        if (hasX && hasY) {
            out.publish(new Message(x + y));
        }
    }

    @Override
    public void close() {
        in1.unsubscribe(this);
        in2.unsubscribe(this);
        out.removePublisher(this);
    }
}
