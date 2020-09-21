package com.starfishst;

import com.starfishst.guido.api.implementations.messaging.json.JsonSocketServer;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

/** A test for socket servers */
public class TestSocketServer {

  public static void main(String[] args) throws IOException {
    JsonSocketServer server = new JsonSocketServer(3000, 1000);
    new Timer()
        .schedule(
            new TimerTask() {
              @Override
              public void run() {}
            },
            1000,
            3000);
    server.start();
  }
}
