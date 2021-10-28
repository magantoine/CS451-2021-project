package cs451;

public class ActiveHost {

    private final int id;
    private final String ip;
    private final int port;

    public ActiveHost(int id, String ip, int port){
        this.id = id;
        this.port = port;
        this.ip = ip;
    }

    public int getId() {
        return id;
    }

    public String getIp() {
        return ip;
    }

    public int getPort() {
        return port;
    }

    @Override
    public String toString(){
        return "id: " + id + "/ ip: " + ip + "/port : " + port;
     }
}
