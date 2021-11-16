package cs451;

import cs451.Message;


import java.util.*;
import java.io.IOException;

public class UrbListener extends Listener {

    private final Process leader;

    public UrbListener(Process p){
        // creates a Urb Listener
        leader = p;

    }

    @Override
    void runListener() {
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
        while (!leader.done) {
            Optional<Message> received = leader.getRlink().waitForMessage();

            if (received.isPresent()) {
                received_card ++;


                //System.out.println(leader.getpId() + ") Pending : " + leader.getPending());
                //System.out.println(leader.getpId() + ") Delivered : " + leader.getDelivered());
                //System.out.println(leader.getpId() + ") acks : " + leader.getAck());

                if(received.get().getType() != MessageType.ACK){
                    Message receivedUnfold = received.get();
                    int senderPid = receivedUnfold.getSender().getId();
                    Pair keyPair = new Pair(receivedUnfold.getOriginalSender().getId(), Integer.parseInt(receivedUnfold.getPayload()) + 1);

                    System.out.println(leader.getpId() +" get acked : " + leader.getAck());


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

                        leader.getPending().add(receivedUnfold);// added to pending message

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



}

