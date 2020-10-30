package me.googas.bot.api.data;

import me.googas.api.links.LinkedInfo;
import me.googas.bot.Guido;
import org.jetbrains.annotations.Nullable;

/** An extension of uncompleted linked data */
public interface BotLinkedInfo extends LinkedInfo {

  @Override
  @Nullable
  default BotLinkedData getLink() {
    switch (this.getType()) {
      case DISCORD_GUILD:
        return Guido.getDataLoader().getLinkedData(this.getType(), this.getIdentification(), true);
      case DISCORD:
      case MINECRAFT:
        return Guido.getDataLoader().getLinkedData(this.getType(), this.getIdentification(), false);
      default:
        throw new IllegalStateException(this.getType() + " is not a valid type");
    }
  }
}
