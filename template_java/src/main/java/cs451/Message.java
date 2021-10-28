package cs451;

import cs451.ActiveHost;
import cs451.MessageType;

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
