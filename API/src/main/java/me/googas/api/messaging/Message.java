package me.googas.api.messaging;

import lombok.NonNull;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.utility.ValuesMap;
import me.googas.commons.cache.Catchable;

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
  @NonNull
  String getId();

  /**
   * Get the sender of the message
   *
   * @return the sender of the message
   */
  @NonNull
  LinkableInfo getSender();

  /**
   * Get the receiver of the message
   *
   * @return the receiver
   */
  @NonNull
  LinkableInfo getReceiver();

  /**
   * Get the details of the message
   *
   * @return the details of the message
   */
  @NonNull
  ValuesMap getDetails();
}
