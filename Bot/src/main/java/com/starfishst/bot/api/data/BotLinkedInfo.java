package com.starfishst.bot.api.data;

import com.starfishst.bot.Guido;
import com.starfishst.bot.handlers.data.GuidoPermission;
import com.starfishst.bot.handlers.data.GuidoPermissionStack;
import com.starfishst.guido.api.data.links.LinkedInfo;
import org.jetbrains.annotations.Nullable;

/** An extension of uncompleted linked data */
public interface BotLinkedInfo extends LinkedInfo<GuidoPermission, GuidoPermissionStack> {

  @Override
  @Nullable
  default BotLinkedData getData() {
    return Guido.getDataLoader().getLinkedData(this.getType(), this.getIdentification());
  }
}
