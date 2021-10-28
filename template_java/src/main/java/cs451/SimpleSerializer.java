package cs451;

import cs451.Serializer;

import java.nio.charset.StandardCharsets;

public class SimpleSerializer implements Serializer {

    public byte[] serialize(String message){
        return message.getBytes();
    }

    public String deserialize(byte[] message){
        return (new String(message, StandardCharsets.UTF_8)).trim();
    }
}
