package client;

import java.io.IOException;
import java.util.Optional;
import java.util.Scanner;
import java.util.function.Consumer;
import me.googas.api.Requests;
import me.googas.api.client.Client;
import me.googas.api.client.data.SimpleValuesMap;
import me.googas.api.client.data.links.SimpleLinkableInfo;
import me.googas.api.client.data.permissions.SimplePermission;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.messaging.json.client.JsonClient;

/** A test for the client */
public class ClientTest {

  private static final String groupId = "kI9w6F";

  private static final LinkableInfo link =
      new SimpleLinkableInfo(
          LinkableType.MINECRAFT, new SimpleValuesMap("uuid", "5eed208dde5840229ba76ccb5ea7e92a"));

  public static void main(String[] args) throws IOException {
    // "167.114.49.251"
    // 5Eh8QKdS7GmrE0Gs
    // localhost
    Client client = new Client("5Eh8QKdS7GmrE0Gs", "167.114.49.251", 3000);
    JsonClient connection = client.startConnection();
    Scanner scanner = new Scanner(System.in);
    while (true) {
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        if (line.equalsIgnoreCase("exit")) {
          client.close();
          System.exit(0);
          break;
        }

        if (line.equalsIgnoreCase("stats")) {
          Requests.Links.stats(ClientTest.link).send(connection, ClientTest.consumer());
        }

        if (line.equalsIgnoreCase("groups")) {
          Requests.Groups.getGroups().send(connection, ClientTest.consumer());
        }

        if (line.equalsIgnoreCase("permissions")) {
          Requests.Links.permissions(ClientTest.link, "bungee", true)
              .send(connection, ClientTest.consumer());
        }

        if (line.equalsIgnoreCase("add")) {
          Requests.Links.permission(
                  ClientTest.link, "bungee", new SimplePermission("guido.test", true, -1))
              .send(connection, ClientTest.consumer());
        }

        if (line.equalsIgnoreCase("code")) {
          Requests.Server.linkCode(
                  new SimpleLinkableInfo(
                      LinkableType.MINECRAFT,
                      new SimpleValuesMap("uuid", "3cd86a0ba63d42ecb7047da79c1ac87a")))
              .send(connection, ClientTest.consumer());
        }

        if (line.equalsIgnoreCase("online")) {
          Requests.Links.preference(ClientTest.link, "online", true)
              .send(connection, ClientTest.consumer());
        }
      }
    }
  }

  private static <T> Consumer<Optional<T>> consumer() {
    return Requests.ifPresentElse(System.out::println, () -> System.out.println("Not present"));
  }
}
