package cs451;

import cs451.broadcast.Broadcaster;
import cs451.broadcast.UrbBroadcaster;
import cs451.broadcast.UrbListener;
import cs451.links.Link;
import cs451.util.Pair;

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
    private final List<Pair<Integer, Integer>> delivered = new CopyOnWriteArrayList<>();
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
        UrbListener listener = new UrbListener(this);

        broadcaster.runBroadcaster(m);
        listener.runListener();

        while(true){
            // this aims to check if message can be delivered

            for(Message msg : pending){
                var keyPair = new Pair(msg.getOriginalSender().getId(), Integer.parseInt(msg.getPayload()) + 1);
                try{
                    ack.get(keyPair).size();
                } catch(NullPointerException e){
                    continue; // don't worry you'll have it the next round
                }

                if(ack.get(keyPair).size() >= (allHosts.size() / 2)){
                    // at least half of the processes acked it

                    if(!delivered.contains(keyPair)){
                        // message hasn't been delivered yet ==> deliver it
                        delivered.add(keyPair);
                        // sequence number starts at 1 and our counter at 0
                        activity.add("d " + keyPair._1() + " " + keyPair._2() + "\n");


                    }
                }
            }

        }
    }

    public int getpId() {
        return pId;
    }

    public List<Pair<Integer, Integer>> getDelivered() {
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
