package cs451;

import cs451.Serializer;

import java.nio.charset.StandardCharsets;

public class ContentSerializer implements Serializer {

    public byte[] serialize(Message message){
        return message.getPayload().getBytes();
    }

    public String deserialize(byte[] message){
        return (new String(message, StandardCharsets.UTF_8)).trim();
    }
}
