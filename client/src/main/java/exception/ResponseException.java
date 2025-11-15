package exception;

import com.google.gson.Gson;

import java.util.HashMap;
import java.util.Map;

public class ResponseException extends Exception {

    public enum Code {
        ServerError,
        ClientError,
    }

    final private Code code;

    public ResponseException(Code code, String message) {
        super(message);
        this.code = code;
    }

    public String toJson() {
        return new Gson().toJson(Map.of("message", getMessage(), "status", code));
    }

    public static ResponseException fromJson(int httpStatusCode, String json) {
        var map = new Gson().fromJson(json, HashMap.class);
        var status = fromHttpStatusCode(httpStatusCode);
        String message = map.get("message").toString();
        return new ResponseException(status, message);
    }

    public Code code() {
        return code;
    }

    public static Code fromHttpStatusCode(int httpStatusCode) {
        if (httpStatusCode >= 400 && httpStatusCode < 500) {
            return Code.ClientError;
        }
        return Code.ServerError;
    }

}