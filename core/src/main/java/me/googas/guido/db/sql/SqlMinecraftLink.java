package me.googas.guido.db.sql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import lombok.NonNull;
import me.googas.guido.GuidoBot;
import me.googas.guido.type.MinecraftLink;
import me.googas.starbox.time.Time;
import me.googas.starbox.time.unit.Unit;
import net.dv8tion.jda.api.entities.User;

public class SqlMinecraftLink implements MinecraftLink {

  private final long user;
  private UUID uniqueId;

  public SqlMinecraftLink(long user, UUID uniqueId) {
    this.user = user;
    this.uniqueId = uniqueId;
  }

  @NonNull
  public static SqlMinecraftLink of(@NonNull ResultSet query) throws SQLException {
    String uuid = query.getString("uuid");
    return new SqlMinecraftLink(query.getLong("user"), uuid == null ? null : UUID.fromString(uuid));
  }

  @Override
  public @NonNull User getUser() {
    return Objects.requireNonNull(
        GuidoBot.getJda().getUserById(this.user),
        "User with the id " + this.user + " could not be found");
  }

  @Override
  public @NonNull Optional<UUID> getMinecraftUniqueId() {
    return Optional.ofNullable(this.uniqueId);
  }

  @Override
  public boolean setMinecraftUniqueId(UUID uniqueId) {
    if (GuidoBot.getLoader()
        .getSubloader(SqlLinksSubloader.class)
        .setMinecraftUniqueId(this, uniqueId)) {
      this.uniqueId = uniqueId;
      return true;
    }
    return false;
  }

  @Override
  public @NonNull Time getToRemove() {
    return Time.of(5, Unit.MINUTES);
  }
}
