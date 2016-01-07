package ru.bagrusss.helpers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.jetbrains.annotations.Nullable;

import java.io.PrintWriter;

/**
 * Created by vladislav
 */

@SuppressWarnings("unused")
public final class Errors {

    public static final byte CODE_OK = 0;
    public static final byte CODE_NOT_FOUND = 1;
    public static final byte CODE_INVALID_REQUEST = 2;
    public static final byte CODE_INCORRECT_REQUEST = 3;
    public static final byte CODE_UNKNOWN_ERROR = 4;
    public static final byte CODE_USER_ALREADY_EXISTS = 5;

    public static final String MESSAGE_OK = "OK";
    public static final String MESSAGE_NOT_FOUND = "запрашиваемый объект не найден";
    public static final String MESSAGE_INVALID_REQUEST = "невалидный запрос";
    public static final String MESSAGE_INCORRECT_REQUEST = "некорректный запрос";
    public static final String MESSAGE_UNKNOWN_ERROR = "неизвестная ошибка";
    public static final String MESSAGE_USER_ALREADY_EXISTS = "такой юзер уже существует";


    private static void errorAPI(PrintWriter writer, byte code, @Nullable String msg) {
        JsonObject resp = new JsonObject();
        resp.addProperty("code", code);
        resp.addProperty("response", msg);
        writer.write(resp.toString());
    }

    public static void notFound(PrintWriter writer) {
        errorAPI(writer, CODE_NOT_FOUND, MESSAGE_NOT_FOUND);
    }

    public static void invalidRequest(PrintWriter writer) {
        errorAPI(writer, CODE_INVALID_REQUEST, MESSAGE_INVALID_REQUEST);
    }

    public static void incorrecRequest(PrintWriter writer) {
        errorAPI(writer, CODE_INCORRECT_REQUEST, MESSAGE_INCORRECT_REQUEST);
    }

    public static void unknownError(PrintWriter writer) {
        errorAPI(writer, CODE_UNKNOWN_ERROR, MESSAGE_UNKNOWN_ERROR);
    }

    public static void userAlreadyExists(PrintWriter writer) {
        errorAPI(writer, CODE_USER_ALREADY_EXISTS, MESSAGE_USER_ALREADY_EXISTS);
    }

    public static void correct(PrintWriter writer, @Nullable String msg) {
        errorAPI(writer, CODE_OK, msg);
    }

    public static void correct(PrintWriter writer, @Nullable JsonElement response) {
        JsonObject resp = new JsonObject();
        resp.addProperty("code", CODE_OK);
        resp.add("response", response);
        writer.write(resp.toString());
    }

}
