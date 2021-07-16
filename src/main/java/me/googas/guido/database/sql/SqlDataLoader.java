package me.googas.guido.database.sql;

import java.sql.SQLException;
import jdk.nashorn.internal.objects.annotations.Getter;
import lombok.NonNull;

public interface SqlDataLoader {

  @NonNull
  default SqlDataLoader initialize() throws SQLException {
    return this;
  }

  @Getter
  SqlLoader getLoader();
}
