package cs451.links;

import cs451.ActiveHost;
import cs451.Constants;
import cs451.Message;
import cs451.Serializer;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class FairLossLink extends Link {


    private final Serializer serializer;
    private int portNumber;
    private final DatagramSocket socket;
    private byte[] sendPacket = new byte[Constants.MAX_PACKET_SIZE];
    private final ActiveHost me;
    private final byte [] recPacket = new byte[Constants.MAX_PACKET_SIZE];


    public FairLossLink(int portNumber, Serializer serializer, ActiveHost me) throws SocketException {
        // socketException due to the create of a datagram socket
        this.serializer = serializer;
        this.portNumber = portNumber;
        this.socket = new DatagramSocket(portNumber);
        this.me = me;

        Thread receiver = new Thread(this::deliver);
        //receiver.setDaemon(true);
        receiver.start();
    }


    public void deliver() {
        DatagramPacket packet = new DatagramPacket(recPacket, recPacket.length);
        while (true) {
            try {
                socket.receive(packet);
            } catch (IOException e) {
                //e.printStackTrace(); The only unecessary one
                System.out.println(me.getId() + ") Error receiving");
            }

            if (packet.getData() != null) {

                var data = serializer.deserialize(packet.getData());
                Message received = null;
                String receivedComponents[] = data.split(">>>");
                if (receivedComponents.length > 3) {
                    received = new Message(data);
                    this.share(received);
                } else {
                    //System.out.println("Received this shit : " + data + " received content length is " + receivedComponents.length);
                }
            }
            Arrays.fill(recPacket, (byte)0);
        }
    }


    public void rSend(String ipDest, int portDest, Message message) throws IOException {
        sendPacket = serializer.serialize(message);
        //System.arraycopy(message.toString().getBytes(), 0, sendPacket, 0, Constants.MAX_PACKET_SIZE);
        DatagramPacket packet = new DatagramPacket(sendPacket, sendPacket.length, InetAddress.getByName(ipDest), portDest);
        socket.send(packet);
    }

    public void close(){
        socket.close();
    }

}





























