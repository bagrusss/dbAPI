package ru.bagrusss.servlets;

import com.google.gson.Gson;
import ru.bagrusss.helpers.DBHelper;
import ru.bagrusss.helpers.Helper;

import javax.servlet.http.HttpServlet;

/**
 * Created by vladislav on 19.10.15.
 */


public class BaseServlet extends HttpServlet {

    protected static final int CODE_OK = 0;
    protected static final int CODE_NOT_FOUND = 1;
    protected static final int CODE_INVALID_REQUEST=2;
    protected static final int CODE_INCORRECT_REQUEST=3;
    protected static final int CODE_UNKNOWN_ERROR=4;
    protected static final int CODE_USER_ALREADY_EXISTS=5;

    protected static final String MESSAGE_OK="OK";
    protected static final String MESSAGE_NOT_FOUND="запрашиваемый объект не найден";
    protected static final String MESSAGE_INVALID_REQUEST="невалидный запрос";
    protected static final String MESSAGE_INCORRECT_REQUEST="некорректный запрос";
    protected static final String MESSAGE_UNKNOWN_ERROR="неизвестная ошибка";
    protected static final String MESSAGE_USER_ALREADY_EXISTS="такой юзер уже существует";

    protected static final String BASE_URL = "/db/api";
    protected final Helper mHelper = DBHelper.getInstance();
    protected final Gson mGson = new Gson();
}
