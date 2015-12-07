package ru.bagrusss.helpers;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by vladislav on 19.10.15.
 */
public interface ResultHandler {
    void handle(ResultSet rs) throws SQLException;
}
