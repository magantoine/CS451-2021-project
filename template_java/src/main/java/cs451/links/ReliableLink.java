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

public class ReliableLink extends Link implements Observer<Message> {

    private final Link innerLink;
    private final ConcurrentHashMap<Triple<String, Integer, Message>, Pair<Boolean, Boolean>> acked = new ConcurrentHashMap<>();
    private final Set<Triple<Integer, Integer, Integer>> delivered = new HashSet<>();
    private final ActiveHost me;


    public ReliableLink(Link flLink, ActiveHost me){
        this.innerLink = flLink;
        this.me = me;

        Thread sender = new Thread(this::runSender);
        sender.setDaemon(true);
        sender.start();

    }


    @Override
    public void rSend(String ipDest, int portDest, Message message) throws IOException {
        // we put as (unacked, undelivered)
        acked.put(new Triple<String, Integer, Message>(ipDest, portDest, message), new Pair(false, false));

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
            }
        } else {
            // we received an ACK message

            // we reconstruct the message
            var msgPayload = message.getId();
            var senderIp = message.getSender().getIp();
            var senderPortNb = message.getSender().getPort();


            var searchedKey = new Triple(senderIp, senderPortNb, msgPayload);
            for (var msg : acked.entrySet()){
                var key = msg.getKey();

                var compareKey = new Triple(key._1(), key._2(), key._3().getId());

                if(compareKey.equals(searchedKey)){
                    // we found the message associated to the received ACK
                    acked.replace(key, new Pair(true, false)); // set as ACKED

                }
            }
        }
    }

    @Override
    public void deliver() {
        
    }

    private void runSender(){
        while(true){
            // we retransmit all the non acked messages
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            acked.forEach((msg, ack) ->{
                if(!ack._1() && !ack._2()){
                    if(me.getId() == 1){
                        System.out.println(msg._3());
                        System.out.println(acked);
                    }
                    try {
                        innerLink.rSend(msg._1(), msg._2(), msg._3());
                    } catch (IOException e){
                        e.printStackTrace();
                    }
                } else if (ack._1() && !ack._2()){
                    acked.replace(msg, new Pair(true, true));
                }
            });
        }
    }

    public void close(){
        this.innerLink.close();
    }


}
