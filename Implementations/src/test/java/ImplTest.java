import com.starfishst.guido.api.implementations.messaging.json.JsonClient;
import com.starfishst.guido.api.implementations.messaging.json.requests.data.MemberDataRequest;
import com.starfishst.guido.implementations.ImplementationClient;
import java.io.IOException;
import java.util.Scanner;

public class ImplTest {

  public static void main(String[] args) {
    ImplementationClient client = new ImplementationClient("hIkECEbo6yN6Nyqh");
    try {
      client.startConnection();
    } catch (IOException e) {
      e.printStackTrace();
    }
    Scanner scanner = new Scanner(System.in);
    while (true) {
      if (scanner.hasNextLine()) {
        String line = scanner.nextLine();
        if (line.equalsIgnoreCase("member")) {
          JsonClient connection = client.getConnection();
          if (connection != null) {
            connection.sendRequest(
                new MemberDataRequest(760968935859683430L, 86321059636203520L, false, true),
                memberData -> {
                  System.out.println("Received data");
                  System.out.println(memberData.getPermissions());
                  System.out.println(memberData);
                });
          }
        }
      }
    }
  }
}
