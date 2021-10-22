package cs451;

import java.io.File;
import java.io.FileWriter;
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
    private final List<String> activity = new ArrayList<>();





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
    public void runAsSender(int m, ActiveHost receiver, String outputPath, String payload) throws IOException {
        int toSend = 0;
        while(toSend != m){
            // random payload
            String content = payload + toSend;
            rlink.rSend(receiver.getIp(), receiver.getPort(), content);
            activity.add("b " + content + "\n");
            toSend ++;
            if(toSend == 1){

            }
        }

        // we tell the receiver we're done
        rlink.rSend(receiver.getIp(), receiver.getPort(), ">>>SIGDONE");

        flushActivity(outputPath);
        System.out.println("SENT EVERYTHING");


    }

    public void runAsReceiver(String outputPath) throws IOException {
        int receivedOne = 0;
        while (true) {
            Optional<Message> received = this.rlink.waitForMessage(1000, true);
            if (received.isPresent()) {
                delivered.add(received.get());
                receivedOne++;
                if(receivedOne == 1){
                    System.out.println("start : " + java.time.LocalDateTime.now());
                }

                if(received.get().getType() == MessageType.MSG) {
                    activity.add("d " + received.get().getPayload() + " " + received.get().getSender().getId() + "\n");
                }


                if (received.get().getType() == MessageType.SIGDONE && (!doneHosts.contains(received.get().getSender()))) {
                    // one of the sender is done
                    doneHosts.add(received.get().getSender());

                    //check if we're totally done


                    if(doneHosts.size() == (allHosts.size() - 1)){
                        System.out.println("Done : " + java.time.LocalDateTime.now());
                        flushActivity(outputPath);
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


    private void flushActivity(String path){
        try {
            FileWriter output = new FileWriter(path);
            for(String act : this.activity){
                output.write(act);
            }
            output.flush();
            output.close();


        } catch (IOException e) {
            System.out.println("Couldn't write out activity");
        }
    }

    public void close(){
        rlink.close();
    }


}
