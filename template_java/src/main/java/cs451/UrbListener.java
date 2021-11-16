package cs451.urb;

import cs451.Message;

public class UrbListener {

    private final Process leader;

    public UrbListener(Process p){
        // creates a Urb Listener
        leader = p;
    }

    public void deliver(Message m){
        leader.getAck().get(m)
    }



}
