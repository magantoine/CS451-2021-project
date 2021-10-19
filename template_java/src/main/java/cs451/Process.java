package cs451;

import java.io.IOException;
import java.net.InetAddress;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Process {

    private final int pId;
    private final List<String> delivered;
    private final Link rlink;
    private final List<Message> received = new ArrayList();




    public Process(int pId, Link rlink){
        this.pId=pId;
        this.delivered = new ArrayList<String>();
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
        Instant inst1 = null;
        while(true){
            String received = this.rlink.waitForMessage(1000, true);
            if(received != null){
                nbRec++;
            }
            if(nbRec == 1){
                inst1 = Instant.now();
            if(nbRec == 20) {
                Instant inst2 = Instant.now();
                System.out.println("Elapsed Time: "+ Duration.between(inst1, inst2).toString());
                return;
                }
            }
        }
    }


    public int getpId() {
        return pId;
    }


}
