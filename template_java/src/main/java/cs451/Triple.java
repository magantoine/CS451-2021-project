package cs451;

import java.util.Objects;

public class Triple<S, T, R> {
    private final S s;
    private final T t;
    private final R r;

    public Triple(S s, T t, R r){
        this.s = s;
        this.t = t;
        this.r = r;
    }

    public S _1(){
        return s;
    }
    public T _2(){
        return t;
    }
    public R _3(){
        return r;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Triple<?, ?, ?> triple = (Triple<?, ?, ?>) o;
        return Objects.equals(s, triple.s) &&
                Objects.equals(t, triple.t) &&
                Objects.equals(r, triple.r);
    }

    @Override
    public int hashCode() {
        return Objects.hash(s, t, r);
    }


    @Override
    public String toString(){
        return "(" + s.toString() + ", " + t.toString() + ", " + r.toString() + ")";
    }
}
