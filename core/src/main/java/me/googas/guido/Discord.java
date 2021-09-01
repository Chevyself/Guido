package me.googas.guido;

import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import me.googas.starbox.Strings;
import net.dv8tion.jda.api.entities.IMentionable;

public class Discord {

  @NonNull
  public static List<String> getMentions(@NonNull List<? extends IMentionable> mentionables) {
    return mentionables.stream().map(IMentionable::getAsMention).collect(Collectors.toList());
  }

  @NonNull
  public static String pretty(@NonNull List<? extends IMentionable> mentionables) {
    return Strings.pretty(Discord.getMentions(mentionables));
  }
}
