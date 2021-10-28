package cs451.net;

import cs451.MessageType;
import cs451.Links.Link;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class Process {

    private final int pId;
    private final Link rlink;
    private final List<Message> delivered = new ArrayList();
    private final List<ActiveHost> doneHosts = new ArrayList<>();
    private final List<ActiveHost> allHosts;
    private final StringBuilder activity = new StringBuilder();
    private final String outpath;




    public Process(int pId, Link rlink, List<ActiveHost> allHosts, String outPath){
        this.pId=pId;
        this.rlink = rlink;
        this.allHosts = allHosts;
        this.outpath = outPath;
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
            activity.append("b " + content + "\n");
            toSend ++;
            if(toSend == 1){

            }
        }

        // we tell the receiver we're done
        rlink.rSend(receiver.getIp(), receiver.getPort(), ">>>SIGDONE");

        /* no need to flush here, the activity of the process is
           handled, process and write out in the handleSignal function in Main
         */
        //flushActivity(outputPath);
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
                    activity.append("d " + received.get().getPayload() + " " + received.get().getSender().getId() + "\n");
                }


                if (received.get().getType() == MessageType.SIGDONE && (!doneHosts.contains(received.get().getSender()))) {
                    // one of the sender is done
                    doneHosts.add(received.get().getSender());

                    //check if we're totally done
                    //return;
                }

                if(doneHosts.size() == (allHosts.size() - 1)){
                    System.out.println("Done : " + java.time.LocalDateTime.now());

                    /* no need to flush here, the activity of the process is
                     handled, process and write out in the handleSignal function in Main
                     */
                    //flushActivity(outputPath);

                    return; //ACTUALLY DON'T

                }

                }
                /**
                if (received.get().getType() == MessageType.SIGINT || received.get().getType() == MessageType.SIGTERM) {
                    // we receive the order to kill the node
                    return;
                }*/

        }
    }




    public int getpId() {
        return pId;
    }


    public void flushActivity(String path){
        System.out.println("Flushing the activity to :");
        System.out.println(path);
        System.out.println("For process of Pid :" + this.pId);
        try {
            FileWriter output = new FileWriter(path);
            output.write(activity.toString());
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
