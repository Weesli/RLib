package net.weesli.rozslib.database;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Getter;
import lombok.Setter;
import net.weesli.rozslib.enums.DatabaseType;
@Getter@Setter
public class ConnectionInfo {
    
    private DatabaseType type;
    private String url;

    // if type is MySQL use root information
    private String username;
    private String password;
    private String dbName;

    public ConnectionInfo(DatabaseType type, String url, String username, String password, String dbName) {
        this.type = type;
        this.url = url;
        this.username = username;
        this.password = password;
        this.dbName = dbName;
    }

    public ConnectionInfo(DatabaseType type, String url){
        this.type = type;
        this.url = url;
        this.username = null;
        this.password = null;
        this.dbName = null;
    }

    public static ConnectionInfo fromJson(String json){
        JsonObject object = JsonParser.parseString(json).getAsJsonObject();
        return new ConnectionInfo(
                DatabaseType.valueOf(object.get("type").getAsString()),
                object.get("url").getAsString(),
                object.has("username")? object.get("username").getAsString() : null,
                object.has("password")? object.get("password").getAsString() : null,
                object.has("dbName")? object.get("dbName").getAsString() : null
        );
    }
}
