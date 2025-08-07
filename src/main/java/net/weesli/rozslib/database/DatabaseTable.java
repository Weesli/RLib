package net.weesli.rozslib.database;

import lombok.SneakyThrows;
import net.weesli.rozslib.database.annotation.PrimaryKey;
import net.weesli.rozslib.database.serializer.ObjectSerializer;
import net.weesli.rozslib.database.serializer.SerializerPack;

import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * Advanced SQL based object database system
 * Record and manage data with Classroom.
 * CustomObject, Map, Set, List, Enum supported
 * @author Weesli
 * @since 03.15.2025
 */
public class DatabaseTable {
    private final Connection connection;
    private final String tableName;
    private final List<SerializerPack> packs = new ArrayList<>();

    @SneakyThrows
    public DatabaseTable(String tableName, Connection connection) {
        this.connection = connection;
        this.tableName = tableName;
        connection.setAutoCommit(false);connection.setNetworkTimeout(null, 10000);
    }

    public DatabaseTable addPack(SerializerPack pack) {
        packs.add(pack);
        return this;
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
                Arrays.stream(clazz.getDeclaredFields())
                        .map(this::getColumnDefinition)
                        .collect(Collectors.joining(", ")) +
                ");";
        executeUpdate(sql);
    }

    private void addMissingColumns(Object obj) {
        Set<String> existingColumns = getExistingColumns();
        Arrays.stream(obj.getClass().getDeclaredFields())
                .filter(field -> !existingColumns.contains(field.getName()))
                .map(this::getAlterColumnDefinition)
                .forEach(this::executeUpdate);
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
        StringBuilder definition = new StringBuilder(field.getName() + " " + getSqlType(field.getType()));
        if (field.isAnnotationPresent(PrimaryKey.class)) {
            definition.append(" PRIMARY KEY");
        }
        return definition.toString();
    }

    private String getAlterColumnDefinition(Field field) {
        return "ALTER TABLE " + tableName + " ADD COLUMN " + getColumnDefinition(field) + " DEFAULT " + getDefaultValueForType(field);
    }

    private String getDefaultValueForType(Field field) {
        if (field.getType().equals(int.class)) return "0";
        if (field.getType().equals(String.class)) return "''";
        if (Collection.class.isAssignableFrom(field.getType())) return "'[]'";
        return "NULL";
    }

    private String getSqlType(Class<?> type) {
        if (type == int.class || type == Integer.class || type == double.class || type == Double.class || type == long.class || type == Long.class) {
            return "INTEGER";
        } else if (type == float.class || type == Float.class) {
            return "REAL";
        } else {
            return "TEXT";
        }
    }

    public void insert(Object obj) {
        executePreparedUpdate("INSERT INTO " + tableName + " (" + getColumns(obj) + ") VALUES (" + getPlaceholders(obj) + ")", obj);
    }

    public void update(Object obj) {
        executePreparedUpdate("UPDATE " + tableName + " SET " + getSetClause(obj) + " WHERE " + getPrimaryKeyWhereClause(obj), obj);
    }

    public void delete(Object obj) throws IllegalAccessException {
        String deleteSQL = "DELETE FROM " + tableName + " WHERE " + getPrimaryKeyWhereClause(obj);
        Object value = getPrimaryKeyValue(obj);
        try(PreparedStatement statement = connection.prepareStatement(deleteSQL)) {
            statement.setObject(1, value);
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private String getColumns(Object obj) {
        return Arrays.stream(obj.getClass().getDeclaredFields()).map(Field::getName).collect(Collectors.joining(", "));
    }

    private String getPlaceholders(Object obj) {
        return Arrays.stream(obj.getClass().getDeclaredFields()).map(field -> "?").collect(Collectors.joining(", "));
    }

    private String getSetClause(Object obj) {
        String primaryKey = getPrimaryKeyField(obj.getClass().getDeclaredFields());
        return Arrays.stream(obj.getClass().getDeclaredFields())
                .filter(field -> !field.getName().equals(primaryKey))
                .map(field -> field.getName() + " = ?")
                .collect(Collectors.joining(", "));
    }

    private String getPrimaryKeyWhereClause(Object obj) {
        String primaryKey = getPrimaryKeyField(obj.getClass().getDeclaredFields());
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
            connection.commit();
        } catch (SQLException | IllegalAccessException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException("Error executing prepared statement: " + sql, e);
        }
    }

    private void setStatementParameters(PreparedStatement statement, Object obj) throws SQLException, IllegalAccessException {
        List<Field> fields = getAllFields(obj);
        String primaryKey = getPrimaryKeyField(fields.toArray(new Field[0]));
        int parameterIndex = 1;
        for (Field field : fields) {
            field.setAccessible(true);
            Object value = serializeFieldValue(field.get(obj), field);
            if (primaryKey != null && sqlContainsPrimaryKey(statement.toString(), field.getName())) {
                statement.setObject(fields.size(), value);
            } else {
                statement.setObject(parameterIndex++, value);
            }
        }
    }

    private boolean sqlContainsPrimaryKey(String sql, String primaryKey) {
        return sql.contains("WHERE " + primaryKey + " = ?");
    }

    private Object serializeFieldValue(Object value, Field field) {
        if (value == null) return null;
        if (field.getType().isEnum()) return ((Enum<?>) value).name();
        if (Collection.class.isAssignableFrom(field.getType())) return serializeCollection((Collection<?>) value);
        if (Map.class.isAssignableFrom(field.getType())) return serializeMap((Map<?, ?>) value);
        for (SerializerPack pack : packs) {
            for (ObjectSerializer serializer : pack.getSerializers()) {
                if (serializer.canSerialize(field.getType())) return serializer.serialize(value);
            }
        }
        return value;
    }


    private String serializeCollection(Collection<?> collection) {
        return "[" + collection.stream().map(this::serializeItem).collect(Collectors.joining(", ")) + "]";
    }

    private String serializeItem(Object item) {
        if (item == null) return "null";
        for (SerializerPack pack : packs) {
            for (ObjectSerializer serializer : pack.getSerializers()) {
                if (serializer.canSerialize(item.getClass())) return serializer.serialize(item);
            }
        }
        return item.toString();
    }

    private String serializeMap(Map<?, ?> map) {
        return "{" + map.entrySet().stream().map(entry -> entry.getKey() + "=" + entry.getValue()).collect(Collectors.joining(", ")) + "}";
    }

    private List<Field> getAllFields(Object obj) {
        List<Field> fields = new ArrayList<>();
        Class<?> currentClass = obj.getClass();
        while (currentClass != null) {
            fields.addAll(Arrays.asList(currentClass.getDeclaredFields()));
            currentClass = currentClass.getSuperclass();
        }
        return fields;
    }

    private void executeUpdate(String sql) {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            statement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            try {
                connection.rollback();
            } catch (SQLException ex) {
                throw new RuntimeException(ex);
            }
            throw new RuntimeException("Error executing update: " + sql, e);
        }
    }

