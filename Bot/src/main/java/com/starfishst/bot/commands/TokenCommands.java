package com.starfishst.bot.commands;

import com.starfishst.bot.Guido;
import com.starfishst.bot.api.data.BotUser;
import com.starfishst.bot.handlers.data.GuidoAuthToken;
import com.starfishst.core.annotations.Parent;
import com.starfishst.core.annotations.Required;
import com.starfishst.guido.api.data.lang.LocaleFile;
import com.starfishst.guido.api.data.token.AuthLevel;
import com.starfishst.guido.api.data.token.AuthToken;
import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.annotations.Perm;
import com.starfishst.jda.result.Result;
import java.util.Collection;
import me.googas.commons.Strings;
import me.googas.commons.maps.Maps;

/** Commands for token generation */
public class TokenCommands {

  /**
   * See the tokens of an user.
   *
   * @param locale the locale of the sender
   * @param sender the sender of the command
   * @return the tokens of the user message
   */
  @Parent
  @Command(
      aliases = {"tokens", "token"},
      description = "tokens.desc",
      permission = @Perm(node = "user:guido.token"))
  public Result token(LocaleFile locale, BotUser sender) {
    Collection<? extends AuthToken> tokens = Guido.getDataLoader().getTokens(sender);
    if (tokens.isEmpty()) {
      return new Result(locale.get("tokens.empty"));
    } else {
      StringBuilder builder = Strings.getBuilder();
      for (AuthToken token : tokens) {
        builder.append(
            locale.get(
                "tokens.token",
                Maps.builder("token", token.getToken())
                    .append("level", token.getLevel().toString().toLowerCase())));
      }
      return new Result(builder.toString());
    }
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
      @Required(name = "token.gen.perm", description = "token.gen.perm.desc") AuthLevel level) {
    GuidoAuthToken token = new GuidoAuthToken(level, user);
    return new Result("Token generated use the string: " + token.getToken() + " to use it");
  }
}
