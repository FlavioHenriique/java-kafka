package messages;

public class Message<T> {

    private T payload;
    private CorrelationId id;

    public Message(T payload, CorrelationId id) {
        this.payload = payload;
        this.id = id;
    }

    public T getPayload() {
        return payload;
    }

    public CorrelationId getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Message{" +
                "payload=" + payload +
                ", id=" + id +
                '}';
    }
}
