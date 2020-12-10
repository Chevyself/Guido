package me.googas.bot.api.types;

import me.googas.api.links.LinkableInfo;
import me.googas.bot.core.Guido;

/** An extension of uncompleted linked data */
public interface BotLinkableInfo extends LinkableInfo {

  @Override
  default BotLinkable getLink() {
    return Guido.getDataLoader().getLinkedData(this.getType(), this.getIdentification());
  }
}
