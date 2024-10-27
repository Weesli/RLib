package net.weesli.rozsLib.database.component;


import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.sql.ResultSet;
import java.util.*;

@Getter@Setter
public class Result{

    private ResultSet resultset;

    public Result(ResultSet resultset) {
        this.resultset = resultset;
    }

    @SneakyThrows
    public <T> T get(String path, Class<T> type) {
        Object value = resultset.getObject(path);
        if (type.isInstance(value)) {
            return type.cast(value);
        } else if (value instanceof List) {
            List<?> list = (List<?>) value;
            List<T> castedList = new ArrayList<>();
            for (Object item : list) {
                if (type.isInstance(item)) {
                    castedList.add((T) item);
                } else {
                    throw new ClassCastException("Cannot cast " + item.getClass().getName() + " to " + type.getName());
                }
            }
            return (T) castedList;
        } else {
            throw new ClassCastException("Cannot cast " + value.getClass().getName() + " to " + type.getName());
        }
    }
}
