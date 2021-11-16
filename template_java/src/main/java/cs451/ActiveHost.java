package cs451;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActiveHost that = (ActiveHost) o;
        return id == that.id &&
                port == that.port &&
                Objects.equals(ip, that.ip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, ip, port);
    }
}
