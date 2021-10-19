package cs451;

import java.io.IOException;
import java.lang.reflect.Array;
import java.net.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
        byte[] serialized = serializer.serialize(message + ">>>" + this.portNumber);


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

    public String waitForMessage(int timeout, boolean toAck) throws IOException {

        byte[] buffer = new byte[Constants.MAX_PACKET_SIZE];


        socket.setSoTimeout(timeout);
        DatagramPacket packet = null;



        try{
            packet = new DatagramPacket(buffer, buffer.length);
            socket.receive(packet);

        } catch(SocketTimeoutException e) {

            return null;
        }

        if(packet.getData() == null){
            return null;
        }
        String messageContent = rDeliver(packet.getData()).split(">>>")[0];
        int senderPort = Integer.parseInt(rDeliver(packet.getData()).split(">>>")[1]);


        if(toAck && !messageContent.contains("ACK")) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            rSend(packet.getAddress().getHostAddress(), senderPort, "ACK");
        }
        return messageContent;
    }

    public String waitForMessage() throws IOException {
        return this.waitForMessage(10000, false);
    }

    public String waitForMessage(int timeout) throws IOException {
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
