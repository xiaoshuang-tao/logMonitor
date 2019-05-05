package taox.logmonitor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ServerConnection {
    private Date time;
    private String connectingServer;
    private String connectedServer;
    public ServerConnection(Date time, String connectingServer, String connectedServer) {
        this.time = time;
        this.connectingServer = connectingServer;
        this.connectedServer = connectedServer;
    }

    public String getConnectingServer() {
        return connectingServer;
    }

    public String getConnectedServer() {
        return connectedServer;
    }

    public List<String> getServers(){
        List<String> servers = new ArrayList<String>();
        servers.add(connectingServer);
        servers.add(connectedServer);
        return servers;
    }
}
