package me.googas.bot.core.commands;

import com.starfishst.core.annotations.Parent;
import com.starfishst.core.annotations.Required;
import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.result.Result;
import java.util.Collection;
import me.googas.api.lang.LocaleFile;
import me.googas.api.token.AuthLevel;
import me.googas.api.token.AuthToken;
import me.googas.api.user.UserData;
import me.googas.bot.core.Guido;
import me.googas.bot.core.types.GuidoAuthToken;
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
      node = "user:guido.token")
  public Result token(LocaleFile locale, UserData sender) {
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
  @Command(aliases = "generate", description = "token.gen.desc", node = "user:guido.token.generate")
  public Result generate(
      UserData user,
      @Required(name = "token.gen.perm", description = "token.gen.perm.desc") AuthLevel level) {
    GuidoAuthToken token = new GuidoAuthToken(level, user.getId()).cache();
    return new Result("Token generated use the string: " + token.getToken() + " to use it");
  }
}
