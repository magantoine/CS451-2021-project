package cs451;

import java.io.IOException;

abstract class Listener {

    abstract void runListener();

    abstract public void deliver() throws IOException;
}
