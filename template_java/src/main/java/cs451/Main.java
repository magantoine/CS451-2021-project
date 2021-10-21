package cs451;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private static void handleSignal() {
        //immediately stop network packet processing
        System.out.println("Immediately stopping network packet processing.");

        //write/flush output file if necessary
        System.out.println("Writing output.");
    }

    private static void initSignalHandlers() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                handleSignal();
            }
        });
    }

    public static void main(String[] args) throws InterruptedException, IOException {
         Parser parser = new Parser(args);
         parser.parse();

         //initSignalHandlers();


         /*
         Here the parser parsed everything in the command line :

         - own process id can be retrieved with : parser.myId()
         - remote hosts can be retrieved with : parser.hosts()
         - for each Hosts you can have host.getId(), host.getPort(), (all IP are the same)
         - path to output file (to log) : parser.output()
         - path to config : parser.config()
          */

         System.out.println("My Id : " + parser.myId());
         System.out.println("All hosts : " + parser.hosts());
         System.out.println("Output file : " + parser.output());
         System.out.println("Number of messages : " + parser.numberOfMessage());
         System.out.println("reciever pid : " + parser.receiverPid());



         System.out.println("> INPUT PARSED");


         ActiveHost me = null;
         ActiveHost receiver = null;
         System.out.println(parser.hosts());
         for(ActiveHost host : parser.hosts()) {
             if (host.getId() == parser.myId()) {
                 me = host;
             }
             if (host.getId() == parser.receiverPid()){
                 receiver = host;
             }
         }


        if(parser.myId() == parser.receiverPid()){
            System.out.println("> Receiver process launched for Host : " + me.toString());
            runReceiver(me);
        } else {
            System.out.println("> Sender process launched for Host : " + me.toString());
            runSender(me, receiver, parser.numberOfMessage());
        }






}




    private static void runSender(ActiveHost selfHost, ActiveHost receiver, int m) throws IOException, InterruptedException {



        FairLossLink flLink = new FairLossLink("" + selfHost.getId(), selfHost.getPort(), new SimpleSerialier());
        // we don't need the log for the senders
        LogLink logLink = new LogLink(flLink);
        ReliableLink rLink = new ReliableLink(flLink);

        // sender host created
        Process p = new Process(selfHost.getId(), rLink);

        // launch the sender behavior
        p.runAsSender(m, receiver);

        // closes the link
        rLink.close();
    }


    private static void runReceiver(ActiveHost selfHost, List<ActiveHost> allHost) throws IOException {

        // create the link
        FairLossLink flLink = new FairLossLink("" + selfHost.getId(), selfHost.getPort(), new SimpleSerialier());
        LogLink logLink = new LogLink(flLink);
        ReliableLink rLink = new ReliableLink(flLink);

        // create the process
        Process p = new Process(selfHost.getId(), rLink, allHost);

        // run the receiver behavior
        p.runAsReceiver();
        // closes the link
        rLink.close();


    }

    
}


