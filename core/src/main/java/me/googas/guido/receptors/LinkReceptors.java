package me.googas.guido.receptors;

import java.util.Objects;
import java.util.UUID;
import lombok.Getter;
import lombok.NonNull;
import me.googas.guido.GuidoBot;
import me.googas.guido.db.LinksSubloader;
import me.googas.net.cache.Catchable;
import me.googas.net.sockets.json.ParamName;
import me.googas.net.sockets.json.Receptor;
import me.googas.starbox.time.Time;
import me.googas.starbox.time.unit.Unit;
import net.dv8tion.jda.api.entities.User;

public class LinkReceptors {

  @Receptor("links/link")
  public boolean link(@ParamName("code") int code) {
    return GuidoBot.getCache()
        .get(LinkCode.class, link -> link.getCode() == code)
        .map(LinkCode::apply)
        .orElse(false);
  }

  @Receptor("links/has")
  public boolean has(@ParamName("uuid") UUID uuid) {
    return GuidoBot.getLoader()
        .getSubloader(LinksSubloader.class)
        .getMinecraftLink(uuid)
        .isPresent();
  }

  public static class LinkCode implements Catchable {
    @Getter private final int code;
    @Getter private final long user;
    @NonNull private final UUID uniqueId;

    public LinkCode(int code, long user, @NonNull UUID uniqueId) {
      this.code = code;
      this.user = user;
      this.uniqueId = uniqueId;
    }

    @Override
    public @NonNull Time getToRemove() {
      return Time.of(30, Unit.SECONDS);
    }

    @NonNull
    public User getDiscordUser() {
      return Objects.requireNonNull(GuidoBot.getJda().getUserById(this.user));
    }

    public boolean apply() {
      if (GuidoBot.getLoader()
          .getSubloader(LinksSubloader.class)
          .getMinecraftLink(this.getDiscordUser())
          .setMinecraftUniqueId(this.uniqueId)) {
        GuidoBot.getCache().remove(this);
        return true;
      }
      return false;
    }
  }
}
