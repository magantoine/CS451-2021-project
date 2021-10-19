package cs451;

import java.io.IOException;
import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class Process {

    private final int pId;
    private final Link rlink;
    private final List<Message> delivered = new ArrayList();




    public Process(int pId, Link rlink){
        this.pId=pId;
        this.rlink = rlink;
    }

    /**
     * runs the process as a sender
     * @param m number of messages to send to the receiver
     * @param receiver host that has to receive the message
     */
    public void runAsSender(int m, Host receiver) throws IOException {
        int toSend = 0;
        while(toSend != m){
            // random payload
            String content = "HEY(" + pId + "):" + toSend;

            rlink.rSend(receiver.getIp(), receiver.getPort(), content);
            toSend ++;
        }
    }

    public void runAsReceiver() throws IOException {
        int nbRec = 0;

        while(true){
            Optional<Message> received = this.rlink.waitForMessage(1000, true);
            if(received.isPresent()){
                nbRec++;
                delivered.add(received.get());
            }
            if(nbRec == 20) {
                return;
                }
            }
        }



    public int getpId() {
        return pId;
    }


}
