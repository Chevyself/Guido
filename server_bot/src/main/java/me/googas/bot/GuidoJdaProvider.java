package me.googas.bot;

import java.util.Objects;
import lombok.NonNull;
import me.googas.api.links.JdaProvider;
import me.googas.bot.core.discord.GuidoGuild;
import net.dv8tion.jda.api.entities.Guild;

public class GuidoJdaProvider implements JdaProvider {

  @Deprecated private static final long GUILD_ID = 1511402659767128291L;

  @NonNull private final GuidoBotRuntime runtime;

  public GuidoJdaProvider(@NonNull GuidoBotRuntime runtime) {
    this.runtime = runtime;
  }

  public @NonNull GuidoGuild getGuidoGuild() {
    return this.runtime
        .getLoader()
        .getGuidoGuildLoader()
        .getGuildOrCreate(GuidoJdaProvider.GUILD_ID);
  }

  @Override
  public @NonNull Guild getGuild() {
    // TODO hardcoded atm
    Guild guild = this.runtime.getJdaConnection().getJda().getGuildById(GuidoJdaProvider.GUILD_ID);
    return Objects.requireNonNull(guild, "Failed to get default guild");
  }
}
