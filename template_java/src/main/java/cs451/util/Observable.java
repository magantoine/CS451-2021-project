package cs451.util;


import cs451.Message;

import java.util.ArrayList;
import java.util.List;

public abstract class Observable<S> {
    public final List<Observer> observers = new ArrayList<>();

    public void register(Observer o){
        observers.add(o);
    }

    public void share(S s){
        observers.forEach(o -> o.receive(s));
    }
}
