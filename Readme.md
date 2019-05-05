#LogMonitor
LogMonitor is a simple java application for monitoring log file which contains server connections
The application will list following information each hour:
- Servers connected by a given server in the last hour
- Servers connected to a given server in the last hour
- Servers with most connections in the last hour

##Log file example
````
1366815793 quark garak
1366815795 brunt quark
1366815811 lilac garak
````
Each line correspond a connection which contains a timestamp, a connecting server and a connected server separated by space.
Lines are ordered by timestamp in ascending order.<br/>
There is an example file test.log in the project folder.

##Run application
Run application with command line
````
java -jar target\log-monitor-0.0.1-SNAPSHOT.jar --log.filePath=log_file_absolute_path --log.serverName=given_a_server_name
````

Ex:
````
java -jar target\log-monitor-0.0.1-SNAPSHOT.jar --log.filePath=C:/Users/taox/Desktop/test.log --log.serverName=test
````
![Alt text](./LogMonitorResultExample.png?raw=true "Result example")