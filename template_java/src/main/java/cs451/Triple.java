package cs451;

public class Triple<A, B, C> {
    private final A a;
    private final B b;
    private final C c;

    public Triple(A a, B b, C c){
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public Object get(int i){
        if(!(i == 0 || i == 1 || i == 2)){
            throw new IndexOutOfBoundsException();
        }
        return i == 0 ? a : i == 1 ? b : c;
    }
}
