package cs451.links;


import cs451.*;
import cs451.util.Observer;
import cs451.util.OutputLog;

import java.io.IOException;

public class LogLink extends Link implements Observer<Message> {
    private final OutputLog log;
    private final Link innerLink;


    public LogLink(Link innerLink, int id) throws IOException {
        this.log = new OutputLog(Constants.PATH + "/outlog_p" + id + ".log");
        this.innerLink = innerLink;


    }
    @Override
    public void rSend(String ipDest, int portDest, Message message) throws IOException {
        this.innerLink.rSend(ipDest, portDest, message);
        this.log.write(ActionType.SEND + "/" + MessageType.MSG + " / (dest : " + ipDest + ":" + portDest + ") / content : { " + message + " }\n");
    }

    @Override
    public void deliver() {

    }

    public void close(){
        try {
            this.log.close();
            this.innerLink.close();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void receive(Message message) {
        try{
            this.log.write(ActionType.RECEIVE + "/" + message.getType() + " / / content : { " + message + " }\n");
        } catch (IOException e){
            e.printStackTrace();
        }


        //observers.forEach(o -> o.receive(message));
        this.share(message);
    }
}
