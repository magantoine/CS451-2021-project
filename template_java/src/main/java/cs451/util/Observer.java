package cs451.util;

import cs451.Message;

public interface Observer<S> {
    public void receive(S s);
}



