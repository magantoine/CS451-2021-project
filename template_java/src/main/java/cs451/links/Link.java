package cs451.links;

import cs451.Message;
import cs451.util.Observable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class Link extends Observable {

    abstract public void rSend(String ipDest, int portDest, Message message) throws IOException;

    abstract public void deliver();

    abstract public void close();
}
