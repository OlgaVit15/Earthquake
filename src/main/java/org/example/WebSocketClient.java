package org.example;

import org.json.JSONObject;

import javax.websocket.ClientEndpoint;
import javax.websocket.OnMessage;
import javax.websocket.Session;
import javax.websocket.WebSocketContainer;
import javax.websocket.ContainerProvider;
import java.net.URI;
import java.util.concurrent.CountDownLatch;

@ClientEndpoint
public class WebSocketClient {

    @OnMessage
    public void onMessage(String message) {
        System.out.println("Received message: " + message);

        JSONObject jsonObject = new JSONObject(message);
        String action = jsonObject.getString("action");

        JSONObject data = jsonObject.getJSONObject("data");
        String id = data.getString("id");

        JSONObject properties = data.getJSONObject("properties");
        String lu = properties.getString("lastupdate");
        String reg = properties.getString("flynn_region");
        double lat = properties.getDouble("lat");
        double lon = properties.getDouble("lon");
        double depth = properties.getDouble("depth");
        double mag = properties.getDouble("mag");
        System.out.println("try: " + lu + " " + reg + " " + mag);
        Event val = new Event(lu, reg, depth, lon, lat, mag);
        /*JSONObject eq = new JSONObject();
        eq.put("lastupdate", lu);
        eq.put("region", reg);
        eq.put("lat", lat);
        eq.put("lon", lon);
        eq.put("depth", depth);
        eq.put("mag", mag);*/

        KProducer kpr = new KProducer();
        kpr.kproducer(id, val);
    }

    public static void main(String[] args) {
        CountDownLatch latch = new CountDownLatch(1); // Синхронизация для ожидания сообщений
        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        String uri = "wss://www.seismicportal.eu/standing_order/websocket"; // Пример сервера WebSocket
        try {
            container.connectToServer(WebSocketClient.class, URI.create(uri));
            System.out.println("Connected to server");
            // Ожидание сообщений (или завершения)
            latch.await();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}