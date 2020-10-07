package com.starfishst.bot.commands;

import com.starfishst.bot.api.data.BotUser;
import com.starfishst.bot.handlers.data.GuidoAuthToken;
import com.starfishst.commands.annotations.Command;
import com.starfishst.commands.annotations.Perm;
import com.starfishst.commands.result.Result;
import com.starfishst.core.annotations.Parent;
import com.starfishst.core.annotations.Required;
import com.starfishst.guido.api.data.AuthLevel;

/** Commands for token generation */
public class TokenCommands {

  @Parent
  @Command(
      aliases = "token",
      description = "See your generated tokens",
      permission = @Perm(node = "user:guido.token"))
  public Result token() {
    return new Result("Not fully implemented");
  }

  @Command(
      aliases = "generate",
      description = "Generate a new token",
      permission = @Perm(node = "user:guido.token.generate"))
  public Result generate(
      BotUser user,
      @Required(name = "permission", description = "The permission to use in the token")
          AuthLevel level) {
    GuidoAuthToken token = new GuidoAuthToken(level, user);
    return new Result("Token generated use the string: " + token.getToken() + " to use it");
  }
}
