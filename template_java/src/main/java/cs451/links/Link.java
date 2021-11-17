package cs451.links;

import cs451.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class Link {
    public final List<LinkObserver> observers = new ArrayList<>();

    abstract public void rSend(String ipDest, int portDest, Message message) throws IOException;

    abstract public void deliver();

    public void register(LinkObserver observer){
        observers.add(observer);
    }


    abstract public void close();
}
