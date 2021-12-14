package cs451;

import cs451.ActiveHost;
import cs451.MessageType;

import java.util.Arrays;
import java.util.Objects;

public class Message {

    private final String payload;
    private MessageType type;
    private final ActiveHost sender;
    private final ActiveHost originalSender;
    private final int id;
    private final VectorClock vectorClock;


    public Message(String payload, MessageType type, ActiveHost sender, ActiveHost originalSender){
        this.payload = payload;
        this.type = type;
        this.sender = sender;
        this.originalSender = originalSender;
        this.id = Integer.parseInt(payload) + 1;
        this.vectorClock = new VectorClock(0);

    }

    public Message(String payload, MessageType type, ActiveHost sender, ActiveHost originalSender, VectorClock vectorClock){
        this.payload = payload;
        this.type = type;
        this.sender = sender;
        this.originalSender = originalSender;
        this.id = Integer.parseInt(payload) + 1;
        this.vectorClock = vectorClock;
    }

    public Message(String desc){

        //System.out.println("Received message : " + desc);
        String content [] = desc.split(">>>");
        this.type = null;
        try {
            this.type = MessageType.valueOf(content[0]);
        } catch (Exception e){
            System.out.println("Enum value of error is for message : ====" + content[0] +"====");
        }
        Integer senderId = Integer.parseInt(content[1]);
        Integer originalSenderId = Integer.parseInt(content[2]);

        this.payload = content[3];
        this.sender = new ActiveHost(senderId, "localhost", Constants.BASE_PORT + senderId - 1);
        this.originalSender = new ActiveHost(originalSenderId, "localhost", Constants.BASE_PORT + originalSenderId - 1);

        this.id = Integer.parseInt(payload) + 1;

        String clockString [] = content[4].split(", ");
        clockString[0] = clockString[0].substring(1); // substring starting at index 1 to get rid of starting "["
        clockString[clockString.length - 1] = clockString[clockString.length - 1].substring(0, clockString[clockString.length - 1].length() - 1);
        var clock = new VectorClock(clockString.length);
        for(int i = 0; i < clockString.length; i++) {
            if(clockString[i].length() > 0) {
                clock.set(i + 1, Integer.parseInt(clockString[i]));
            }
        }

        this.vectorClock = clock;

    }

    public MessageType getType() {
        return type;
    }

    public String getPayload() {
        return payload;
    }

    public int getId(){
        return id;
    }

    public ActiveHost getSender(){ return sender; }

    public ActiveHost getOriginalSender() { return originalSender; }

    public VectorClock getVectorClock(){ return vectorClock; }
    @Override
    public String toString() {
        return type + ">>>" + sender.getId() + ">>>" + originalSender.getId() + ">>>" + payload + ">>>" + vectorClock;
    }

    @Override
    public boolean equals(Object obj) {
        if(! (obj instanceof Message)){
            return false;
        } else {
            Message that = (Message)obj;
            return that.payload.equals(this.payload) && that.getOriginalSender().equals(this.getOriginalSender()) && that.type.equals(this.type);
        }
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(type, payload, originalSender);
    }
}
