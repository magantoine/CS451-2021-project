package cs451;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;


public class Process {

    private final int pId;
    private final Link rlink;

    private final List<ActiveHost> doneHosts = new ArrayList<>();
    private final List<ActiveHost> allHosts;
    private final List<String> activity = new CopyOnWriteArrayList<>();
    private final String outpath;
    private final List<Message> delivered = new CopyOnWriteArrayList<>();
    private final List<Message> pending = new CopyOnWriteArrayList<>();
    private final ConcurrentMap<Pair<Integer, Integer>, List<Integer>> ack = new ConcurrentHashMap<>();
    private final ActiveHost associatedHost;
    boolean done = false;






    public Process(int pId, Link rlink, List<ActiveHost> allHosts, String outPath, ActiveHost associatedHost){
        this.pId=pId;
        this.rlink = rlink;
        this.allHosts = allHosts;
        this.outpath = outPath;
        this.associatedHost = associatedHost;
    }


    /**
     * runs uniform reliable broadcast to send m messages to all other processes
     * EVERY MESSAGE IN PENDING AND NOT DELIVERED YET FOR WHICH IT'S BEEN ACKED MY HALF OF PROCESS
     * GOES IN DELIVER
     * @param m number of message to send to the others
     */
    public void urbBroadcast(int m) throws IOException {

        // creates listener (to deliver messages) and broadcaster (to send messages)
        Broadcaster broadcaster = new UrbBroadcaster(this);
        Listener listener = new UrbListener(this);

        broadcaster.runBroadcaster(m);
        listener.runListener();

        while(true){
            // this aims to check if message can be delivered

            for(Message msg : pending){
                Pair keyPair = new Pair(msg.getOriginalSender().getId(), Integer.parseInt(msg.getPayload()) + 1);
                try{
                    ack.get(keyPair).size();
                } catch(NullPointerException e){
                    continue; // don't worry you'll have it the next round
                }

                if(ack.get(keyPair).size() > (allHosts.size() / 2)){
                    // at least half of the processes acked it
                    if(!delivered.contains(msg)){
                        // message hasn't been delivered yet ==> deliver it
                        delivered.add(msg);

                        // sequence number starts at 1 and our counter at 0
                        activity.add("d " + msg.getSender().getId() + " " + (Integer.parseInt(msg.getPayload()) + 1)+ "\n");


                        if(delivered.size() == m * (allHosts.size())){
                            done = true;
                            // we're done
                            //return;
                        }

                    }
                }
            }

        }








    }

    /**
     * runs the process as a sender
     * @param m number of messages to send to the receiver
     * @param receiver host that has to receive the message
     */
    public void runAsSender(int m, ActiveHost receiver, String outputPath, String payload, ActiveHost me) throws IOException {
        int toSend = 0;
        while(toSend != m){
            // random payload
            String content = payload + toSend;
            Message msg = new Message(payload + toSend, MessageType.MSG, me, me);
            rlink.rSend(receiver.getIp(), receiver.getPort(), msg);
            // counter starts at 0 but sequence number starts at 1
            activity.add("b " + (content + 1) + "\n");
            toSend ++;
            if(toSend == 1){

            }
        }

        // we tell the receiver we're done
        rlink.rSend(receiver.getIp(), receiver.getPort(), new Message("", MessageType.SIGDONE, me, me));

        /* no need to flush here, the activity of the process is
           handled, process and write out in the handleSignal function in Main
         */
        //flushActivity(outputPath);
        //System.out.println("SENT EVERYTHING");



    }

    public void runAsReceiver(String outputPath) throws IOException {
        int receivedOne = 0;
        while (true) {
            Optional<Message> received = this.rlink.waitForMessage(1000, true);
            if (received.isPresent()) {
                //delivered.add(received.get());
                receivedOne++;
                if(receivedOne == 1){
                    //System.out.println("start : " + java.time.LocalDateTime.now());
                }

                if(received.get().getType() == MessageType.MSG) {
                    activity.add("d " + received.get().getPayload() + " " + received.get().getSender().getId() + "\n");
                }


                if (received.get().getType() == MessageType.SIGDONE && (!doneHosts.contains(received.get().getSender()))) {
                    // one of the sender is done
                    doneHosts.add(received.get().getSender());

                    //check if we're totally done
                    //return;
                }

                if(doneHosts.size() == (allHosts.size() - 1)){
                    //System.out.println("Done : " + java.time.LocalDateTime.now());

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

    public List<Message> getDelivered() {
        return delivered;
    }

    public List<Message> getPending() {
        return pending;
    }

    public Map<Pair<Integer, Integer>, List<Integer>> getAck() {
        return ack;
    }

    public ActiveHost getAssociatedHost(){ return associatedHost; }



    public void addActivity(String act){
        activity.add(act);
    }
    public List<ActiveHost> getAllHosts() {
        return allHosts;
    }

    public Link getRlink() {
        return rlink;
    }

    public void flushActivity(String path){
        //System.out.println("Flushing the activity to :");
        //System.out.println(path);
        //System.out.println("For process of Pid :" + this.pId);
        try {
            FileWriter output = new FileWriter(path);
            for(String act : activity){
                output.write(act);
            }

            output.flush();
            output.close();


        } catch (IOException e) {
            //System.out.println("Couldn't write out activity");
        }
    }

    public void close(){
        rlink.close();
    }


}
