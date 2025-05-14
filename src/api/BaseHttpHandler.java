package api;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class BaseHttpHandler {
    Gson gson = new Gson();

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] resp = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, resp.length);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(resp);
        }

    }

    protected void sendNotFound(HttpExchange exchange) throws IOException {
        String resp = "Ресурс не найден";
        sendText(exchange, resp, 404);
    }

    protected void sendHasInteractions(HttpExchange exchange) throws IOException {
        String resp = "Задача пересекается по времени с существующими";
        sendText(exchange, resp, 406);

    }

    protected void sendInternalError(HttpExchange exchange) throws IOException {
        String resp = "Внутренняя ошибка сервера";
        sendText(exchange, resp, 500);
    }

    protected String readRequestBody(InputStream inputStream) throws IOException {
        return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
    }

}
