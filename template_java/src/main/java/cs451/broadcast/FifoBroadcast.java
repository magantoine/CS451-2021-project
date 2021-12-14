package cs451.broadcast;

import cs451.ActionType;
import cs451.ActiveHost;
import cs451.Message;
import cs451.VectorClock;
import cs451.links.Link;
import cs451.util.Observer;
import cs451.util.Pair;
import cs451.util.Triple;

import java.util.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.CopyOnWriteArrayList;

public class FifoBroadcast implements Observer<Pair<Message, ActionType>> {
    private final StringBuilder activity = new StringBuilder();
    private final int pId;
    private final List<ActiveHost> allHosts;
    private final String outpath;
    private final List<Message> pending = new LinkedList<>();
    private final ActiveHost associatedHost;
    private final UrbBroadcastManager urbManager;
    private final int next [];
    private int delivered = 0;


    public FifoBroadcast(int pId, Link rlink, List<ActiveHost> allHosts, String outPath, ActiveHost associatedHost){
        this.pId=pId;
        this.allHosts = allHosts;
        this.associatedHost = associatedHost;
        this.outpath = outPath;
        next = new int [allHosts.size()];

        for(int i = 0; i < allHosts.size(); ++i){
            next[allHosts.get(i).getId() - 1] =  1;
        }


        // CREATE THE URB MANAGER
        this.urbManager = new UrbBroadcastManager(pId, rlink, allHosts, outPath, associatedHost);

        urbManager.register(this);

    }


    public void fifoBroadcast(int m){
        //VectorClock clocks [] = new VectorClock[m];
        //for(int i = 0; i < m; ++i){
        //  clocks[i] = new VectorClock(allHosts.size());
        //    clocks[i].set(associatedHost.getId(), i);
        //}

        //try{
        //    urbManager.urbBroadcast(m, clocks);
        //} catch(IOException e){
        //    System.out.println(associatedHost.getId() +") Failed broadcasting all the messages");
        //}
        // we want to broadcast the m messages [MAIN THREAD]
        try {
            urbManager.urbBroadcast(m);
        } catch (IOException e){
            e.printStackTrace();
        }
    }



    @Override
    public void receive(Pair<Message, ActionType> rec) {
        // called when URB *delivers* a message or *broadcasts* a message

        if(rec._2().equals(ActionType.SEND)){
            activity.append("b " + rec._1().getId() + "\n");
        } else {
            // we receive a message delivered by URB
            pending.add(rec._1()); // we add the message to the pending message
            var earlyMessage = getEarlierMessage(rec._1());
            while(earlyMessage.isPresent()){
                activity.append("d " + earlyMessage.get().getOriginalSender().getId() + " " + earlyMessage.get().getId() + "\n");
                delivered++;
                earlyMessage = getEarlierMessage(rec._1());
            }

            if(delivered % 100 == 0 && delivered != 0){
                System.out.println(getpId() + ") delivered " + delivered + " messages");

            }

        }
    }

    private Optional<Message> getEarlierMessage(Message recievedMessage){
        for(var heldMessage : pending){
            var receivedSender = recievedMessage.getOriginalSender();

            if(receivedSender.getId() - 1 < next.length && receivedSender.getId() - 1 >= 0) {
                if (heldMessage.getOriginalSender().equals(receivedSender)) {
                    // both message come from the same person
                    if (heldMessage.getId() == next[receivedSender.getId() - 1]) {
                        // the next message that should be deliver is this one
                        pending.remove(heldMessage); // O(1) LinkedList
                        next[receivedSender.getId() - 1] = heldMessage.getId() + 1;
                        return Optional.of(heldMessage);
                    }
                }
            } else {
                System.out.println(receivedSender  + " not in " + next);
            }

        }
        return Optional.empty();
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

    public void close(){
        this.urbManager.close();
    }

    public int getpId() {
        return pId;
    }




}
