package me.googas.bot.core.commands.administrative;

import com.github.chevyself.starbox.annotations.Command;
import com.github.chevyself.starbox.annotations.Parent;
import com.github.chevyself.starbox.annotations.Required;
import com.github.chevyself.starbox.result.Result;
import java.util.Collection;
import me.googas.api.lang.LocaleFile;
import me.googas.api.token.AuthLevel;
import me.googas.api.token.AuthToken;
import me.googas.api.user.UserData;
import me.googas.api.utility.Maps;
import me.googas.bot.GuidoBotRuntime;
import me.googas.bot.core.commands.middleware.GuidoJdaPermission;

/** Commands for token generation */
public class TokenCommands {

  @Parent
  @GuidoJdaPermission("user:guido.token")
  @Command(
      aliases = {"tokens", "token"},
      description = "tokens.desc")
  public Result token(LocaleFile locale, UserData sender, GuidoBotRuntime runtime) {
    Collection<? extends AuthToken> tokens =
        runtime.getLoader().getTokens().getTokens(sender.getId());
    if (tokens.isEmpty()) {
      return Result.of(locale.get("tokens.empty"));
    } else {
      StringBuilder builder = new StringBuilder();
      for (AuthToken token : tokens) {
        builder.append(
            locale.get(
                "tokens.token",
                Maps.builder("token", token.getToken())
                    .put("level", token.getAuthLevel().toString().toLowerCase())));
      }
      return Result.of(builder.toString());
    }
  }

  @GuidoJdaPermission("user:guido.token.generate")
  @Command(aliases = "generate", description = "token.gen.desc")
  public Result generate(
      UserData user,
      GuidoBotRuntime runtime,
      @Required(name = "token.gen.perm", description = "token.gen.perm.desc") AuthLevel level) {
    AuthToken token = runtime.getLoader().getTokens().create(user.getId(), level);
    return Result.of("Token generated use the string: " + token.getToken() + " to use it");
  }
}
