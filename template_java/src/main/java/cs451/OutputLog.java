package cs451;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

public class OutputLog {

    // not final but cannot be accessed anyway
    private FileWriter innerFile = null;
    private final ArrayList logStack = new ArrayList<String>();
    private boolean readOnly = false; // get true when closed


    public OutputLog(String logName) throws IOException {
        this.innerFile = new FileWriter(logName);
    }

    public void write(String log) throws IOException {
        if(!readOnly) {
            String finalLog = appendTimeStamp(log);
            logStack.add(finalLog);
            this.innerFile.write(finalLog);
            this.innerFile.flush();
        }

    }

    public void close() throws IOException {
        if(!readOnly) {
            this.innerFile.close();
            readOnly = true;
        }
    }

    public int getLogSize(){
        return logStack.size();
    }

    public String getLogAtIndex(int logIndex){
        return (String) logStack.get(logIndex);
    }

    public String[] getAllLogEntries(){
        return (String[]) logStack.toArray(new String[0]);
    }


    public static String appendTimeStamp(String message){
        return "{ " + java.time.LocalDateTime.now() + " } " + message;
    }

}
