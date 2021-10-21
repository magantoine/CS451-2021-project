package cs451;

public class Message {

    private final String payload;
    private final MessageType type;
    private final ActiveHost sender;

    public Message(String payload, MessageType type, ActiveHost sender){
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

    public ActiveHost getSender(){ return sender; }
}
