package me.googas.bot.core.commands.types;

import com.github.chevyself.starbox.result.Result;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;

public class EmbededResult implements Result {
  @NonNull @Getter private final EmbedBuilder embedBuilder;

  public EmbededResult(@NonNull EmbedBuilder embedBuilder) {
    this.embedBuilder = embedBuilder;
  }
}
