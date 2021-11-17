package cs451;

import java.util.Objects;

public class Pair<S, T> {
    private final S s;
    private final T t;

    public Pair(S s, T t){
        this.s = s;
        this.t = t;
    }

    public S _1(){
        return s;
    }

    public T _2(){
        return t;
    }


    @Override
    public int hashCode() {
        return Objects.hash(s, t);
    }

    @Override
    public boolean equals(Object obj) {
        if(obj instanceof Pair){
            Pair<?, ?> objP = (Pair<?, ?>)obj;
            return objP._1().equals(this._1()) && objP._2().equals(this._2());
        } else {
            return false;
        }
    }

    @Override
    public String toString(){
        return "( " + this._1() + ", " + this._2() + " )";
    }
}
