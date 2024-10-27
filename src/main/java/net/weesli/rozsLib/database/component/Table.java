package net.weesli.rozsLib.database.component;

import lombok.Getter;
import lombok.Setter;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
@Getter@Setter
public class Table implements SQLComponent{

    private String name;
    private Column[] columns;
    private Connection connection;

    public Table(Connection connection, String name, Column[] columns) {
        this.name = name;
        this.columns = columns;
        this.connection = connection;
    }

    @Override
    public String getSQL() {
        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS ").append(name).append(" (");
        for(Column column : columns){
            sql.append(column.getName()).append(" ").append(column.getType()).append(", ");
        }
        sql.setLength(sql.length() - 2);
        sql.append(");");
        return sql.toString();
    }

    public void execute(){
        try (PreparedStatement statement = connection.prepareStatement(getSQL())) {
            statement.execute();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


}
