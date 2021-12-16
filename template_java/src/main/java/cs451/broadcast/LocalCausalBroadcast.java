package cs451.broadcast;

import cs451.ActionType;
import cs451.ActiveHost;
import cs451.Message;
import cs451.VectorClock;
import cs451.links.Link;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.PriorityBlockingQueue;

import cs451.util.Observer;
import cs451.util.Pair;

public class LocalCausalBroadcast implements Observer<Pair<Message, ActionType>> {

    private final int pId;
    private final List<ActiveHost> allHosts;
    private final String outpath;
    private final ActiveHost associatedHost;
    private final UrbBroadcastManager urbManager;
    private final VectorClock vectorClock;
    private final HashMap<Integer, PriorityQueue<Message>> pending;
    private final StringBuilder activity = new StringBuilder();
    private final int [][] dependencies;
    private int delivered;

    public LocalCausalBroadcast(int pId, Link rlink, List<ActiveHost> allHosts, String outPath, ActiveHost associatedHost, int [][] dependencies){
        this.pId=pId;
        this.allHosts = allHosts;
        this.associatedHost = associatedHost;
        this.outpath = outPath;
        this.dependencies = dependencies;


        // CREATE THE URB MANAGER
        this.urbManager = new UrbBroadcastManager(pId, rlink, allHosts, outPath, associatedHost);
        urbManager.register(this);

        // initialization of the vector as 0 for every one
        vectorClock = new VectorClock(allHosts.size());
        pending = new HashMap<Integer, PriorityQueue<Message>>();
        // This structure avoids scanning everything every time
        for (var host: allHosts) {
            pending.put(host.getId(), new PriorityQueue<Message>(10, (m1, m2) -> m1.getId() < m2.getId() ? -1 : m1.getId() == m2.getId() ? 0 : +1));
        }
        delivered = 0;

    }

    public void lcbBroadcast(int m){
        VectorClock clocks [] = new VectorClock[m];
        for(int i = 0; i < m; ++i){
            clocks[i] = new VectorClock(allHosts.size());
            clocks[i].set(associatedHost.getId(), i);
        }

        try{
            urbManager.urbBroadcast(m, clocks);
        } catch(IOException e){
            System.out.println(associatedHost.getId() +") Failed broadcasting all the messages");
        }
    }

    @Override
    public void receive(Pair<Message, ActionType> rec) {
        if(rec._2().equals(ActionType.SEND)){
            activity.append("b " + rec._1().getId() + "\n");
        } else {


            pending.get(rec._1().getOriginalSender().getId()).add(rec._1());

            var newRoundPossible = false;
            do{
                newRoundPossible = false;
                for(var hostPending : pending.values()) {
                    var messageIterator = hostPending.iterator();
                    boolean canDeliver = true;
                    while (messageIterator.hasNext() && canDeliver) {

                        var currentMessage = messageIterator.next();
                        var currentOriginalSenderId = currentMessage.getOriginalSender().getId();
                        
                        if (currentMessage.getVectorClock().smallerThan(this.getVectorClock(), dependencies[currentOriginalSenderId - 1])) {
                            activity.append("d " + currentOriginalSenderId + " " + currentMessage.getId() + "\n");
                            delivered ++;
                            if(delivered % 100 == 0){
                                System.out.println(getpId() +") delviered " + delivered + " messages");
                            }
                            this.getVectorClock().increment(currentOriginalSenderId);
                            messageIterator.remove();
                            canDeliver = true;
                            newRoundPossible = true;
                        } else {
                            canDeliver = false;
                        }
                    }
                }
            }while(newRoundPossible);
        }
    }

    public void flushActivity(String path){

        try {
            FileWriter output = new FileWriter(path);
            output.write(activity.toString());
            output.flush();
            output.close();
        } catch (IOException e) {
            System.out.println("Couldn't write out activity");
        }
    }

    public int getpId() {
        return pId;
    }

    public List<ActiveHost> getAllHosts() {
        return allHosts;
    }

    public String getOutpath() {
        return outpath;
    }

    public ActiveHost getAssociatedHost() {
        return associatedHost;
    }

    public UrbBroadcastManager getUrbManager() {
        return urbManager;
    }

    public VectorClock getVectorClock() {
        return vectorClock;
    }

    public Map<Integer, PriorityQueue<Message>> getPending() {
        return pending;
    }

    public StringBuilder getActivity() {
        return activity;
    }

    public int[] getDependencies() {
        return dependencies[getpId() - 1];
    }
}
