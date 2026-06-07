package dev.xevy.guido.bot;

import java.util.UUID;
import lombok.NonNull;
import lombok.experimental.Delegate;
import me.googas.api.links.MinecraftLinkable;
import me.googas.api.user.UserData;
import me.googas.bot.GuidoBot;
import me.googas.bot.core.GuidoBotRuntime;

public final class GuidoTestRuntime implements GuidoBotRuntime {

  @NonNull @Delegate private final GuidoBot parent;

  private GuidoTestRuntime(@NonNull GuidoBot parent) {
    this.parent = parent;
  }

  @NonNull
  public static GuidoTestRuntime createRuntime() {
    GuidoBot bot = new GuidoBot(new JGuidoRuntime(), new JGuidoConfig());
    bot.start();
    return new GuidoTestRuntime(bot);
  }

  @NonNull
  public MinecraftLinkable createMinecraftLinkable(@NonNull String nickname, boolean link) {
    MinecraftLinkable linkable = this.getLoader()
            .getMinecraftLinks()
            .updateOrCreate(UUID.randomUUID(), nickname, "", true);
    if (link) {
      UserData userData = this.getLoader().getUsers().create();
      linkable.setLinkedUser(userData);
    }
    return linkable;
  }
}
