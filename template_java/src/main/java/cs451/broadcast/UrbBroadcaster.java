package cs451.broadcast;

import cs451.*;
import cs451.broadcast.UrbBroadcastManager;
import cs451.util.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class UrbBroadcaster implements Broadcaster {

    private final UrbBroadcastManager leader;
    private final ActiveHost me;

    public UrbBroadcaster(UrbBroadcastManager p){
        this.me = p.getAssociatedHost();
        leader = p;
    }

    @Override
    public void runBroadcaster(int m) {
        Thread broadcasterThread = new Thread(() -> {
            try {
                broadcast(m);
            } catch (IOException e) {
                //System.out.println("Exception occured in Broadcaster");
            }
        });

        broadcasterThread.setDaemon(true);
        broadcasterThread.start();
    }

    @Override
    /**
     * Is in charge of broadcasting the messages to send
     *
     * ADDS MESSAGE TO PENDING AND THEN BEBBROADCAST IT
     *
     * @param m : number of messages to send
     */
    public void broadcast(int m) throws IOException {
        for (int i = 0; i < m; i++){
            String customPayload = "" + i; // payload is gonna be the index of the message
            Message msg = new Message(customPayload, MessageType.MSG, me, me);

            // add message to pending :
            leader.getPending().add(msg);
            leader.getAck().put(new Pair(msg.getOriginalSender().getId(), Integer.parseInt(msg.getPayload()) + 1), new ArrayList(Arrays.asList(msg.getOriginalSender().getId())));

            // BebBrocast it
            for (ActiveHost h : leader.getAllHosts()) {
                // sends the message to every host (thanks to Perfect Link property we know we'll have it)
                if(!h.equals(leader.getAssociatedHost())) {
                    leader.getRlink().rSend(h.getIp(), h.getPort(), msg);
                }
            }
            // we broadcast message from 1 to m
            leader.addBroadcastedMessage(msg);//"b " + (Integer.parseInt(customPayload) + 1) + "\n");
        }



        // if we get here we sent everything
    }


}
