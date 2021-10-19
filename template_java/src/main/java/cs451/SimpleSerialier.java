package cs451;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

class SimpleSerialier implements Serializer {

    public byte[] serialize(String message){
        return message.getBytes();
    }

    public String deserialize(byte[] message){
        return (new String(message, StandardCharsets.UTF_8)).trim();
    }
}
