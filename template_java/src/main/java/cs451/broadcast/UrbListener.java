package cs451.broadcast;

import cs451.*;
import cs451.broadcast.UrbBroadcastManager;
import cs451.util.Observer;
import cs451.util.Pair;


import java.util.*;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

public class UrbListener extends Listener implements Observer {

    private final UrbBroadcastManager leader;
    private final ArrayBlockingQueue<Message> messages = new ArrayBlockingQueue<Message>(3000);

    public UrbListener(UrbBroadcastManager p){
        // creates a Urb Listener
        leader = p;

        p.getRlink().register(this);

    }

    @Override
    public void runListener() {
        Thread listenerThread = new Thread(() -> {
            try {
                deliver();
            } catch (IOException e) {
                //System.out.println("Exception occured in listener");
            }
        });

        listenerThread.setDaemon(true);
        listenerThread.start();
    }

    @Override
    /**
     * Is in charge of receiving the broadcasted messages
     * @throws IOException
     */
    public void deliver() throws IOException {
        int received_card = 0;
        while (true) {
            Message received = null;

            try {
                received = messages.poll((long)1000, TimeUnit.MILLISECONDS);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }


            if (received != null) {
                received_card ++;

                if(received.getType() != MessageType.ACK){
                    var receivedUnfold = received;

                    int senderPid = receivedUnfold.getSender().getId();
                    Pair keyPair = new Pair(receivedUnfold.getOriginalSender().getId(), Integer.parseInt(receivedUnfold.getPayload()) + 1);


                    // ALREADY AN ENTRY IN ACK OR NOT ? CREATE IT
                    if(leader.getAck().get(keyPair) == null){
                        leader.getAck().put(keyPair, new ArrayList<>());
                    }

                    // YOU KNOW YOU RECEIVE IT FROM THE SENDER SO IT'S AN ACK RIGHT AWA
                    if (!leader.getAck().get(keyPair).contains(senderPid)) {
                        // the process whom sent us this message just acked
                        leader.getAck().get(keyPair).add(senderPid);
                    }

                    // IF THE MESSAGE IS NOT PENDING YET (YOU JUST RECEIVE IT)
                    if (!leader.getPending().contains(receivedUnfold)) {
                        // the received message is not pending

                        leader.getPending().add(receivedUnfold); // added to pending message

                        Message msg = new Message(receivedUnfold.getPayload(), receivedUnfold.getType(), leader.getAssociatedHost(), receivedUnfold.getOriginalSender());
                        // then we BEBBroadcast :

                        leader.getAllHosts().forEach(h -> {
                            // we don't resend it to the guy who sent it to us (he's already in ACK)
                            if(!h.equals(leader.getAssociatedHost())) {
                                try {
                                    leader.getRlink().rSend(h.getIp(), h.getPort(), msg);
                                } catch (IOException e) {
                                }
                            }
                        });
                    }
                }
            }
        }
    }


    @Override
    public void receive(Message message) {
        try {
            messages.put(message);
        } catch (InterruptedException e){
            e.printStackTrace();
        }
    }
}

