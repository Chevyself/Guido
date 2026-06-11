package me.googas.bot.core.util;

import java.awt.*;
import java.util.Map;
import java.util.Set;
import lombok.NonNull;
import me.googas.api.lang.LocaleFile;
import me.googas.api.loader.Loader;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.MatchTeam;
import me.googas.api.utility.Lots;
import me.googas.api.utility.Maps;
import net.dv8tion.jda.api.EmbedBuilder;

public class Matches {

  /** The keys to ignore from the details of the match when making the information */
  public static final Set<String> toIgnore =
      Lots.set("thumbnail", "guild", "type", "winners-difference", "losers-difference");

  @NonNull
  private static final Map<MatchStatus, Color> colors =
      Maps.builder(MatchStatus.WAITING, Colors.getColor("#f9313b"))
          .put(MatchStatus.READY, Colors.getColor("#efd004"))
          .put(MatchStatus.STARTING, Colors.getColor("#47db30"))
          .put(MatchStatus.PLAYING, Colors.getColor("#f99613"))
          .put(MatchStatus.VOIDED, Colors.getColor("#1e1e1e"))
          .put(MatchStatus.FINISHED, Colors.getColor("#f9313b"))
          .build();

  /**
   * Get the information from a match
   *
   * @param locale the locale that will read the information
   * @return the information of the match as a embed query
   */
  @NonNull
  public static EmbedBuilder getInformation(
      @NonNull Match match, @NonNull LocaleFile locale, @NonNull Loader loader) {
    EmbedBuilder builder = new EmbedBuilder();
    Map<String, String> placeholders = Maps.singleton("id", match.getId().toString());
    builder.setTitle(locale.get("match.title", placeholders));
    builder.setFooter(locale.get("footer"));
    builder.setColor(Matches.getColor(match.getStatus()));
    builder.setDescription(locale.get("match.description", placeholders));
    Matches.appendTeams(match, builder, loader);
    match.appendDetails(builder);
    return builder;
  }

  public static void appendTeams(
      @NonNull Match match, @NonNull EmbedBuilder builder, @NonNull Loader loader) {
    for (MatchTeam matchTeam : match.getTeams()) {
      builder.addField(
          Matches.getTitle(matchTeam, match),
          Lots.pretty(matchTeam.getMemberPublicDisplay(loader), "[]"),
          false);
    }
  }

  public static String getTitle(@NonNull MatchTeam team, @NonNull Match match) {
    MatchTeam winners = match.getWinners().orElse(null);
    if (winners == null) {
      return team.getName() + " (+0)";
    }
    if (team.getId() == winners.getId()) {
      return team.getName() + " (+" + match.getWinnersDifference() + ")";
    } else {
      return team.getName() + " (" + match.getLosersDifference() + ")";
    }
  }

  @NonNull
  public static Color getColor(@NonNull MatchStatus status) {
    Color color = Matches.colors.get(status);
    if (color != null) return color;
    return Matches.getColor(MatchStatus.VOIDED);
  }
}
