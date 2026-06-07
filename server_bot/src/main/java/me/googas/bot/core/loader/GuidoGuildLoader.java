package me.googas.bot.core.loader;

import lombok.NonNull;
import me.googas.bot.core.discord.GuidoGuild;
import net.dv8tion.jda.api.entities.Guild;

public interface GuidoGuildLoader {
  @NonNull
  default GuidoGuild getGuild(@NonNull Guild guild) {
    return this.getGuildOrCreate(guild.getIdLong());
  }

  GuidoGuild getGuildOrCreate(long id);
}
