package ru.bagrusss.helpers;

import com.google.gson.JsonObject;

import java.io.PrintWriter;

/**
 * Created by vladislav
 */
public final class Errors {

    public static final int CODE_OK = 0;
    public static final int CODE_NOT_FOUND = 1;
    public static final int CODE_INVALID_REQUEST = 2;
    public static final int CODE_INCORRECT_REQUEST = 3;
    public static final int CODE_UNKNOWN_ERROR = 4;
    public static final int CODE_USER_ALREADY_EXISTS = 5;

    public static final String MESSAGE_OK = "OK";
    public static final String MESSAGE_NOT_FOUND = "запрашиваемый объект не найден";
    public static final String MESSAGE_INVALID_REQUEST = "невалидный запрос";
    public static final String MESSAGE_INCORRECT_REQUEST = "некорректный запрос";
    public static final String MESSAGE_UNKNOWN_ERROR = "неизвестная ошибка";
    public static final String MESSAGE_USER_ALREADY_EXISTS = "такой юзер уже существует";

    public static void notFound(PrintWriter writer) {
        JsonObject resp = new JsonObject();
        resp.addProperty("code", CODE_NOT_FOUND);
        resp.addProperty("response", MESSAGE_NOT_FOUND);
        writer.write(resp.toString());
    }

    public static void invalidRequest(PrintWriter writer) {
        JsonObject resp = new JsonObject();
        resp.addProperty("code", CODE_INVALID_REQUEST);
        resp.addProperty("response", MESSAGE_INVALID_REQUEST);
        writer.write(resp.toString());
    }

    public static void incorrecRequest(PrintWriter writer) {
        JsonObject resp = new JsonObject();
        resp.addProperty("code", CODE_INCORRECT_REQUEST);
        resp.addProperty("response", MESSAGE_INCORRECT_REQUEST);
        writer.write(resp.toString());
    }

    public static void unknownError(PrintWriter writer) {
        JsonObject resp = new JsonObject();
        resp.addProperty("code", CODE_UNKNOWN_ERROR);
        resp.addProperty("response", MESSAGE_UNKNOWN_ERROR);
        writer.write(resp.toString());
    }

    public static void userAlreadyExists(PrintWriter writer) {
        JsonObject resp = new JsonObject();
        resp.addProperty("code", CODE_USER_ALREADY_EXISTS);
        resp.addProperty("response", MESSAGE_USER_ALREADY_EXISTS);
        writer.write(resp.toString());
    }

    public static void correct(PrintWriter writer, JsonObject response) {
        JsonObject resp = new JsonObject();
        resp.addProperty("code", CODE_OK);
        resp.add("response", response);
        writer.write(resp.toString());
    }

}
