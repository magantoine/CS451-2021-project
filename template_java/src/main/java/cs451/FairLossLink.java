package cs451;

import java.io.IOException;
import java.net.*;
import java.util.Optional;

public class FairLossLink implements Link{

    private final String channelId;
    private final Serializer serializer;
    private int portNumber;
    private final DatagramSocket socket;




    public FairLossLink(String channelId, int portNumber, Serializer serializer) throws SocketException {
        // socketException due to the create of a datagram socket
        this.channelId = channelId;
        this.serializer = serializer;
        this.portNumber = portNumber;
        this.socket = new DatagramSocket(portNumber);

    }


    public void rSend(String ipDest, int portDest, String message) throws IOException {
        boolean acked = false;
        MessageType type = message.contains("ACK") ? MessageType.ACK : MessageType.MSG;
        message += ">>>" + type.toString();
        byte[] serialized = serializer.serialize(message);




        DatagramPacket packet = new DatagramPacket(serialized, serialized.length, InetAddress.getByName(ipDest), portDest);
        socket.send(packet);
    }


    /**
     * gets a byte array from the channel and delivers it
     * @return
     */
    private String rDeliver(byte [] received){
        return serializer.deserialize(received);
    }

    public Optional<Message> waitForMessage(int timeout, boolean toAck) throws IOException {

        byte[] buffer = new byte[Constants.MAX_PACKET_SIZE];


        socket.setSoTimeout(timeout);
        DatagramPacket packet = null;



        try{
            packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

        } catch(SocketTimeoutException e) {

            return Optional.empty();
        }

        if(packet.getData() == null){
            return Optional.empty();
        }
        String messageContent = rDeliver(packet.getData()).split(">>>")[0];
        MessageType messageType = MessageType.valueOf(rDeliver(packet.getData()).split(">>>")[1]);


        if(toAck && messageType != MessageType.ACK) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            rSend(packet.getAddress().getHostAddress(), packet.getPort(), "_>>>" + MessageType.ACK.toString());
        }

        // we return build message once we know every component of it
        return Optional.of(new Message(messageContent, messageType, new Host(packet.getPort() % Constants.BASE_PORT, packet.getAddress().getHostAddress(), packet.getPort())));
    }

    public Optional<Message> waitForMessage() throws IOException {
        return this.waitForMessage(10000, false);
    }

    public Optional<Message> waitForMessage(int timeout) throws IOException {
        return this.waitForMessage(timeout, false);
    }


    public String getChannelId() {
        return channelId;
    }

    public Serializer getSerializer() {
        return serializer;
    }


    public int getPortNumber() {
        return portNumber;
    }

    public void close(){
        socket.close();
    }
}
