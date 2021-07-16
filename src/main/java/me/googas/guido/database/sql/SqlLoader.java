package me.googas.guido.database.sql;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Objects;
import lombok.Getter;
import lombok.NonNull;
import me.googas.guido.database.Loader;
import me.googas.net.cache.MemoryCache;

public class SqlLoader implements Loader {

  @Getter private final MemoryCache cache = new MemoryCache();
  @NonNull @Getter private final SqlQueries queries = SqlQueries.load();
  @NonNull @Getter private final SqlMembersLoader members = new SqlMembersLoader(this);
  @NonNull private final String url;
  private Connection connection;

  public SqlLoader(@NonNull String url) {
    this.url = url;
  }

  public boolean hasConnection() {
    try {
      return this.connection == null || !this.connection.isClosed();
    } catch (SQLException e) {
      return false;
    }
  }

  @NonNull
  public PreparedStatement statementOf(@NonNull String key) throws SQLException {
    return this.getQueries().statementOf(this.getConnection(), key);
  }

  @NonNull
  public PreparedStatement statementOf(@NonNull String key, Object... objects) throws SQLException {
    return this.getQueries().statementOf(this.getConnection(), key, objects);
  }

  @NonNull
  public Connection getConnection() {
    return Objects.requireNonNull(this.connection, "There's no SQL connection");
  }

  @Override
  public SqlLoader start() {
    if (this.connection == null) {
      try {
        this.connection = DriverManager.getConnection(this.url);
        this.members.initialize();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return this;
  }

  @Override
  public @NonNull SqlLoader close() {
    if (this.connection != null) {
      try {
        this.connection.close();
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return this;
  }
}
