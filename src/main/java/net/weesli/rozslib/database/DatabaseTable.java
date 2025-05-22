package net.weesli.rozslib.database;

import net.weesli.rozslib.database.annotation.PrimaryKey;
import net.weesli.rozslib.database.serializer.ObjectSerializer;
import net.weesli.rozslib.database.serializer.SerializerPack;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Advanced SQL based object database system
 * Record and manage data with Classroom.
 * CustomObject, Map, Set, List, Enum supported
 * Improved with transaction handling, boolean support,
 * safer query, logging and generic type handling.
 * @author Weesli
 * @since 03.15.2025
 */
public class DatabaseTable {

    private static final Logger LOGGER = Logger.getLogger(DatabaseTable.class.getName());

    private final Connection connection;
    private final String tableName;
    private final List<SerializerPack> packs = new ArrayList<>();

    public interface DatabaseDialect {
        String getSqlType(Class<?> javaType);
        String getDefaultValue(Class<?> javaType);
        boolean supportsBoolean();
    }

    private final DatabaseDialect dialect;

    public DatabaseTable(String tableName, Connection connection) {
        this(tableName, connection, new SQLiteDialect());
    }

    public DatabaseTable(String tableName, Connection connection, DatabaseDialect dialect) {
        this.connection = connection;
        this.tableName = tableName;
        this.dialect = dialect;
        try {
            connection.setAutoCommit(false);
            connection.setNetworkTimeout(Executors.newSingleThreadExecutor(), 10000);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize connection settings", e);
        }
    }

    public DatabaseTable addPack(SerializerPack pack) {
        packs.add(pack);
        return this;
    }

