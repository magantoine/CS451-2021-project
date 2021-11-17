package cs451.util;

import cs451.Message;

public interface Observer {
    public void receive(Message message);
}



