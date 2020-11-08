package me.googas.bot.core.server.receptors;

import java.util.Collection;
import me.googas.api.links.LinkedData;
import me.googas.api.links.LinkedInfo;
import me.googas.api.matches.Queue;
import me.googas.bot.core.Guido;
import me.googas.bot.core.handlers.matches.QueueHandler;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;

/** Receptors created for queue */
public class QueueReceptors {

  /**
   * Tell the program that an user left the queue
   *
   * @param info the information of the link
   * @return true if the user was removed from the queue
   */
  @Receptor("left-queue")
  public boolean leftQueue(@ParamName("info") LinkedInfo info) {
    LinkedData data = info.getLink();
    if (data != null) {
      Collection<Queue> queues = Guido.getHandler(QueueHandler.class).getQueues(info);
      for (Queue queue : queues) {
        queue.leave(info);
      }
      return true;
    }
    return false;
  }
}
