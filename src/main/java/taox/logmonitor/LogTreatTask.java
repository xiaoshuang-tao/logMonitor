package taox.logmonitor;

import org.apache.commons.io.input.ReversedLinesFileReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class LogTreatTask {
    private static final Logger log = LoggerFactory.getLogger(LogTreatTask.class);
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm:ss");


    private static String filePath;

    private static String serverName;

    @Scheduled(cron="0 0 * * * *")
    public void treatLog(){
        Date now = new Date();
        log.info("The time is {} ",dateFormat.format(new Date()));
        log.info("Treat log of last one hour in log file:{}",filePath);


        //LocalDateTime time = LocalDateTime.parse("2019-05-05T17:30:00");
        //now = Date.from(time.atZone(ZoneId.systemDefault()).toInstant());

        List<ServerConnection> connections = getConnectionsInLastHour(now);
        List<String> connectedServers = getConnectedServersByConnectingServer(connections);
        log.info("-------------------------------------------------------");
        log.info("Servers connected by {} in the last hour:", serverName);
        if (connectedServers != null) {
            connectedServers.forEach(log::info);
        }else{
            log.info("no server found");
        }
        log.info("-------------------------------------------------------\n");

        List<String> connectingServers = getConnectingServersByConnectedServer(connections);
        log.info("-------------------------------------------------------");
        log.info("Servers connected to {} in the last hour:", serverName);
        if (connectingServers != null) {
            connectingServers.forEach(log::info);
        }else{
            log.info("no server found");
        }
        log.info("-------------------------------------------------------\n");

        List<String> serversWithMostConnections = getServerNameWithMostConnections(connections);
        log.info("-------------------------------------------------------");
        log.info("Servers with most connections:");
        if (serversWithMostConnections != null) {
            serversWithMostConnections.forEach(log::info);
        }else{
            log.info("no server found");
        }
        log.info("-------------------------------------------------------\n\n\n\n");
    }

    public List<ServerConnection> getConnectionsInLastHour(Date now) {
        List<ServerConnection> connections = new ArrayList<>();
        filePath = filePath.replaceAll("\\\\","/");
        try (ReversedLinesFileReader reader = new ReversedLinesFileReader(new File(filePath),4096,"UTF-8")) {
            String line;
            while ((line= reader.readLine())!= null) {
                if (!line.isEmpty()) {
                    line = line.replaceAll("[\uFEFF-\uFFFF]*", "");
                    String[] results = line.trim().split(" ");
                    if (results.length == 3) {
                        Date time = new Date(Long.parseLong(results[0]));
                        if( now.getTime()>time.getTime() && now.getTime() - time.getTime() < 60*60*1000 ) {
                            connections.add(new ServerConnection(time, results[1], results[2]));
                        }else{
                            break;
                        }
                    }
                }
            }
        }catch(FileNotFoundException e) {
            log.error("{} not found",filePath);
            e.printStackTrace();
        }catch (IOException e) {
            log.error("Error in reading log file:{}",filePath);
            e.printStackTrace();
        }catch(Exception e){
            log.error("Error in treating log:{}",filePath);
            e.printStackTrace();
        }
        return connections;
    }

    public String getFilePath() {
        return filePath;
    }

    protected List<String> getConnectedServersByConnectingServer(List<ServerConnection> connections){
        return isEmptyList(connections) ?
                connections.stream().
                        filter(c -> serverName.equals(c.getConnectingServer()))
                        .map(ServerConnection::getConnectedServer)
                        .distinct()
                        .collect(Collectors.toList()) : null;
    }

    protected List<String> getConnectingServersByConnectedServer(List<ServerConnection> connections){
        return isEmptyList(connections) ? connections.stream().filter(c -> serverName.equals(c.getConnectedServer())).map(ServerConnection::getConnectingServer).distinct().collect(Collectors.toList()) : null;
    }

    protected List<String> getServerNameWithMostConnections(List<ServerConnection> connections){
        if(isEmptyList(connections)){
            Map<String,Long> result = connections.stream().map(ServerConnection::getServers).flatMap(Collection::stream).collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            LinkedHashMap<String,Long> orderedMap=result.entrySet().stream().sorted(Map.Entry.comparingByValue()).collect(Collectors.toMap(Map.Entry::getKey,Map.Entry::getValue,(e1, e2)->e1,LinkedHashMap::new));
            Long number = Objects.requireNonNull(orderedMap.entrySet().stream().reduce((first, second) -> second).orElse(null)).getValue();
            return orderedMap.entrySet().stream().filter(e-> e.getValue().equals(number)).map(Map.Entry::getKey).collect(Collectors.toList());
        }
        return null;
    }

    private static boolean isEmptyList(List<ServerConnection> connections) {
        return connections != null && !connections.isEmpty();
    }


    @Value("${log.filePath}")
    public void setFilePath(String filePath) {
        LogTreatTask.filePath = filePath;
    }

    @Value("${log.serverName}")
    public void setServerName(String serverName) {
        LogTreatTask.serverName = serverName;
    }
}
