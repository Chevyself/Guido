package me.googas.guido.db.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.Optional;
import java.util.UUID;
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
  public @NonNull Optional<MinecraftLink> getMinecraftLink(@NonNull UUID uuid) {
    MinecraftLink minecraftLink =
        this.parent
            .getCache()
            .get(
                MinecraftLink.class,
                link ->
                    link.getMinecraftUniqueId().isPresent()
                        && link.getMinecraftUniqueId().get().equals(uuid))
            .orElseGet(
                () ->
                    this.statement("SELECT * FROM `minecraft_links` WHERE `uuid`=?;")
                        .execute(
                            statement -> {
                              statement.setString(1, uuid.toString());
                              ResultSet query = statement.executeQuery();
                              if (query.next()) {
                                SqlMinecraftLink link = SqlMinecraftLink.of(query);
                                this.parent.getCache().add(link);
                                return link;
                              }
                              return null;
                            })
                        .orElse(null));
    return Optional.ofNullable(minecraftLink);
  }

  @Override
  public @NonNull SqlLinksSubloader createTable() {
    this.statementWithKey("links.minecraft.create-table").execute(PreparedStatement::execute);
    return this;
  }

  public boolean setMinecraftUniqueId(@NonNull SqlMinecraftLink link, UUID uniqueId) {
    return this.statement("UPDATE `minecraft_links` SET `uuid`=? WHERE `user`=?;")
        .execute(
            statement -> {
              if (uniqueId == null) {
                statement.setNull(1, Types.VARCHAR);
              } else {
                statement.setString(1, uniqueId.toString());
              }
              statement.setLong(2, link.getUser().getIdLong());
              return statement.executeUpdate() > 0;
            })
        .orElse(false);
  }

  public static class Builder implements LazySQLSubloaderBuilder {

    @Override
    @NonNull
    public SqlLinksSubloader build(@NonNull LazySQL lazySQL) {
      return new SqlLinksSubloader(lazySQL);
    }
  }
}
