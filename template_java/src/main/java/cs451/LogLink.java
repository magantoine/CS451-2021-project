package cs451;


import java.io.IOException;
import java.util.Optional;

public class LogLink implements Link {
    private final OutputLog log;
    private final Link innerLink;


    public LogLink(Link innerLink) throws IOException {
        this.log = new OutputLog(Constants.PATH + "/outlog_p" + innerLink.getChannelId() + ".log");
        this.innerLink = innerLink;

    }
    @Override
    public void rSend(String ipDest, int portDest, Message message) throws IOException {
        this.innerLink.rSend(ipDest, portDest, message);
        this.log.write(ActionType.SEND + "/" + MessageType.MSG + " / (dest : " + ipDest + ":" + portDest + ") / content : { " + message + " }\n");
    }

    @Override
    public Optional<Message> waitForMessage(int timeout, boolean toAck) throws IOException {
        Optional<Message> rec = this.innerLink.waitForMessage(timeout, toAck);
        Message parsed = rec.orElse(new Message("_", MessageType.TIMEOUT, null, null));
        this.log.write("sender : " + parsed.getSender() + " / Osender : " + parsed.getOriginalSender() + " / " + ActionType.RECEIVE + " / type : " + parsed.getType() + " / content : { " + parsed.getPayload() + " } \n");
        return rec;
    }

    @Override
    public Optional<Message> waitForMessage() throws IOException {
        return this.waitForMessage(100000, false);
    }

    @Override
    public String getChannelId() {
        return this.innerLink.getChannelId();
    }

    public void close(){
        try {
            this.log.close();
            this.innerLink.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }
}
