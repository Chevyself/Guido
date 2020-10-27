import com.starfishst.guido.api.data.Group;
import com.starfishst.guido.api.data.implementations.ClientImpl;
import com.starfishst.guido.api.data.implementations.data.LinkedInfoImpl;
import com.starfishst.guido.api.data.implementations.data.PermissionStackImpl;
import com.starfishst.guido.api.data.implementations.data.ValuesMapImpl;
import com.starfishst.guido.api.data.links.LinkedDataType;
import com.starfishst.guido.api.data.links.LinkedInfo;
import com.starfishst.guido.api.data.matches.Ladder;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;
import java.util.UUID;
import me.googas.commons.UUIDUtils;
import me.googas.commons.maps.Maps;
import me.googas.messaging.Request;
import me.googas.messaging.api.MessengerListenFailException;
import me.googas.messaging.json.client.JsonClient;

/** A test for the client */
public class ClientTest {

  private static String groupId = "kI9w6F";

  public static void main(String[] args) throws IOException, MessengerListenFailException {
    ClientImpl client = new ClientImpl("1Uv2AZduciPKwUL8");
    String nick = "Selfie";
    UUID uuid = UUID.fromString("5eed208d-de58-4022-9ba7-6ccb5ea7e92a");
    String trimmed = UUIDUtils.trim(uuid);
    JsonClient conn = client.startConnection();
    Scanner scanner = new Scanner(System.in);
    LinkedInfoImpl info =
        new LinkedInfoImpl(
            LinkedDataType.MINECRAFT,
            new ValuesMapImpl(Maps.objects("uuid", trimmed).append("nickname", nick).build()));
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
                  Maps.objects("uuid", uuid).append("nickname", nick).build()),
              bol -> {
                System.out.println("Created? " + bol);
              });
        } else if (line.equalsIgnoreCase("permission")) {
          conn.sendRequest(
              new Request<>(
                  PermissionStackImpl.class,
                  "permission",
                  Maps.objects("context", "bungee").append("info", info).build()),
              stack -> {
                System.out.println("Permission for bungee: " + stack.getPermissions());
              });
        } else if (line.equalsIgnoreCase("add-permission")) {
          conn.sendRequest(
              new Request<>(
                  Boolean.class,
                  "add-permission",
                  Maps.objects("info", info)
                      .append("context", "bungee")
                      .append("permission", "guido.*")
                      .build()),
              bol -> {
                System.out.println("Perm added? " + bol);
              });
        } else if (line.equalsIgnoreCase("save-stats")) {
          conn.sendRequest(
              new Request<>(
                  Boolean.class,
                  "save-stats",
                  Maps.objects("identification", Maps.singleton("uuid", trimmed))
                      .append("stats", Maps.singleton("kills", 1))
                      .append("type", LinkedDataType.MINECRAFT)
                      .build()),
              bol -> {
                System.out.println("Saved? " + bol);
              });
        } else if (line.equalsIgnoreCase("group")) {
          conn.sendRequest(
              new Request<>(Group.class, "group", Maps.singleton("id", ClientTest.groupId)),
              System.out::println);
        } else if (line.equalsIgnoreCase("groups")) {
          conn.sendRequest(
              new Request<>(Group[].class, "groups"),
              arr -> {
                System.out.println(Arrays.toString(arr));
              });
        } else if (line.equalsIgnoreCase("create-group")) {
          conn.sendRequest(
              new Request<>(String.class, "create-group"),
              id -> {
                ClientTest.groupId = id;
                System.out.println(id);
              });
        } else if (line.equalsIgnoreCase("link")) {
          conn.sendRequest(
              new Request<>(
                  String.class,
                  "link-code",
                  Maps.objects("type", LinkedDataType.MINECRAFT)
                      .append("identification", Maps.singleton("uuid", trimmed))
                      .build()),
              System.out::println);
        } else if (line.equalsIgnoreCase("by-name")) {
          conn.sendRequest(
              new Request<>(LinkedInfo.class, "get-mc-by-name", Maps.singleton("nickname", nick)),
              linkInfo -> {
                System.out.println(linkInfo.getType());
                System.out.println(linkInfo.getIdentification().getMap());
              });
        } else if (line.equalsIgnoreCase("ladder")) {
          System.out.println(
              conn.sendRequest(
                  new Request<>(
                      Ladder.class,
                      "ladder",
                      Maps.objects("guild", 718281601112604675L).append("name", "1v1").build())));
        }
      }
    }
  }
}
