package cs451.broadcast;

import java.io.IOException;

public interface Broadcaster {

    public void runBroadcaster(int m);
    public void broadcast(int m) throws IOException;
}
