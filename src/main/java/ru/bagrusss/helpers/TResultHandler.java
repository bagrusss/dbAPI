package ru.bagrusss.helpers;

import org.jetbrains.annotations.Nullable;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Created by vladislav
 */

public interface TResultHandler<T> {

    @Nullable
    T handle(ResultSet rs) throws SQLException;
}
