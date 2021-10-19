package cs451;

public class Message {

    private final String payload;
    private final MessageType type;
    private final Host sender;

    public Message(String payload, MessageType type, Host sender){
        this.payload = payload;
        this.type = type;
        this.sender = sender;
    }

    public MessageType getType() {
        return type;
    }

    public String getPayload() {
        return payload;
    }
}
