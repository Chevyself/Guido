import java.util.Map;
import me.googas.guido.MemberStats;
import me.googas.guido.database.sql.SqlLoader;
import me.googas.guido.database.sql.SqlQueries;
import me.googas.io.StarboxFile;

public class QueriesTest {

  public static void main(String[] args) {
    for (Map.Entry<Object, Object> entry : SqlQueries.load().getQueries().entrySet()) {
      System.out.println("entry = " + entry);
    }

    long id = 1;
    long guild = 1;
    StarboxFile file = new StarboxFile(StarboxFile.DIR, "test.db");
    String url = "jdbc:" + file.getAbsoluteFile().toURI().toString().replaceFirst("^file", "h2");
    System.out.println("url = " + url);
    SqlLoader loader = new SqlLoader(url);
    loader.start();
    MemberStats stats = loader.getMembers().getMemberStats(1, 1);
    System.out.println("stats = " + stats);
    loader.close();
    /*

    boolean stats = loader.getMembers().createMemberStats(new MemberStatsProvider(id, guild, 0, 0, 0));
    System.out.println("stats = " + stats);
    loader.close();

     */
  }
}
