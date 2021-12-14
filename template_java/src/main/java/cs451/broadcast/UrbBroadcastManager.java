package cs451.broadcast;

import cs451.*;
import cs451.broadcast.Broadcaster;
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

    private final Set<Pair<Integer, Integer>> delivered = new HashSet<>();
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
        UrbListener listener = new UrbListener(this);


        listener.runListener();

        for (int i = 0; i < m; i++){
            String customPayload = "" + i; // payload is gonna be the index of the message
            Message msg = new Message(customPayload, MessageType.MSG, associatedHost, associatedHost);
            // add message to pending :
            getPending().add(msg);
            getAck().put(new Pair(msg.getOriginalSender().getId(), msg.getId()), new ArrayList(Arrays.asList(msg.getOriginalSender().getId())));

            // BebBrocast it
            for (ActiveHost h : getAllHosts()) {
                // sends the message to every host (thanks to Perfect Link property we know we'll have it)
                if(!h.equals(getAssociatedHost())) {
                    getRlink().rSend(h.getIp(), h.getPort(), msg);
                }
            }
            // we broadcast message from 1 to m
            addBroadcastedMessage(msg);
        }
        while(true){

        }
    }

    /**
     * runs uniform reliable broadcast to send m messages to all other processes
     * EVERY MESSAGE IN PENDING AND NOT DELIVERED YET FOR WHICH IT'S BEEN ACKED MY HALF OF PROCESS
     * GOES IN DELIVER
     * @param m number of message to send to the others
     * @param clocks values of the vector clocks that should come along each message
     *
     */
    public void urbBroadcast(int m, VectorClock[] clocks) throws IOException {

        // creates listener (to deliver messages) and broadcaster (to send messages)
        UrbListener listener = new UrbListener(this);


        listener.runListener();

        for (int i = 0; i < m; i++){
            String customPayload = "" + i; // payload is gonna be the index of the message
            Message msg = new Message(customPayload, MessageType.MSG, associatedHost, associatedHost, clocks[i]);
            // add message to pending :
            getPending().add(msg);
            getAck().put(new Pair(msg.getOriginalSender().getId(), msg.getId()), new ArrayList(Arrays.asList(msg.getOriginalSender().getId())));

            // BebBrocast it
            for (ActiveHost h : getAllHosts()) {
                // sends the message to every host (thanks to Perfect Link property we know we'll have it)
                if(!h.equals(getAssociatedHost())) {
                    getRlink().rSend(h.getIp(), h.getPort(), msg);
                }
            }
            // we broadcast message from 1 to m
            addBroadcastedMessage(msg);
        }
        while(true){

        }
    }


    public void addBroadcastedMessage(Message msg){
        this.share(new Pair(msg, ActionType.SEND));
    }
    public int getpId() {
        return pId;
    }


    public List<Message> getPending() {
        return pending;
    }

    public Map<Pair<Integer, Integer>, List<Integer>> getAck() {
        return ack;
    }

    public ActiveHost getAssociatedHost(){ return associatedHost; }

    public int getThreshold(){
        return allHosts.size() / 2;
    }

    public Set<Pair<Integer, Integer>> getDelivered(){
        return delivered;
    }

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
