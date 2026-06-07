package me.googas.bot.core.commands.providers;

import com.github.chevyself.starbox.exceptions.ArgumentProviderException;
import com.github.chevyself.starbox.jda.context.CommandContext;
import com.github.chevyself.starbox.jda.providers.type.JdaArgumentProvider;
import java.util.Optional;
import lombok.NonNull;
import me.googas.api.links.DiscordLinkable;
import me.googas.api.links.MinecraftLinkable;
import me.googas.api.loader.MinecraftLinkableLoader;
import me.googas.api.utility.Maps;
import me.googas.bot.GuidoBotRuntime;
import me.googas.bot.core.util.Lang;

public class MinecraftLinkableProvider implements JdaArgumentProvider<MinecraftLinkable> {

  @NonNull private final GuidoBotRuntime runtime;

  public MinecraftLinkableProvider(@NonNull GuidoBotRuntime runtime) {
    this.runtime = runtime;
  }

  @Override
  public @NonNull Class<MinecraftLinkable> getClazz() {
    return MinecraftLinkable.class;
  }

  @Override
  public @NonNull MinecraftLinkable fromString(@NonNull String s, @NonNull CommandContext context)
      throws ArgumentProviderException {
    MinecraftLinkableLoader minecraftLinks = runtime.getLoader().getMinecraftLinks();

    return minecraftLinks
        .getByNickname(s)
        .or(() -> minecraftLinks.getByIdRegex(s))
        .or(
            () -> {
              try {
                DiscordLinkable discord =
                    context.getProvidersRegistry().fromString(s, DiscordLinkable.class, context);
                return discord
                    .getLinkedUserId()
                    .flatMap(
                        linkedUserId ->
                            runtime.getLoader().getMinecraftLinks().getByLinkedUser(linkedUserId));
              } catch (ArgumentProviderException ignored) {
                return Optional.empty();
              }
            })
        .orElseThrow(
            () -> Lang.getException("invalid.minecraft", Maps.singleton("string", s), context));
  }
}
