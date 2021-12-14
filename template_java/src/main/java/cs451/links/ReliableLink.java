package cs451.links;


import cs451.*;
import cs451.util.Observer;
import cs451.util.Pair;
import cs451.util.Triple;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.atomic.AtomicInteger;

public class ReliableLink extends Link implements Observer<Message> {

    private final Link innerLink;
    private final ConcurrentHashMap<Triple<String, Integer, Message>, Boolean> acked = new ConcurrentHashMap<>();
    private final ActiveHost me;
    private final Set<Triple<Integer, Integer, Integer>> delivered = new HashSet<>();



    public ReliableLink(Link flLink, ActiveHost me){
        this.innerLink = flLink;
        this.me = me;


        Thread sender = new Thread(this::runSender);
        //sender.setDaemon(true);
        sender.start();

    }


    @Override
    public void rSend(String ipDest, int portDest, Message message) throws IOException {
        // we put as (unacked, undelivered)
        acked.put(new Triple<String, Integer, Message>(ipDest, portDest, message), false);

    }


    @Override
    public void receive(Message message) {
        if(!message.getType().equals(MessageType.ACK)){
            try{
                // send ACK
                this.innerLink.rSend(message.getSender().getIp(), message.getSender().getPort(), new Message(message.getPayload(), MessageType.ACK, this.me, this.me));
            } catch (IOException e){
                e.printStackTrace();
            }

            var key = new Triple(message.getSender().getId(), message.getOriginalSender().getId(), message.getId());
            if(!delivered.contains(key)) { // O(1)
                delivered.add(key);
                this.share(message);
                //System.out.println(me.getId() + ") receiving : " + message + " ( " + java.time.LocalDateTime.now() + ") ");

            }
        } else {
            // we received an ACK message

            // we reconstruct the message
            //var msgPayload = Integer.parseInt(message.getPayload());
            var msgPayload = message.getId() - 1;
            var senderIp = message.getSender().getIp();
            var senderPortNb = message.getSender().getPort();

            var searchedKey = new Triple(senderIp, senderPortNb, msgPayload);

            acked.replace(searchedKey, true); // set as ACKED
        }
    }

    @Override
    public void deliver() {
        
    }

    private void runSender(){

        while(true){
            // we retransmit all the non acked messages
            acked.forEach((msg, ack) ->{
                if(!ack) {
                    try {
                        innerLink.rSend(msg._1(), msg._2(), msg._3());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });

        }
    }

    public void close(){
        this.innerLink.close();
    }


}
