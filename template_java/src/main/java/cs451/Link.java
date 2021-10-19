package cs451;

import java.io.IOException;

public interface Link {

    public void rSend(String ipDest, int portDest, String message) throws IOException;

    public String waitForMessage(int timeout, boolean toAck) throws IOException;

    public String waitForMessage() throws IOException;

    String getChannelId();

    public void close();
}
