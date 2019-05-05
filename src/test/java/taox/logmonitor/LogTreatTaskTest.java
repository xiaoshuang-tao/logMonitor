package taox.logmonitor;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource(locations="classpath:application.yml")
public class LogTreatTaskTest {
    @Autowired
    private LogTreatTask logTreatTask;

    @Test
    public void getConnectionsInLastHourTest() throws Exception{
        URL res = LogTreatTaskTest.class.getClassLoader().getResource(logTreatTask.getFilePath());
        File file = Paths.get(res.toURI()).toFile();
        String absolutePath = file.getAbsolutePath();
        logTreatTask.setFilePath(absolutePath);
        LocalDateTime time = LocalDateTime.parse("2019-05-05T17:30:00");
        Date now = Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
        List<ServerConnection> result = logTreatTask.getConnectionsInLastHour(now);
        Assert.assertTrue(result.size()==3);

    }

    @Test
    public void getConnectedServersByConnectingServerTest(){
        List<ServerConnection> connections = new ArrayList<>();
        connections.add(new ServerConnection(new Date(),"test","one"));
        connections.add(new ServerConnection(new Date(),"test","two"));
        connections.add(new ServerConnection(new Date(),"testi","three"));
        connections.add(new ServerConnection(new Date(),"testi","four"));
        List<String> result = logTreatTask.getConnectedServersByConnectingServer(connections);
        Assert.assertTrue(result.size()==2);
    }

    @Test
    public void getConnectingServersByConnectedServerTest(){
        List<ServerConnection> connections = new ArrayList<>();
        connections.add(new ServerConnection(new Date(),"test","one"));
        connections.add(new ServerConnection(new Date(),"test","two"));
        connections.add(new ServerConnection(new Date(),"testi","three"));
        connections.add(new ServerConnection(new Date(),"testi","four"));
        List<String> result = logTreatTask.getConnectingServersByConnectedServer(connections);
        Assert.assertTrue(result.isEmpty());
    }

    @Test
    public void getServerNameWithMostConnectionsTest(){
        List<ServerConnection> connections = new ArrayList<>();
        connections.add(new ServerConnection(new Date(),"test","one"));
        connections.add(new ServerConnection(new Date(),"test","two"));
        connections.add(new ServerConnection(new Date(),"testi","three"));
        connections.add(new ServerConnection(new Date(),"testi","four"));
        List<String> result = logTreatTask.getServerNameWithMostConnections(connections);
        Assert.assertTrue(result.size()==2);
    }


}