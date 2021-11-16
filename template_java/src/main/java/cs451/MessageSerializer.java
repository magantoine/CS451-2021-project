package cs451;

import java.nio.charset.StandardCharsets;

public class MessageSerializer implements Serializer {

    @Override
    public byte[] serialize(Message toSend) {
        return toSend.toString().getBytes();
    }

    @Override
    public String deserialize(byte[] received) {
        return (new String(received, StandardCharsets.UTF_8)).trim();
    }
}