    private void runInTransaction(SQLRunnable runnable) {
        try {
            runnable.run();
            connection.commit();
        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                LOGGER.log(Level.SEVERE, "Rollback failed", ex);
            }
            throw new RuntimeException("Transaction failed", e);
        }
    }

    @FunctionalInterface
    private interface SQLRunnable {
        void run() throws Exception;
    }

    public void createTable(Object obj) {
        if (tableExists()) {
            addMissingColumns(obj);
        } else {
            createTableIfNotExists(obj.getClass());
        }
    }

    private boolean tableExists() {
        try (ResultSet tables = connection.getMetaData().getTables(null, null, tableName, null)) {
            return tables.next();
        } catch (SQLException e) {
            throw new RuntimeException("Error checking table existence", e);
        }
    }

    private void createTableIfNotExists(Class<?> clazz) {
        String sql = "CREATE TABLE IF NOT EXISTS " + tableName + " (" +
                getAllFields(clazz).stream().map(field -> getColumnDefinition(field) + ", ").collect(Collectors.joining()) +
                ");";
        executeUpdate(sql);
    }

    private void addMissingColumns(Object obj) {
        Set<String> existingColumns = getExistingColumns();
        getAllFields(obj.getClass()).stream()
                .filter(field -> !existingColumns.contains(field.getName()))
                .forEach(field -> executeUpdate(getAlterColumnDefinition(field)));
    }


    private Set<String> getExistingColumns() {
        try (ResultSet columns = connection.getMetaData().getColumns(null, null, tableName, null)) {
            Set<String> columnNames = new HashSet<>();
            while (columns.next()) {
                columnNames.add(columns.getString("COLUMN_NAME"));
            }
            return columnNames;
        } catch (SQLException e) {
            throw new RuntimeException("Error fetching existing columns", e);
        }
    }

    private String getColumnDefinition(Field field) {
        StringBuilder definition = new StringBuilder(field.getName() + " " + dialect.getSqlType(field.getType()));
        if (field.isAnnotationPresent(PrimaryKey.class)) {
            definition.append(" PRIMARY KEY");
        }
        return definition.toString();
    }

    private String getAlterColumnDefinition(Field field) {
        return "ALTER TABLE " + tableName + " ADD COLUMN " + getColumnDefinition(field) + " DEFAULT " + dialect.getDefaultValue(field.getType());
    }

    public void insert(Object obj) {
        String sql = "INSERT INTO " + tableName + " (" + getColumns(obj) + ") VALUES (" + getPlaceholders(obj) + ")";
        runInTransaction(() -> executePreparedUpdate(sql, obj));
    }

    public void update(Object obj) {
        String sql = "UPDATE " + tableName + " SET " + getSetClause(obj) + " WHERE " + getPrimaryKeyWhereClause(obj);
        runInTransaction(() -> executePreparedUpdate(sql, obj));
    }

    public void delete(Object obj) {
        String deleteSQL = "DELETE FROM " + tableName + " WHERE " + getPrimaryKeyWhereClause(obj);
        try {
            Object value = getPrimaryKeyValue(obj);
            runInTransaction(() -> {
                try (PreparedStatement statement = connection.prepareStatement(deleteSQL)) {
                    statement.setObject(1, value);
                    statement.executeUpdate();
                }
            });
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Primary key access failed", e);
        }
    }

    private String getColumns(Object obj) {
        return getAllFields(obj).stream().map(Field::getName).collect(Collectors.joining(", "));
    }

    private String getPlaceholders(Object obj) {
        return getAllFields(obj).stream().map(f -> "?").collect(Collectors.joining(", "));
    }

    private String getSetClause(Object obj) {
        String primaryKey = getPrimaryKeyField(getAllFields(obj).toArray(new Field[0]));
        return getAllFields(obj).stream()
                .filter(field -> !field.getName().equals(primaryKey))
                .map(field -> field.getName() + " = ?")
                .collect(Collectors.joining(", "));
    }

    private String getPrimaryKeyWhereClause(Object obj) {
        String primaryKey = getPrimaryKeyField(getAllFields(obj).toArray(new Field[0]));
        if (primaryKey == null) {
            throw new RuntimeException("No primary key defined on class " + obj.getClass().getName());
        }
        return primaryKey + " = ?";
    }

    private String getPrimaryKeyField(Field[] fields) {
        return Arrays.stream(fields)
                .filter(field -> field.isAnnotationPresent(PrimaryKey.class))
                .map(Field::getName)
                .findFirst()
                .orElse(null);
    }

    private Object getPrimaryKeyValue(Object obj) throws IllegalAccessException {
        Field[] fields = obj.getClass().getDeclaredFields();
        String primaryKeyField = getPrimaryKeyField(fields);
        if (primaryKeyField == null) {
            throw new RuntimeException("Primary key field not found");
        }
        for (Field field : fields) {
            if (field.getName().equals(primaryKeyField)) {
                field.setAccessible(true);
                return field.get(obj);
            }
        }
        return null;
    }

    private void executePreparedUpdate(String sql, Object obj) {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            setStatementParameters(statement, obj);
            statement.executeUpdate();
        } catch (SQLException | IllegalAccessException e) {
            throw new RuntimeException("Error executing prepared statement: " + sql, e);
        }
    }

    private void setStatementParameters(PreparedStatement statement, Object obj) throws SQLException, IllegalAccessException {
        List<Field> fields = getAllFields(obj);
        String primaryKey = getPrimaryKeyField(fields.toArray(new Field[0]));

        int paramIndex = 1;
        for (Field field : fields) {
            field.setAccessible(true);
            if (primaryKey != null && statement.toString().contains("WHERE " + primaryKey + " = ?") && field.getName().equals(primaryKey)) {
                continue;
            }
            Object value = serializeFieldValue(field.get(obj), field);
            statement.setObject(paramIndex++, value);
        }
        if (primaryKey !=null && statement.toString().contains("WHERE " + primaryKey + " = ?")) {
            Field pkField = fields.stream().filter(f -> f.getName().equals(primaryKey)).findFirst().orElseThrow();
            pkField.setAccessible(true);
            Object pkValue = serializeFieldValue(pkField.get(obj), pkField);
            statement.setObject(paramIndex, pkValue);
        }
    }
    private Object serializeFieldValue(Object value, Field field) {
        if (value == null) return null;
        Class<?> type = field.getType();
        if (type == boolean.class || type == Boolean.class) {
            // boolean -> INTEGER 0/1
            return (Boolean) value ? 1 : 0;
        }
        for (SerializerPack pack : packs) {
            for (ObjectSerializer serializer : pack.getSerializers()) {
                if (serializer.canSerialize(type)) {
                    return serializer.serialize(value);
                }
            }
        }
        if (type.isEnum()) {
            return ((Enum<?>) value).name();
        }
        return value;
    }

    public <T> T selectByPrimaryKey(Class<T> clazz, Object primaryKeyValue) {
        String primaryKey = getPrimaryKeyField(getAllFields(clazz).toArray(new Field[0]));
        if (primaryKey == null) {
            throw new RuntimeException("Primary key not defined on class " + clazz.getName());
        }
        String sql = "SELECT * FROM " + tableName + " WHERE " + primaryKey + " = ?";
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.setObject(1, primaryKeyValue);
            try (ResultSet rs = statement.executeQuery()) {
                if (rs.next()) {
                    return deserializeObject(clazz, rs);
                }
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Select by primary key failed", e);
        }
    }

    public <T> List<T> selectWhere(Class<T> clazz, String whereClause, Object... params) {
        String sql = "SELECT * FROM " + tableName + (whereClause == null || whereClause.isEmpty() ? "" : " WHERE " + whereClause);
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            if (params != null) {
                for (int i = 0; i < params.length; i++) {
                    statement.setObject(i + 1, params[i]);
                }
            }
            try (ResultSet rs = statement.executeQuery()) {
                List<T> result = new ArrayList<>();
                while (rs.next()) {
                    result.add(deserializeObject(clazz, rs));
                }
                return result;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Select where failed", e);
        }
    }

    private <T> T deserializeObject(Class<T> clazz, ResultSet rs) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            for (Field field : getAllFields(clazz)) {
                field.setAccessible(true);
                Object dbValue = rs.getObject(field.getName());
                Object value = deserializeFieldValue(field, dbValue);
                field.set(instance, value);
            }
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Deserialization failed", e);
        }
    }

    private Object deserializeFieldValue(Field field, Object dbValue) {
        if (dbValue == null) return null;
        Class<?> type = field.getType();

        if (type == boolean.class || type == Boolean.class) {
            if (dbValue instanceof Number) {
                return ((Number) dbValue).intValue() != 0;
            }
            return Boolean.parseBoolean(dbValue.toString());
        }

        for (SerializerPack pack : packs) {
            for (ObjectSerializer serializer : pack.getSerializers()) {
                if (serializer.canSerialize(type)) {
                    return serializer.deserialize(dbValue.toString());
                }
            }
        }

        if (type.isEnum()) {
            String enumValue = dbValue.toString();
            for (Object constant : type.getEnumConstants()) {
                if (((Enum<?>) constant).name().equalsIgnoreCase(enumValue)) {
                    return constant;
                }
            }
            throw new IllegalArgumentException("Unknown enum value '" + enumValue + "' for enum " + type.getName());
        }

        return dbValue;
    }

    private List<Field> getAllFields(Object obj) {
        return getAllFields(obj.getClass());
    }

    private List<Field> getAllFields(Class<?> clazz) {
        List<Field> fields = new ArrayList<>();
        while (clazz != null && clazz != Object.class) {
            fields.addAll(Arrays.asList(clazz.getDeclaredFields()));
            clazz = clazz.getSuperclass();
        }
        return fields;
    }

    private static class SQLiteDialect implements DatabaseDialect {

        @Override
        public String getSqlType(Class<?> javaType) {
            if (javaType == int.class || javaType == Integer.class ||
                    javaType == boolean.class || javaType == Boolean.class) {
                return "INTEGER";
            }
            if (javaType == long.class || javaType == Long.class) {
                return "BIGINT";
            }
            if (javaType == float.class || javaType == Float.class ||
                    javaType == double.class || javaType == Double.class) {
                return "REAL";
            }
            if (javaType == String.class || javaType.isEnum()) {
                return "TEXT";
            }
            return "BLOB";
        }

        @Override
        public String getDefaultValue(Class<?> javaType) {
            if (javaType == boolean.class || javaType == Boolean.class) {
                return "0";
            }
            if (javaType == int.class || javaType == Integer.class ||
                    javaType == long.class || javaType == Long.class ||
                    javaType == float.class || javaType == Float.class ||
                    javaType == double.class || javaType == Double.class) {
                return "0";
            }
            return "''";
        }

        @Override
        public boolean supportsBoolean() {
            return false;
        }
    }

    private void executeUpdate(String sql) {
        try (Statement statement = connection.createStatement()) {
            statement.executeUpdate(sql);
        } catch (SQLException e) {
            throw new RuntimeException("SQL execution failed: " + sql, e);
        }
    }
}
