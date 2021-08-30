package me.googas.guido.db.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.NonNull;
import me.googas.guido.db.LinksSubloader;
import me.googas.guido.type.MinecraftLink;
import me.googas.lazy.sql.LazySQL;
import me.googas.lazy.sql.LazySQLSubloader;
import me.googas.lazy.sql.LazySQLSubloaderBuilder;
import net.dv8tion.jda.api.entities.User;

public class SqlLinksSubloader extends LazySQLSubloader implements LinksSubloader {

  protected SqlLinksSubloader(@NonNull LazySQL parent) {
    super(parent);
  }

  @Override
  public @NonNull MinecraftLink getMinecraftLink(@NonNull User user) {
    return this.parent
        .getCache()
        .get(MinecraftLink.class, link -> link.getUser().equals(user))
        .orElseGet(
            () ->
                this.statement("SELECT * FROM `minecraft_links` WHERE `user`=?;")
                    .execute(
                        statement -> {
                          statement.setLong(1, user.getIdLong());
                          ResultSet query = statement.executeQuery();
                          if (query.next()) {
                            SqlMinecraftLink link = SqlMinecraftLink.of(query);
                            this.parent.getCache().add(link);
                            return link;
                          }
                          return null;
                        })
                    .orElseGet(
                        () -> {
                          this.statement("INSERT INTO `minecraft_links`(`user`) VALUES(?);")
                              .execute(
                                  insert -> {
                                    insert.setLong(1, user.getIdLong());
                                    insert.executeUpdate();
                                    return null;
                                  });
                          SqlMinecraftLink link = new SqlMinecraftLink(user.getIdLong(), null);
                          this.parent.getCache().add(link);
                          return link;
                        }));
  }

  @Override
  public @NonNull SqlLinksSubloader createTable() throws SQLException {
    this.statementWithKey("links.minecraft.create-table").execute(PreparedStatement::execute);
    return this;
  }

  public static class Builder implements LazySQLSubloaderBuilder {

    @Override
    @NonNull
    public SqlLinksSubloader build(@NonNull LazySQL lazySQL) {
      return new SqlLinksSubloader(lazySQL);
    }
  }
}
