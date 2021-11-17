package cs451.broadcast;

import cs451.ActionType;
import cs451.ActiveHost;
import cs451.Message;
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
import cs451.util.Observable;


public class UrbBroadcastManager extends Observable<Pair<Message, ActionType>>{

    private final int pId;
    private final Link rlink;


    private final List<ActiveHost> allHosts;

    private final List<Pair<Integer, Integer>> delivered = new CopyOnWriteArrayList<>();
    private final List<Message> pending = new CopyOnWriteArrayList<>();
    private final ConcurrentMap<Pair<Integer, Integer>, List<Integer>> ack = new ConcurrentHashMap<>();
    private final ActiveHost associatedHost;



    public UrbBroadcastManager(int pId, Link rlink, List<ActiveHost> allHosts, String outPath, ActiveHost associatedHost){
        this.pId=pId;
        this.rlink = rlink;
        this.allHosts = allHosts;
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
                        //this.share("d " + keyPair._1() + " " + keyPair._2() + "\n");
                        this.share(new Pair(msg, ActionType.RECEIVE));


                    }
                }
            }

        }
    }


    public void addBroadcastedMessage(Message msg){
        this.share(new Pair(msg, ActionType.SEND));
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



    public List<ActiveHost> getAllHosts() {
        return allHosts;
    }

    public Link getRlink() {
        return rlink;
    }



    public void close(){
        rlink.close();
    }


}
