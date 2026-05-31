package me.googas.bot.core.commands;

import com.github.chevyself.starbox.annotations.Command;
import com.github.chevyself.starbox.annotations.Free;
import com.github.chevyself.starbox.annotations.Parent;
import com.github.chevyself.starbox.result.Result;
import java.util.Collection;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.bot.api.Guido;
import me.googas.bot.core.commands.middleware.GuidoJdaPermission;
import me.googas.bot.core.discord.GuidoGuild;
import me.googas.bot.core.handlers.matches.MatchEloCalculator;

public class SeasonCommands {

  @Parent
  @GuidoJdaPermission("guido.season")
  @Command(aliases = "season", description = "Manage the season")
  public Result season() {
    return Result.of("Please use a subcommand");
  }

  @Command(aliases = "reset", description = "Resets the season")
  public Result season(
      GuidoGuild guild,
      @Free(
              name = "Reset",
              description = "Reset wins and loses",
              suggestions = {"false", "true"})
          boolean reset) {
    Collection<LinkableInfo> links = Guido.getHandlers().getLoader().getLinks().getLinks(-1, -1);
    for (LinkableInfo link : links) {
      Linkable data = link.getLink();
      if (data == null) continue;
      Guido.getHandlers()
          .getHandler(MatchEloCalculator.class)
          .setNewSeasonElo(data, guild.getLadders());
      if (reset) data.resetWinsAndLoses(guild.getLadders());
    }
    return Result.of("Season has been reset");
  }
}
