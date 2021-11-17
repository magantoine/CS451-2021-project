package cs451.broadcast;

import cs451.ActionType;
import cs451.ActiveHost;
import cs451.Message;
import cs451.links.Link;
import cs451.util.Observer;
import cs451.util.Pair;
import cs451.util.Triple;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class FifoBroadcast implements Observer<Pair<Message, ActionType>> {
    private final List<String> activity = new CopyOnWriteArrayList<>();
    private final int pId;
    private final List<ActiveHost> allHosts;
    private final String outpath;
    private final List<Triple<ActiveHost, Message, Integer>> pending = new CopyOnWriteArrayList<>();
    private final ActiveHost associatedHost;
    private final UrbBroadcastManager urbManager;

    public FifoBroadcast(int pId, Link rlink, List<ActiveHost> allHosts, String outPath, ActiveHost associatedHost){
        this.pId=pId;
        this.allHosts = allHosts;
        this.associatedHost = associatedHost;
        this.outpath = outPath;

        // CREATE THE URB MANAGER
        this.urbManager = new UrbBroadcastManager(pId, rlink, allHosts, outPath, associatedHost);

        urbManager.register(this);
    }


    public void fifoBroadcast(int m){
        // we want to broadcast the m messages

    }



    @Override
    public void receive(Pair<Message, ActionType> rec) {
        // called when URB *delivers* a message or *broadcasts* a message
        if(rec._2().equals(ActionType.SEND)){
            activity.add("b " + (Integer.parseInt(rec._1().getPayload()) + 1) + "\n");
        } else {
            // we receive a message delivered by URB

        }
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
}
