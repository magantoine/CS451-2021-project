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
    private DatagramSocket socket;
    private byte[] sendPacket = new byte[Constants.MAX_PACKET_SIZE];
    private final byte [] recPacket = new byte[Constants.MAX_PACKET_SIZE];






    public FairLossLink(int portNumber, Serializer serializer) throws SocketException {
        // socketException due to the create of a datagram socket
        this.serializer = serializer;
        this.portNumber = portNumber;
        this.socket = new DatagramSocket(portNumber);

        Thread receiver = new Thread(this::deliver);
        receiver.setDaemon(true);
        receiver.start();
    }


    public void deliver() {
        while (true) {
            DatagramPacket packet = new DatagramPacket(recPacket, recPacket.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }
            var data = serializer.deserialize(packet.getData());
            Message received = null;
            String receivedComponents[] = data.split(">>>");
            if (receivedComponents.length > 3) {
                received = new Message(data);

                this.share(received);
            }

        }
    }


    public void rSend(String ipDest, int portDest, Message message) throws IOException {
        try{
            sendPacket = serializer.serialize(message);
            DatagramPacket packet = new DatagramPacket(sendPacket, sendPacket.length, InetAddress.getByName(ipDest), portDest);
            socket.send(packet);
        } catch (SocketException e){
            e.printStackTrace();
        }

    }

    public void close(){
        socket.close();
    }

}





























