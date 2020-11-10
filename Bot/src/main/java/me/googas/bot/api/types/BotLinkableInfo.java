package me.googas.bot.api.types;

import me.googas.api.links.LinkableInfo;
import me.googas.bot.core.Guido;
import org.jetbrains.annotations.Nullable;

/** An extension of uncompleted linked data */
public interface BotLinkableInfo extends LinkableInfo {

  @Override
  @Nullable
  default BotLinkableData getLink() {
    switch (this.getType()) {
      case DISCORD_GUILD:
        return Guido.getDataLoader().getLinkedData(this.getType(), this.getIdentification());
      case DISCORD:
      case MINECRAFT:
        return Guido.getDataLoader().getLinkedData(this.getType(), this.getIdentification());
      default:
        throw new IllegalStateException(this.getType() + " is not a valid type");
    }
  }
}
