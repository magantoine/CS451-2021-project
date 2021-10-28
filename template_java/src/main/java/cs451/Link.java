package cs451;

import cs451.Message;

import java.io.IOException;
import java.util.Optional;

public interface Link {

    public void rSend(String ipDest, int portDest, String message) throws IOException;

    public Optional<Message> waitForMessage(int timeout, boolean toAck) throws IOException;

    public Optional<Message> waitForMessage() throws IOException;

    String getChannelId();

    public void close();
}
