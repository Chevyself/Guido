package com.starfishst;

import com.starfishst.core.utils.maps.Maps;
import com.starfishst.guido.api.implementations.messaging.Request;
import com.starfishst.guido.api.implementations.messaging.VoidRequest;
import com.starfishst.guido.api.implementations.messaging.json.JsonClient;
import com.starfishst.guido.api.implementations.messaging.json.requests.Ping;
import java.io.IOException;
import java.net.Socket;
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
          client.sendRequest(
              new Request("ping", Maps.singleton("start", System.currentTimeMillis())),
              Ping.class,
              ping -> {
                System.out.println(ping.getMillis());
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
