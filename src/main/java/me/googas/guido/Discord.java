package me.googas.guido;

import java.util.ArrayList;
import java.util.List;
import lombok.NonNull;
import net.dv8tion.jda.api.entities.IMentionable;

public class Discord {

  @NonNull
  public static List<String> getMentions(@NonNull List<? extends IMentionable> mentionables) {
    List<String> mentions = new ArrayList<>(mentionables.size());
    for (IMentionable mentionable : mentionables) {
      mentions.add(mentionable.getAsMention());
    }
    return mentions;
  }

  @NonNull
  public static String pretty(@NonNull List<? extends IMentionable> mentionables) {
    return Discord.prettyToString(Discord.getMentions(mentionables));
  }

  @NonNull
  public static String prettyToString(@NonNull List<?> list) {
    return list.toString().replace("[", "").replace("]", "");
  }
}
