package me.googas.bot;

import java.util.Objects;
import lombok.NonNull;
import me.googas.api.links.JdaProvider;
import me.googas.bot.core.GuidoBotRuntime;
import me.googas.server.GuidoGuild;
import net.dv8tion.jda.api.entities.Guild;

public class GuidoJdaProvider implements JdaProvider {

  @NonNull private final GuidoBotRuntime runtime;
  private final long guildId;

  public GuidoJdaProvider(@NonNull GuidoBotRuntime runtime, long guildId) {
    this.runtime = runtime;
    this.guildId = guildId;
  }

  public @NonNull GuidoGuild getGuidoGuild() {
    return this.runtime.getLoader().getGuidoGuildLoader().getGuildOrCreate(this.guildId);
  }

  @Override
  public @NonNull Guild getGuild() {
    // TODO hardcoded atm
    Guild guild = this.runtime.getJdaConnection().getJda().getGuildById(this.guildId);
    return Objects.requireNonNull(guild, "Failed to get default guild");
  }
}
