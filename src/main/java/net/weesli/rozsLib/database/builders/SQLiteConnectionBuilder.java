package net.weesli.rozsLib.database.builders;

import lombok.SneakyThrows;

import java.io.File;
import java.sql.DriverManager;

public class SQLiteConnectionBuilder extends Connection {

    private File file;

    public SQLiteConnectionBuilder(File file) {
        this.file = file;
    }

    @SneakyThrows
    @Override
    public java.sql.Connection getConnection() {
        return DriverManager.getConnection("jdbc:sqlite:" + file.getAbsolutePath());
    }
}
