package cs451;

import cs451.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

abstract class Link {
    public final List<LinkObserver> observers = new ArrayList<>();

    abstract public void rSend(String ipDest, int portDest, Message message) throws IOException;

    abstract public void deliver();

    public void register(LinkObserver observer){
        observers.add(observer);
    }


    abstract public void close();
}
