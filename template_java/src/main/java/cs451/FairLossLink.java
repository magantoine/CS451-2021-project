package cs451;

import cs451.Serializer;

import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class FairLossLink implements Link {

    private final String channelId;
    private final Serializer serializer;
    private int portNumber;
    private final DatagramSocket socket;
    private byte [] sendPacket = new byte[Constants.MAX_PACKET_SIZE];
    private final byte [] receivePacket = new byte[Constants.MAX_PACKET_SIZE];
    private final ActiveHost me;
    private final List<Message> delivered = new ArrayList<Message>();





    public FairLossLink(String channelId, int portNumber, Serializer serializer, ActiveHost me) throws SocketException {
        // socketException due to the create of a datagram socket
        this.channelId = channelId;
        this.serializer = serializer;
        this.portNumber = portNumber;
        this.socket = new DatagramSocket(portNumber);
        this.me = me;

    }


    public void rSend(String ipDest, int portDest, Message message) throws IOException {
        sendPacket = serializer.serialize(message);
        DatagramPacket packet = new DatagramPacket(sendPacket, sendPacket.length, InetAddress.getByName(ipDest), portDest);
        socket.send(packet);

        // we empty the sendPacket once again
        Arrays.fill(sendPacket, (byte)0);
    }


    /**
     * gets a byte array from the channel and delivers it
     * @return
     */
    private String rDeliver(byte [] received){
        return serializer.deserialize(received);
    }

    public Optional<Message> waitForMessage(int timeout, boolean toAck) throws IOException {
        socket.setSoTimeout(timeout);
        DatagramPacket packet = null;

        try{
            packet = new DatagramPacket(receivePacket, receivePacket.length);
            socket.receive(packet);

        } catch(SocketTimeoutException e) {
            return Optional.empty();
        }



        if(packet.getData() == null){

            return Optional.empty();
        }

        String data = serializer.deserialize(packet.getData());
        Message received = null;
        String receivedComponents [] = data.split(">>>");
        if(receivedComponents.length > 3){
            received = new Message(data);
        } else {
            System.out.println("Received this shit : " + data + " received content length is " + receivedComponents.length);

            return Optional.empty();
        }

        if(delivered.contains(received)){
            // we received it already (avoiding duplication)
            if(toAck && received.getType() != MessageType.ACK) {
                // we need to ack it if he's waiting for an ACK otherwise he's gonna retransmit it
                rSend(packet.getAddress().getHostAddress(), packet.getPort(), new Message("_", MessageType.ACK, me, me));
            }
            return Optional.empty(); // act like we received nothing
        }

        // if we get here we've never seen this message


        if(toAck && received.getType() != MessageType.ACK) {
            rSend(packet.getAddress().getHostAddress(), packet.getPort(), new Message("_", MessageType.ACK, me, me));
            delivered.add(received); // we know we haven't received an ACK we can add it to the delivered message and continue
        }

        //System.out.println(me.getId() + " delivered for fairlosslink " + delivered);
        //System.out.println(me.getId() + " received " + received);
        // we empty the receivePacket
        Arrays.fill(receivePacket, (byte)0);
        // we return build message once we know every component of it
        return Optional.of(received);
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
