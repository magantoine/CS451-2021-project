package cs451;

import cs451.broadcast.FifoBroadcast;
import cs451.broadcast.UrbBroadcastManager;
import cs451.links.FairLossLink;
import cs451.links.ReliableLink;

import java.io.IOException;

public class Main {

    private static FifoBroadcast currentProcess = null;
    private static String outPath = null;

    private static void handleSignal() {
        //immediately stop network packet processing
        System.out.println("Immediately stopping network packet processing.");
        System.out.println("end " +java.time.LocalDateTime.now());
        //write/flush output file if necessary
        //System.out.println("Writing output.");

        // flushes the activity of the process when interrupted
        System.out.println("Dealing with interruption of process : " + currentProcess.getpId());
        System.out.println("Flushed activity for process " + currentProcess.getpId());
        currentProcess.flushActivity(outPath);
        System.out.println("Closing process " + currentProcess.getpId());
        //currentProcess.close();

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

        System.out.println("Start " +java.time.LocalDateTime.now());

        outPath = parser.output();
         //System.out.println("> INPUT PARSED");
         ActiveHost me = null;

         System.out.println(parser.hosts());
         for(ActiveHost host : parser.hosts()) {
             if (host.getId() == parser.myId()) {
                 me = host;
             }
         }

         // we got us
        initSignalHandlers();



        FairLossLink flLink = new FairLossLink(me.getPort(), new MessageSerializer(), me);

        //LogLink logLink = new LogLink(flLink, me.getId());

        ReliableLink rLink = new ReliableLink(flLink, me);

        // both registered to fl's received

        flLink.register(rLink);
        //rLink.register(logLink);


        // sender host created
        var p = new FifoBroadcast(me.getId(), rLink, parser.hosts(), parser.output(), me);

        currentProcess = p;


        p.fifoBroadcast(parser.numberOfMessage());

}


/**

    private static void runSender(ActiveHost selfHost, ActiveHost receiver, int m, String outputPath, List<ActiveHost> allHosts, String payload) throws IOException, InterruptedException {



        FairLossLink flLink = new FairLossLink("" + selfHost.getId(), selfHost.getPort(), new MessageSerializer());
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
        //System.out.println("========================================================================================");
        //System.out.println("Running as receiver with Pid : " + p.getpId() + " (out : " + outputPath + ")");
        //System.out.println("========================================================================================");
        currentProcess = p;
        // run the receiver behavior
        p.runAsReceiver(outputPath);
        // closes the link
        p.close();


    }
 */
    
}


