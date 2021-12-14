package cs451.broadcast;

import cs451.ActionType;
import cs451.ActiveHost;
import cs451.Message;
import cs451.VectorClock;
import cs451.links.Link;

import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

import cs451.util.Observer;
import cs451.util.Pair;

public class LocalCausalBroadcast implements Observer<Pair<Message, ActionType>> {

    private final int pId;
    private final List<ActiveHost> allHosts;
    private final String outpath;
    private final ActiveHost associatedHost;
    private final UrbBroadcastManager urbManager;
    private final VectorClock vectorClock;
    private final List<Message> pending;
    private final StringBuilder activity = new StringBuilder();
    private final int [][] dependencies;

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


        // initialization of pending
        pending = new ArrayList<Message>();

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
            pending.add(rec._1());
            Iterator<Message> pendingIterator = pending.iterator();
            while(pendingIterator.hasNext()){
                var msg = pendingIterator.next();
                if(this.getVectorClock().smallerThan(msg.getVectorClock(), this.dependencies[msg.getOriginalSender().getId() - 1])){
                    pendingIterator.remove();
                    vectorClock.increment(msg.getOriginalSender().getId());
                }
            }
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

    public List<Message> getPending() {
        return pending;
    }

    public StringBuilder getActivity() {
        return activity;
    }

    public int[] getDependencies() {
        return dependencies[getpId() - 1];
    }
}
