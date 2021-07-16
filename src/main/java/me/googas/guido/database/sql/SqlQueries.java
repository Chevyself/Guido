package me.googas.guido.database.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.Getter;
import lombok.NonNull;
import me.googas.guido.GuidoFiles;
import me.googas.starbox.Strings;

public class SqlQueries {

  @NonNull public static final Pattern PATTERN = Pattern.compile("%.*?%");

  @NonNull @Getter private final Properties queries;

  public SqlQueries(@NonNull Properties queries) {
    this.queries = queries;
  }

  public static SqlQueries load() {
    Properties properties = GuidoFiles.Contexts.PROPERTIES.read(GuidoFiles.Resources.SQL_QUERIES);
    Set<Map.Entry<Object, Object>> entries = new HashSet<>(properties.entrySet());
    for (Map.Entry<Object, Object> entry : entries) {
      Object value = entry.getValue();
      if (value instanceof String) {
        String stringValue = (String) value;
        Matcher matcher = SqlQueries.PATTERN.matcher(stringValue);
        while (matcher.find()) {
          String rawPlaceholder = matcher.group();
          String placeholder = rawPlaceholder.replace("%", "");
          String placeholderValue = properties.getProperty(placeholder);
          if (placeholderValue != null) {
            stringValue = stringValue.replace(rawPlaceholder, placeholderValue);
          }
        }
        properties.put(entry.getKey(), stringValue);
      }
    }
    return new SqlQueries(properties);
  }

  @NonNull
  public PreparedStatement statementOf(@NonNull Connection connection, @NonNull String key)
      throws SQLException {
    String sql = this.queries.getProperty(key);
    if (sql == null) {
      throw new IllegalArgumentException("Could not find SQL for key: " + key);
    }
    return connection.prepareStatement(sql);
  }

  @NonNull
  public PreparedStatement statementOf(
      @NonNull Connection connection, @NonNull String key, Object... objects) throws SQLException {
    String sql = this.queries.getProperty(key);
    if (sql == null) {
      throw new IllegalArgumentException("Could not find SQL for key: " + key);
    }
    return connection.prepareStatement(Strings.format(sql, objects));
  }
}
