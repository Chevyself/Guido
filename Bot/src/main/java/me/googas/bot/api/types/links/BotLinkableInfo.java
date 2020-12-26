package me.googas.bot.api.types.links;

import me.googas.api.links.LinkableInfo;
import me.googas.bot.Guido;

/** An extension of uncompleted linked data */
public interface BotLinkableInfo extends LinkableInfo {

  @Override
  default BotLinkable getLink() {
    return Guido.getDataLoader().getLink(this.getType(), this.getIdentification());
  }
}
