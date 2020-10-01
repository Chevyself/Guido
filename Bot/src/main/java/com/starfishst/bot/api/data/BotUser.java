package com.starfishst.bot.api.data;

import com.starfishst.bot.Guido;
import com.starfishst.guido.api.data.UserData;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Nullable;

/** The user data of a bot */
public interface BotUser extends UserData, BotPermissible {

  /**
   * Get the discord of the user
   *
   * @return the discord of the user
   */
  @Nullable
  default User getDiscord() {
    return Guido.getConnection().validatedJda().getUserById(this.getId());
  }
}
