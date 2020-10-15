package com.starfishst.bot.commands;

import com.starfishst.bot.api.data.BotUser;
import com.starfishst.bot.handlers.data.GuidoAuthToken;
import com.starfishst.core.annotations.Parent;
import com.starfishst.core.annotations.Required;
import com.starfishst.guido.api.data.token.AuthLevel;
import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.annotations.Perm;
import com.starfishst.jda.result.Result;

/** Commands for token generation */
public class TokenCommands {

  /**
   * See the tokens of an user
   *
   * @return the tokens of the user message
   */
  @Parent
  @Command(
      aliases = "token",
      description = "token.desc",
      permission = @Perm(node = "user:guido.token"))
  public Result token() {
    return new Result("Not fully implemented");
  }

  /**
   * Generate a token
   *
   * @param user the user that generates the token
   * @param level the level of the token
   * @return the generated token
   */
  @Command(
      aliases = "generate",
      description = "token.gen.desc",
      permission = @Perm(node = "user:guido.token.generate"))
  public Result generate(
      BotUser user,
      @Required(name = "token.gen.perm", description = "token.gen.perm.desc")
          AuthLevel level) {
    GuidoAuthToken token = new GuidoAuthToken(level, user);
    return new Result("Token generated use the string: " + token.getToken() + " to use it");
  }
}
