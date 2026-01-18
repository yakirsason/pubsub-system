package test;

import java.util.function.BinaryOperator;

public class BinOpAgent implements Agent {

    private final String name;

    private final Topic inTopic1;
    private final Topic inTopic2;
    private final Topic outTopic;

    private final String in1Name;
    private final String in2Name;

    private final BinaryOperator<Double> op;

    private double v1;
    private double v2;
    private boolean has1;
    private boolean has2;

    public BinOpAgent(String name, String in1, String in2, String out, BinaryOperator<Double> op) {
        this.name = name;
        this.in1Name = in1;
        this.in2Name = in2;
        this.op = op;

        TopicManagerSingleton.TopicManager tm = TopicManagerSingleton.get();

        this.inTopic1 = tm.getTopic(in1);
        this.inTopic2 = tm.getTopic(in2);
        this.outTopic = tm.getTopic(out);

        // Subscribe to inputs
        this.inTopic1.subscribe(this);
        this.inTopic2.subscribe(this);

        // Register as publisher to output (needed for Graph.createFromTopics)
        this.outTopic.addPublisher(this);

        reset();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void reset() {
        v1 = 0.0;
        v2 = 0.0;
        has1 = false;
        has2 = false;
    }

    @Override
    public void callback(String topic, Message msg) {
        if (msg == null) return;

        double val = msg.asDouble;
        if (Double.isNaN(val)) return; // ignore non-numeric

        if (topic.equals(in1Name)) {
            v1 = val;
            has1 = true;
        } else if (topic.equals(in2Name)) {
            v2 = val;
            has2 = true;
        } else {
            return;
        }

        if (has1 && has2) {
            double result = op.apply(v1, v2);
            outTopic.publish(new Message(result));
        }
    }

    @Override
    public void close() {
        inTopic1.unsubscribe(this);
        inTopic2.unsubscribe(this);
        outTopic.removePublisher(this);
    }
}
