package cs451.broadcast;

import java.io.IOException;

public abstract class Listener {

    abstract void runListener();

    abstract public void deliver() throws IOException;
}
