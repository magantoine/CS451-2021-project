package cs451;

import cs451.Link;
import cs451.Message;
import cs451.MessageType;

import java.io.IOException;
import java.util.Optional;

public class ReliableLink implements Link {

    private final Link innerLink;

    public ReliableLink(Link flLink){
        this.innerLink = flLink;

    }
    @Override
    public void rSend(String ipDest, int portDest, String message) throws IOException {
        /**
         * Here the goal is to 1) send using the fairloss link, wait 1000 ms for a ACK,
         * if no hack is received start again
         */
        boolean acked = false;

        while(!acked){
            // we send the message
            innerLink.rSend(ipDest, portDest, message);
            // we wait for an ACK (no ACK in return)
            Optional<Message> res = waitForMessage(5000, true);
            acked = res.isPresent() && res.get().getType() == MessageType.ACK;
        }

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
        return innerLink.waitForMessage(100000, true);
    }

    public String getChannelId(){
        return this.innerLink.getChannelId();
    }

    public void close(){
        this.innerLink.close();
    }

}
