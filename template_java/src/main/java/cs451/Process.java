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
    public void runAsSender(int m, ActiveHost receiver) throws IOException {
        int toSend = 0;
        while(toSend != m){
            // random payload
            String content = "HEY(" + pId + "):" + toSend;
            rlink.rSend(receiver.getIp(), receiver.getPort(), content);
            toSend ++;
            if(toSend == 1){
                System.out.println(java.time.LocalDateTime.now());
            }
        }
        System.out.println(java.time.LocalDateTime.now());
        System.out.println("SENT EVERYTHING");


    }

    public void runAsReceiver() throws IOException {
        int nbRec = 0;

        while(true){
            Optional<Message> received = this.rlink.waitForMessage(1000, true);
            if(received.isPresent()){
                nbRec++;
                delivered.add(received.get());

                if(received.get().getType() == MessageType.SIGINT || received.get().getType() == MessageType.SIGTERM){
                    // we receive the order to kill the node
                    return;
                }
            }

            if(nbRec == 10000) {
                System.out.println("RECEIVED EVERYTHING");
                return;
                }
            }
        }



    public int getpId() {
        return pId;
    }


}
