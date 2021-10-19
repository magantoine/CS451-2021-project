package cs451;

interface Serializer{

    public byte[] serialize(String toSend);
    public String deserialize(byte[] received);

}
