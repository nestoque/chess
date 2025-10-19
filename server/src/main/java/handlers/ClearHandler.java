package handlers;

import com.google.gson.Gson;

public class ClearHandler {
    public Object handleRequest() {
        ClearService clearService = new ClearService();
        clearService.clear();
        Gson json = new Gson();
        return json.toJson();
    }
}
