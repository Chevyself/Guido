package me.googas.bot.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import me.googas.commons.Lots;
import net.dv8tion.jda.api.entities.IMentionable;
import org.jetbrains.annotations.NotNull;

/** y Static utilities for mentions */
public class Mentions {

  /**
   * Get the mentions from a collection of mentionables.
   *
   * @param mentionables the mentionables to get the mentions from
   * @param <T> the type of mentionables
   * @return the mentions of the mentionables
   */
  @NotNull
  public static <T extends IMentionable> List<String> getMentions(
      @NotNull Collection<T> mentionables) {
    List<String> mentions = new ArrayList<>();
    for (T mentionable : mentionables) {
      mentions.add(mentionable.getAsMention());
    }
    return mentions;
  }

  /**
   * Get the mentions from a collection of mentionables in a pretty way. This will get the
   * collection of mentions and turn them into a pretty string using {@link Lots#pretty(Collection)}
   *
   * @param mentionables the mentionables to get the mentions from
   * @param <T> the type of mentionables
   * @return the mentions of the mentionables
   */
  @NotNull
  public static <T extends IMentionable> String pretty(@NotNull Collection<T> mentionables) {
    return Lots.pretty(Mentions.getMentions(mentionables));
  }
}
