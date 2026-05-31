package me.googas.bot;

import com.github.chevyself.starbox.jda.ListenerOptions;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.Guild;

public class GuidoListenerOptions implements ListenerOptions {
  @Override
  public @NonNull String getPrefix(Guild guild) {
    return "-";
  }
}
