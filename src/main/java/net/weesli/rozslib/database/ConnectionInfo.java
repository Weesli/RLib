package net.weesli.rozslib.database;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.Setter;
import net.weesli.rozslib.enums.DatabaseType;
@Getter@Setter
public class ConnectionInfo {
    
    private DatabaseType type;
    private String hostname;

    // if type is MySQL use root information
    private Integer port;
    private String username;
    private String password;
    private String dbName;

    public ConnectionInfo(DatabaseType type, String hostname, Integer port, String username, String password, String dbName) {
        this.type = type;
        this.hostname = hostname;
        this.username = username;
        this.password = password;
        this.dbName = dbName;
    }

    public ConnectionInfo(DatabaseType type, String dbName){
        this.type = type;
        this.username = null;
        this.password = null;
        this.dbName = dbName;
    }

}
