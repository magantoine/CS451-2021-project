package cs451;

import java.io.IOException;
import java.util.List;

public class Main {

    private static Process currentProcess = null;
    private static String outPath = null;

    private static void handleSignal() {
        //immediately stop network packet processing
        System.out.println("Immediately stopping network packet processing.");

        //write/flush output file if necessary
        System.out.println("Writing output.");

        // flushes the activity of the process when interrupted
        System.out.println("Dealing with interruption of process : " + currentProcess.getpId());
        currentProcess.flushActivity(outPath);
        currentProcess.close();

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




         /*
         Here the parser parsed everything in the command line :

         - own process id can be retrieved with : parser.myId()
         - remote hosts can be retrieved with : parser.hosts()
         - for each Hosts you can have host.getId(), host.getPort(), (all IP are the same)
         - path to output file (to log) : parser.output()
         - path to config : parser.config()
          */


        outPath = parser.output();
         System.out.println("> INPUT PARSED");


         ActiveHost me = null;
         ActiveHost receiver = null;

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
            runReceiver(me, parser.hosts(), parser.output());
        } else {
            System.out.println("> Sender process launched for Host : " + me.toString());
            runSender(me, receiver, parser.numberOfMessage(), parser.output(), parser.hosts(), parser.payload());
        }



        initSignalHandlers();






}




    private static void runSender(ActiveHost selfHost, ActiveHost receiver, int m, String outputPath, List<ActiveHost> allHosts, String payload) throws IOException, InterruptedException {



        FairLossLink flLink = new FairLossLink("" + selfHost.getId(), selfHost.getPort(), new SimpleSerializer());
        // we don't need the log for the senders
        //LogLink logLink = new LogLink(flLink);
        ReliableLink rLink = new ReliableLink(flLink);

        // sender host created
        Process p = new Process(selfHost.getId(), rLink, allHosts, outputPath);

        currentProcess = p;

        // launch the sender behavior
        p.runAsSender(m, receiver, outputPath, payload);

        // closes the link
        p.close();
    }


    private static void runReceiver(ActiveHost selfHost, List<ActiveHost> allHost, String outputPath) throws IOException {

        // create the link
        FairLossLink flLink = new FairLossLink("" + selfHost.getId(), selfHost.getPort(), new SimpleSerializer());
        //LogLink logLink = new LogLink(flLink);
        ReliableLink rLink = new ReliableLink(flLink);

        // create the process
        Process p = new Process(selfHost.getId(), rLink, allHost, outputPath);
        System.out.println("========================================================================================");
        System.out.println("Running as receiver with Pid : " + p.getpId() + " (out : " + outputPath + ")");
        System.out.println("========================================================================================");
        currentProcess = p;
        // run the receiver behavior
        p.runAsReceiver(outputPath);
        // closes the link
        p.close();


    }

    
}


