package cs451;

import java.io.IOException;

public class LogLink implements Link {
    private final OutputLog log;
    private final Link innerLink;


    public LogLink(Link innerLink) throws IOException {
        this.log = new OutputLog(Constants.PATH + "/outlog_p" + innerLink.getChannelId() + ".log");
        this.innerLink = innerLink;

    }
    @Override
    public void rSend(String ipDest, int portDest, String message) throws IOException {
        this.innerLink.rSend(ipDest, portDest, message);
        this.log.write(ActionType.SEND + "/" + MessageType.MSG + " / (dest : " + ipDest + ":" + portDest + ") / content : { " + message + " }\n");
    }

    @Override
    public String waitForMessage(int timeout, boolean toAck) throws IOException {
        String rec = this.innerLink.waitForMessage(timeout, toAck);
        this.log.write(ActionType.RECEIVE + " / content : { " + rec + " } \n");
        return rec;
    }

    @Override
    public String waitForMessage() throws IOException {
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
