package cs451;

import cs451.ActiveHost;
import cs451.MessageType;

import java.util.Objects;

public class Message {

    private int payload;
    private MessageType type;
    private final ActiveHost sender;
    private final ActiveHost originalSender;


    public Message(String payload, MessageType type, ActiveHost sender, ActiveHost originalSender){
        this.payload = Integer.parseInt(payload);
        this.type = type;
        this.sender = sender;
        this.originalSender = originalSender;

    }

    public Message(String desc){
        String content [] = desc.split(">>>");


        this.type = null;
        try {
            this.type = MessageType.valueOf(content[0]);
        } catch (Exception e){
            System.out.println("Enum value of error is for message : ====" + content[0] +"====");
        }
        Integer senderId = Integer.parseInt(content[1]);
        Integer originalSenderId = Integer.parseInt(content[2]);

        try {
            this.payload = Integer.parseInt(content[3]);
        } catch(Exception e){
            System.out.println("Problem to interpret : ");
            System.out.println(desc);
        }
        this.sender = new ActiveHost(senderId, "localhost", Constants.BASE_PORT + senderId - 1);
        this.originalSender = new ActiveHost(originalSenderId, "localhost", Constants.BASE_PORT + originalSenderId - 1);



    }

    public MessageType getType() {
        return type;
    }

    public String getPayload() {
        return "" + payload;
    }

    public int getId(){
        //return Integer.parseInt(payload) + 1;
        return payload + 1;
    }

    public ActiveHost getSender(){ return sender; }

    public ActiveHost getOriginalSender() { return originalSender; }

    @Override
    public String toString() {
        return type + ">>>" + sender.getId() + ">>>" + originalSender.getId() + ">>>" + payload;
    }

    @Override
    public boolean equals(Object obj) {
        if(! (obj instanceof Message)){
            return false;
        } else {
            Message that = (Message)obj;
            return that.payload == this.payload && that.getOriginalSender().equals(this.getOriginalSender()) && that.type.equals(this.type);
        }
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(type, payload, originalSender);
    }
}