    public List<Object> selectAll(Class<?> clazz) {
        return select("SELECT * FROM " + tableName, clazz);
    }

    public List<Object> selectWhere(Class<?> clazz, String where, Object... params) {
        String sql = "SELECT * FROM " + tableName;
        if (where != null && !where.isEmpty()) {
            sql += " WHERE " + where + " = ?";
        }
        return select(sql, clazz, params);
    }

    private List<Object> select(String sql, Class<?> clazz, Object... params) {
        try (PreparedStatement statement = connection.prepareStatement(sql)) {
            for (int i = 0; i < params.length; i++) {
                statement.setObject(i + 1, params[i]);
            }
            try (ResultSet resultSet = statement.executeQuery()) {
                List<Object> result = new ArrayList<>();
                while (resultSet.next()) {
                    result.add(deserialize(resultSet, clazz));
                }
                return result;
            }
        } catch (SQLException | ReflectiveOperationException e) {
            throw new RuntimeException("Error executing select: " + sql, e);
        }
    }


    private Object deserialize(ResultSet resultSet, Class<?> clazz) throws SQLException, ReflectiveOperationException {
        Object obj = clazz.getDeclaredConstructor().newInstance();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            Object value = resultSet.getObject(field.getName());
            field.set(obj, deserializeFieldValue(value, field));
        }
        return obj;
    }

    private Object deserializeFieldValue(Object value, Field field) {
        if (value == null) return null;

        Class<?> fieldType = field.getType();

        if (fieldType == boolean.class || fieldType == Boolean.class) {
            if (value instanceof Boolean) return value;
            if (value instanceof Number) return ((Number) value).intValue() != 0;
            return Boolean.parseBoolean(value.toString());
        }

        if (fieldType.isEnum()) return Enum.valueOf((Class<Enum>) fieldType, value.toString());
        if (Collection.class.isAssignableFrom(fieldType)) return deserializeCollection(value.toString(), field);
        if (Map.class.isAssignableFrom(fieldType)) return deserializeMap(value.toString());

        for (SerializerPack pack : packs) {
            for (ObjectSerializer serializer : pack.getSerializers()) {
                if (serializer.canSerialize(fieldType)) return serializer.deserialize(value.toString());
            }
        }

        return value;
    }


    private Collection<?> deserializeCollection(String value, Field field) {
        if (value == null || value.isEmpty() || "[]".equals(value)) return Collections.emptyList();
        String[] items = value.substring(1, value.length() - 1).split(", ");
        Type elementType = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
        if (List.class.isAssignableFrom(field.getType())) {
            return Arrays.stream(items).map(item -> deserializeItem(item, (Class<?>) elementType)).collect(Collectors.toList());
        } else if (Set.class.isAssignableFrom(field.getType())) {
            return Arrays.stream(items).map(item -> deserializeItem(item, (Class<?>) elementType)).collect(Collectors.toSet());
        }
        return Collections.emptyList();
    }

    private Object deserializeItem(String item, Class<?> elementType) {
        if (elementType.isEnum() || Enum.class.isAssignableFrom(elementType)) {
            try {
                Method valueOf = elementType.getMethod("valueOf", String.class);
                return valueOf.invoke(null, item);
            } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e) {
                System.err.println("Enum.valueOf çağrısı başarısız: " + e.getMessage());
                return null;    
            }
        }
        for (SerializerPack pack : packs) {
            for (ObjectSerializer serializer : pack.getSerializers()) {
                if (serializer.canSerialize(elementType)) return serializer.deserialize(item);
            }
        }
        return item;
    }

    private Map<?, ?> deserializeMap(String value) {
        if (value == null || value.isEmpty() || "{}".equals(value)) return Collections.emptyMap();
        String[] entries = value.substring(1, value.length() - 1).split(", ");
        Map<Object, Object> map = new HashMap<>();
        for (String entry : entries) {
            String[] keyValue = entry.split("=");
            Object key = deserializeItem(keyValue[0], Object.class);
            Object val = deserializeItem(keyValue[1], Object.class);
            map.put(key, val);
        }
        return map;
    }
}