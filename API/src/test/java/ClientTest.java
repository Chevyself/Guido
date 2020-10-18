import com.starfishst.guido.api.data.implementations.ClientImpl;
import com.starfishst.guido.api.data.implementations.data.PermissionStackImpl;
import com.starfishst.guido.api.data.links.LinkedDataType;
import java.io.IOException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;
import me.googas.commons.UUIDUtils;
import me.googas.commons.maps.Maps;
import me.googas.messaging.Request;
import me.googas.messaging.json.client.JsonClient;

/** A test for the client */
public class ClientTest {

  public static void main(String[] args) throws IOException {
    ClientImpl client = new ClientImpl("1Uv2AZduciPKwUL8");
    String nick = "Selfie";
    UUID uud = UUID.fromString("5eed208d-de58-4022-9ba7-6ccb5ea7e92a");
    String trimmed = UUIDUtils.trim(uud);
    JsonClient conn = client.startConnection();
    Scanner scanner = new Scanner(System.in);
    while (true) {
      while (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        if (line.equalsIgnoreCase("exit")) {
          conn.close();
          System.exit(0);
        } else if (line.equalsIgnoreCase("exists")) {
          conn.sendRequest(
              new Request<>(
                  Boolean.class,
                  "data-exists",
                  Maps.objects("type", LinkedDataType.MINECRAFT)
                      .append("identification", Maps.singleton("uuid", trimmed))
                      .build()),
              bol -> {
                System.out.println("Exists? " + bol);
              });
        } else if (line.equalsIgnoreCase("create")) {
          conn.sendRequest(
              new Request<>(
                  Boolean.class,
                  "create-minecraft",
                  Maps.objects("uuid", uud).append("nickname", nick).build()),
              bol -> {
                System.out.println("Created? " + bol);
              });
        } else if (line.equalsIgnoreCase("permission")) {
          conn.sendRequest(
              new Request<>(
                  PermissionStackImpl.class,
                  "permission",
                  Maps.objects("context", "bungee")
                      .append("type", LinkedDataType.MINECRAFT)
                      .append("identification", Maps.singleton("uuid", trimmed))
                      .build()),
              stack -> {
                System.out.println("Permission for bungee: " + stack.getPermissions());
              });
        } else if (line.equalsIgnoreCase("add-permission")) {
          conn.sendRequest(
              new Request<>(
                  Boolean.class,
                  "add-permission",
                  Maps.objects("type", LinkedDataType.MINECRAFT)
                      .append("context", "bungee")
                      .append("permission", "guido.*")
                      .append("identification", Maps.singleton("uuid", trimmed))
                      .build()),
              bol -> {
                System.out.println("Perm added? " + bol);
              });
        } else {
          conn.sendRequest(
              new Request<>(HashMap.class, "get-mc-by-name", Maps.singleton("nickname", line)),
              System.out::println);
        }
      }
    }
  }
}
