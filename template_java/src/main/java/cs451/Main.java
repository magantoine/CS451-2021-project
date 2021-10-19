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

    /**
    public static void main(String[] args) throws InterruptedException, IOException {

         Parser parser = new Parser(args);
         parser.parse();

         initSignalHandlers();

         // example
         long pid = ProcessHandle.current().pid();
         System.out.println("My PID: " + pid + "\n");
         System.out.println("From a new terminal type `kill -SIGINT " + pid + "` or `kill -SIGTERM " + pid + "` to stop processing packets\n");

         System.out.println("My ID: " + parser.myId() + "\n");
         System.out.println("List of resolved hosts is:");
         System.out.println("==========================");
         for (Host host: parser.hosts()) {
         System.out.println(host.getId());
         System.out.println("Human-readable IP: " + host.getIp());
         System.out.println("Human-readable Port: " + host.getPort());
         System.out.println();
         }
         System.out.println();

         System.out.println("Path to output:");
         System.out.println("===============");
         System.out.println(parser.output() + "\n");

         System.out.println("Path to config:");
         System.out.println("===============");
         System.out.println(parser.config() + "\n");

         System.out.println("Doing some initialization\n");

         System.out.println("Broadcasting and delivering messages...\n");

         // After a process finishes broadcasting,
         // it waits forever for the delivery of messages.
         while (true) {
         // Sleep for 1 hour
         Thread.sleep(60 * 60 * 1000);
         }

    }

    **/

    private static void runSender(Host selfHost, Host receiver, int m) throws IOException, InterruptedException {



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


    private static void runReceiver(Host selfHost) throws IOException {

        // create the link
        FairLossLink flLink = new FairLossLink("" + selfHost.getId(), selfHost.getPort(), new SimpleSerialier());
        LogLink logLink = new LogLink(flLink);
        ReliableLink rLink = new ReliableLink(logLink);

        // create the process
        Process p = new Process(selfHost.getId(), rLink);

        // run the receiver behavior
        p.runAsReceiver();
        // closes the link
        rLink.close();


    }

    public static void main(String[] args) throws InterruptedException, UnknownHostException {
        int basePort = 10010;
        int m = 10;

        // this is the host that will be receiving
        Host receiver = new Host(10, InetAddress.getLocalHost().getHostAddress(), basePort++);
        System.out.println("RECEIVER HOST CREATED");
        // we launch the receiver process
        Thread recProcess = new Thread(() -> {
            try {
                runReceiver(receiver);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        recProcess.start();

        System.out.println("RECEIVER THREAD STARTED");


        List<Host> senderHosts = new ArrayList<>();
        List<Thread> processes = new ArrayList<>();
        for(int i = 0; i < 3; i++){
            Host h = new Host(i, InetAddress.getLocalHost().getHostAddress(), basePort++);
            System.out.println("SENDER HOST " + i + " CREATED");
            processes.add(new Thread(() -> {
                try {
                    runSender(h, receiver, m);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }));
            System.out.println("SENDER PROCESS " + i + " CREATED");
        }

        processes.forEach(t -> t.setDaemon(true));
        processes.forEach(Thread::start);
    }


}


