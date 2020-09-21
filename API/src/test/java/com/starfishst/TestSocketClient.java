package com.starfishst;

import com.starfishst.core.utils.maps.Maps;
import com.starfishst.guido.api.implementations.messaging.Request;
import com.starfishst.guido.api.implementations.messaging.Response;
import com.starfishst.guido.api.implementations.messaging.VoidRequest;
import com.starfishst.guido.api.implementations.messaging.json.JsonClient;
import java.io.IOException;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/** A test socket client */
public class TestSocketClient {

  public static void main(String[] args) throws IOException {
    Scanner scanner = new Scanner(System.in);
    JsonClient client = new JsonClient(new Socket("localhost", 3000), 1000);
    client.start();
    while (true) {
      if (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        if (line.equalsIgnoreCase("ping")) {
          List<Request> requets = new ArrayList<>();
          for (int i = 0; i < 5; i++) {
            requets.add(new Request("ping", Maps.singleton("start", System.currentTimeMillis())));
            try {
              Thread.sleep(1000);
            } catch (InterruptedException e) {
              e.printStackTrace();
            }
          }

          client.sendRequest(
              new Request("multi", Maps.singleton("requests", requets)),
              Response[].class,
              responses -> {
                for (Response<?> response : responses) {
                  Object object = response.getObject();
                  System.out.println(object);
                }
              });
        } else if (line.equalsIgnoreCase("exit")) {
          System.exit(0);
        } else if (line.equalsIgnoreCase("disconnect")) {
          client.sendRequest(new VoidRequest("disconnect"));
          client.close();
        }
      }
    }
  }
}
