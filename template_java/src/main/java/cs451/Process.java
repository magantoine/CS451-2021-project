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
    private final List<ActiveHost> doneHosts = new ArrayList<>();
    private final List<ActiveHost> allHosts;
    private final List<Triple<ActiveHost, ActionType, Message>> activity = new ArrayList<>();





    public Process(int pId, Link rlink, List<ActiveHost> allHosts){
        this.pId=pId;
        this.rlink = rlink;
        this.allHosts = allHosts;
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

        // we tell the receiver we're done
        rlink.rSend(receiver.getIp(), receiver.getPort(), "SIGDONE");

        System.out.println(java.time.LocalDateTime.now());
        System.out.println("SENT EVERYTHING");


    }

    public void runAsReceiver() throws IOException {
        while (true) {
            Optional<Message> received = this.rlink.waitForMessage(1000, true);
            if (received.isPresent()) {
                delivered.add(received.get());

                if (received.get().getType() == MessageType.SIGDONE && (!doneHosts.contains(received.get().getSender()))) {
                    // one of the sender is done
                    doneHosts.add(received.get().getSender());

                    //check if we're totally done
                    if(doneHosts.size() == allHosts.size()){
                        return;
                    }

                }
                if (received.get().getType() == MessageType.SIGINT || received.get().getType() == MessageType.SIGTERM) {
                    // we receive the order to kill the node
                    return;
                }
            }
        }
    }



    public int getpId() {
        return pId;
    }


}
