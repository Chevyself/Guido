import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import me.googas.io.StarboxFile;

public class TestDatabase {

  public static void main(String[] args) throws SQLException {
    StarboxFile file = new StarboxFile(StarboxFile.DIR, "test.db");
    String url = "jdbc:" + file.getAbsoluteFile().toURI().toString().replaceFirst("^file", "h2");
    System.out.println("url = " + url);
    Connection connection = DriverManager.getConnection(url);
    DatabaseMetaData meta = connection.getMetaData();
    System.out.println("meta.getDriverName() = " + meta.getDriverName());
    String sql =
        "CREATE TABLE IF NOT EXISTS sex (\n"
            + " id integer PRIMARY KEY,\n"
            + " name text NOT NULL,\n"
            + " capacity real\n"
            + ");";
    Statement statement = connection.createStatement();
    boolean execute = statement.execute(sql);

    PreparedStatement insert =
        connection.prepareStatement("INSERT INTO employees(id, name, capacity) VALUES(?,?,?)");
    insert.setInt(1, 1);
    insert.setString(2, "Edward");
    insert.setDouble(3, 120.5);
    int update = insert.executeUpdate();
    System.out.println("update = " + update);

    System.out.println("execute = " + execute);
    connection.close();
  }
}
