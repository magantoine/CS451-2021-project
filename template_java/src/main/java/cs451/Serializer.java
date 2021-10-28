package cs451;

public interface Serializer{

    public byte[] serialize(String toSend);
    public String deserialize(byte[] received);

}
