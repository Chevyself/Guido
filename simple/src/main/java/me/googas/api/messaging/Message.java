package me.googas.api.messaging;

import lombok.NonNull;
import me.googas.api.Informative;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.matches.team.Team;
import me.googas.commons.cache.Catchable;

/**
 * A message is a notification a chat message and more. It can be, for example, an invitation for a
 * {@link Linkable} to participate in a {@link Team}
 */
public interface Message extends Catchable, Informative {

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
  LinkableInfo getSender();

  /**
   * Get the receiver of the message
   *
   * @return the receiver
   */
  @NonNull
  LinkableInfo getReceiver();
}
