package me.googas.bot.core.commands;

import com.starfishst.core.annotations.Optional;
import com.starfishst.core.annotations.Parent;
import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.result.Result;
import java.util.Collection;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.bot.api.Guido;
import me.googas.bot.api.types.discord.BotGuild;
import me.googas.bot.core.handlers.matches.MatchEloCalculator;

public class SeasonCommands {

  @Parent
  @Command(aliases = "season", description = "Manage the season", node = "guido.season")
  public Result season() {
    return new Result("Please use a subcommand");
  }

  @Command(
      aliases = "reset",
      description = "Resets the season",
      node = "guido.season.resetWinsAndLoses")
  public Result season(
      BotGuild guild,
      @Optional(
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
    return new Result("Season has been reset");
  }
}
