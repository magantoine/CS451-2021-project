package cs451;

import cs451.Serializer;

import java.io.IOException;
import java.net.*;
import java.util.*;

public class FairLossLink extends Link {


    private final Serializer serializer;
    private int portNumber;
    private final DatagramSocket socket;
    private byte[] sendPacket = new byte[Constants.MAX_PACKET_SIZE];
    private final List<Message> delivered = new ArrayList<Message>();
    private final ActiveHost me;





    public FairLossLink(int portNumber, Serializer serializer, ActiveHost me) throws SocketException {
        // socketException due to the create of a datagram socket
        this.serializer = serializer;
        this.portNumber = portNumber;
        this.socket = new DatagramSocket(portNumber);
        this.me = me;

        Thread receiver = new Thread(this::deliver);
        receiver.setDaemon(true);
        receiver.start();
    }


    public void deliver() {
        while (true) {
            byte [] recPacket = new byte[Constants.MAX_PACKET_SIZE];
            DatagramPacket packet = new DatagramPacket(recPacket, recPacket.length);

            try {
                socket.setSoTimeout(1000);
                socket.receive(packet);
            } catch (IOException e) {
                //e.printStackTrace(); The only unecessary one
                System.out.println(me.getId() + ") TIMEOUT");
            }

            if (packet.getData() != null) {

                var data = serializer.deserialize(packet.getData());
                if(me.getId() == 1) {
                    //System.out.println("Received a new thing " + data);
                }
                Message received = null;
                String receivedComponents[] = data.split(">>>");
                if (receivedComponents.length > 3) {
                    received = new Message(data);
                    Message finalReceived = received;
                    observers.forEach(o -> o.receive(finalReceived));
                } else {
                    //System.out.println("Received this shit : " + data + " received content length is " + receivedComponents.length);
                }
            }

        }
    }


    public void rSend(String ipDest, int portDest, Message message) throws IOException {
        sendPacket = serializer.serialize(message);
        DatagramPacket packet = new DatagramPacket(sendPacket, sendPacket.length, InetAddress.getByName(ipDest), portDest);
        socket.send(packet);
    }

    public void close(){
        socket.close();
    }

}





























