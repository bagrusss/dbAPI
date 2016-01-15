package ru.bagrusss.helpers;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by vladislav
 */

public interface TResultHandler<T> {
    T handle(ResultSet rs) throws SQLException;
}
