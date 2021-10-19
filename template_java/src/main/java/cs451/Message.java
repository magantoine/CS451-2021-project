package cs451;

public class Message {

    private final String payload;
    private final MessageType type;

    public Message(String payload, MessageType type){
        this.payload = payload;
        this.type = type;
    }

    public MessageType getType() {
        return type;
    }

    public String getPayload() {
        return payload;
    }
}
