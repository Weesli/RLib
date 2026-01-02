package net.weesli.rozslib.database;

import lombok.SneakyThrows;
import net.weesli.rozslib.enums.DatabaseType;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.nio.file.Path;
import java.sql.*;

public class DatabaseFactory {

    @SneakyThrows
    public static Connection createConnection(ConnectionInfo info, @Nullable Path dataFolder){
        if (info.getType().equals(DatabaseType.SQLite)){
            Path finalPath = dataFolder.resolve(info.getDbName() + ".db");
            if (!finalPath.toFile().exists()){
                finalPath.toFile().createNewFile();
            }
            return DriverManager.getConnection("jdbc:sqlite:" + finalPath);
        } else if (info.getType().equals(DatabaseType.MySQL)) {
            return DriverManager.getConnection(String.format("jdbc:mysql://%s:%d/%s?createDatabaseIfNotExist=true&useSSL=true",
                    info.getHostname(), info.getPort(), info.getDbName()), info.getUsername(), info.getPassword());
        }
        throw new IllegalArgumentException("Unsupported database type: " + info.getType());
    }
}
