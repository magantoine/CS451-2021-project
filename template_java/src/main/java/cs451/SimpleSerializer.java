package cs451.Serialization;

import cs451.Serialization.Serializer;

import java.nio.charset.StandardCharsets;

public class SimpleSerializer implements Serializer {

    public byte[] serialize(String message){
        return message.getBytes();
    }

    public String deserialize(byte[] message){
        return (new String(message, StandardCharsets.UTF_8)).trim();
    }
}
