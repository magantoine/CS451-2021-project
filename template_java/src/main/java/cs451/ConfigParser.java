package cs451;

import java.io.*;
import java.util.Scanner;

public class ConfigParser {

    private String path;
    private int numberOfMsg;
    private int receiverPid;
    private String payload;


    public boolean populate(String value) {
        String args[] = null;
        String content = null;

        try {

            Scanner in = new Scanner(new FileReader(value));

            content = in.nextLine();

            args = content.split(" ");

        } catch (FileNotFoundException e){
            return true;
        }

        path = value;

        numberOfMsg = Integer.parseInt(args[0]);

        receiverPid = Integer.parseInt(args[1]);

        payload = "";

        return false;
    }

    public String getPath() {
        return path;
    }

    public int getNumberOfMessage(){
        return numberOfMsg;
    }

    public int getReceiverPid(){
        return receiverPid;
    }

    public String getPayload(){
        return payload;
    }

}
