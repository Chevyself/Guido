package com.starfishst.bot.api.events.data.member;

import com.starfishst.bot.api.data.BotMember;
import org.jetbrains.annotations.NotNull;

/** Called when a link gets added in a member */
public class BotMemberNewLinkEvent extends BotMemberEvent {

  /** The key of the link */
  @NotNull private final String key;

  /** The value of the link */
  @NotNull private final String value;
  /**
   * Create the event
   *
   * @param data the data involved in the event
   * @param key the key of the link
   * @param value the value of the link
   */
  public BotMemberNewLinkEvent(
      @NotNull BotMember data, @NotNull String key, @NotNull String value) {
    super(data);
    this.key = key;
    this.value = value;
  }

  /**
   * Get the key of the link
   *
   * @return the key
   */
  @NotNull
  public String getKey() {
    return key;
  }

  /**
   * Get the value of the link
   *
   * @return the value
   */
  @NotNull
  public String getValue() {
    return value;
  }
}
