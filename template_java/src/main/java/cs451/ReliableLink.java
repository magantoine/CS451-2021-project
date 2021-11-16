package cs451;

import cs451.Link;
import cs451.Message;
import cs451.MessageType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ReliableLink implements Link {

    private final Link innerLink;
    private final List<Message> ackedMessages = new ArrayList<Message>();
    private final List<Message> received = new ArrayList<Message>();

    public ReliableLink(Link flLink){
        this.innerLink = flLink;

    }
    @Override
    public void rSend(String ipDest, int portDest, Message message) throws IOException {
        /**
         * Here the goal is to 1) send using the fairloss link, wait 1000 ms for a ACK,
         * if no hack is received start again
         */
        if(ackedMessages.contains(message)){
            // if we get here it means the message has already been sent and properly acked by receiver
            // we have simply nothing to do and can carry on
            return;
        }
        boolean acked = false;

        while(!acked){
            // we send the message
            innerLink.rSend(ipDest, portDest, message);
            // we wait for an ACK (no ACK in return)
            Optional<Message> res = waitForMessage(5000, true);
            acked = res.isPresent() && res.get().getType() == MessageType.ACK;

        }
        System.out.println("Message " + message + " got acked");
        ackedMessages.add(message); // message has been acked properly

        // if we get here means, we sent a message and received an ACK
    }

    @Override
    public Optional<Message> waitForMessage(int timeout, boolean toAck) throws IOException {
        // short time out
        // waits for a message and ACK for it
        return innerLink.waitForMessage(5000, true);
    }

    @Override
    public Optional<Message> waitForMessage() throws IOException {
        // long time out
        Optional<Message> msg = innerLink.waitForMessage(100000, true);
        if(!msg.isPresent()){
            return msg;
        }
        Message unfolded = msg.get();

        // we don't deliver twice the same message and don't deliver ACKs
        if(received.contains(unfolded) || unfolded.getType().equals(MessageType.ACK)){
            return Optional.empty();
        }

        System.out.println("Received : " + unfolded);
        received.add(unfolded);
        return msg;

    }

    public String getChannelId(){
        return this.innerLink.getChannelId();
    }

    public void close(){
        this.innerLink.close();
    }

}
