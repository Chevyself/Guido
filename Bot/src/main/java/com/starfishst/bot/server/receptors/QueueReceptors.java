package com.starfishst.bot.server.receptors;

import com.starfishst.bot.Guido;
import com.starfishst.bot.api.data.BotLinkedData;
import com.starfishst.bot.api.data.BotLinkedInfo;
import com.starfishst.bot.handlers.data.GuidoValuesMap;
import com.starfishst.bot.handlers.matches.MatchMakingHandler;
import com.starfishst.guido.api.data.links.LinkedDataType;
import com.starfishst.guido.api.data.matches.Queue;
import java.util.Collection;
import java.util.Map;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;

/** Receptors created for queue */
public class QueueReceptors {

  @Receptor(method = "left-queue")
  public boolean leftQueue(
      @ParamName(name = "type") LinkedDataType type,
      @ParamName(name = "identification") Map<String, Object> identification) {
    BotLinkedData data =
        Guido.getDataLoader().getLinkedData(type, new GuidoValuesMap(identification));
    if (data != null) {
      BotLinkedInfo info = data.getInfo();
      Collection<Queue> queues = Guido.getHandler(MatchMakingHandler.class).getQueues(info);
      for (Queue queue : queues) {
        queue.leave(info);
      }
    }
    return false;
  }
}
