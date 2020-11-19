package me.googas.api.messaging;

import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.utility.ValuesMap;
import me.googas.commons.cache.Catchable;
import org.jetbrains.annotations.NotNull;

/**
 * A message is a notification a chat message and more. It can be, for example, an invitation for a
 * {@link Linkable} to participate in a {@link me.googas.api.matches.TeamData}
 */
public interface Message extends Catchable {

  /**
   * Get the id of the message
   *
   * @return the id of the message
   */
  @NotNull
  String getId();

  /**
   * Get the sender of the message
   *
   * @return the sender of the message
   */
  @NotNull
  LinkableInfo getSender();

  /**
   * Get the receiver of the message
   *
   * @return the receiver
   */
  @NotNull
  LinkableInfo getReceiver();

  /**
   * Get the details of the message
   *
   * @return the details of the message
   */
  @NotNull
  ValuesMap getDetails();
}
