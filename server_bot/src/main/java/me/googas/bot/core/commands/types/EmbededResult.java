package me.googas.bot.core.commands.types;

import com.github.chevyself.starbox.result.Result;
import lombok.Getter;
import lombok.NonNull;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;

public class EmbededResult implements Result {
  @NonNull @Getter private final EmbedBuilder embedBuilder;

  public EmbededResult(@NonNull EmbedBuilder embedBuilder) {
    this.embedBuilder = embedBuilder;
  }

  @NonNull
  public MessageEmbed build() {
    return this.embedBuilder.build();
  }
}
