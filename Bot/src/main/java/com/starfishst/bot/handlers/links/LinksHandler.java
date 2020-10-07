package com.starfishst.bot.handlers.links;

import com.starfishst.bot.handlers.GuidoEventHandler;
import com.starfishst.core.utils.RandomUtils;
import com.starfishst.guido.api.data.UnlinkedMemberData;
import java.util.HashMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** Handles links */
public class LinksHandler implements GuidoEventHandler {

  /** The members that can be linked using the code */
  @NotNull private final HashMap<String, UnlinkedMemberData> toLink = new HashMap<>();

  /**
   * Adds a unlinked member to link
   *
   * @param member tue unlinked member to link
   * @return the string that can link the member
   */
  public String addToLink(@NotNull UnlinkedMemberData member) {
    for (String string : this.toLink.keySet()) {
      if (this.toLink.get(string) == member) {
        return string;
      }
    }
    String string = RandomUtils.nextString(16);
    toLink.put(string, member);
    return string;
  }

  /**
   * Get the unlinked member to link
   *
   * @param string the string to match
   * @return the user to link if found
   */
  @Nullable
  public UnlinkedMemberData getToLink(String string) {
    return this.toLink.get(string);
  }

  @Override
  public void close() {
    toLink.clear();
  }
}
