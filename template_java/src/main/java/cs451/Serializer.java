package cs451;

public interface Serializer{

    public byte[] serialize(Message toSend);
    public String deserialize(byte[] received);

}
