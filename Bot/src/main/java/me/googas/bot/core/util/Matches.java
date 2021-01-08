package me.googas.bot.core.util;

import com.starfishst.jda.utils.embeds.EmbedQuery;
import java.awt.*;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import lombok.NonNull;
import me.googas.api.lang.LocaleFile;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.MatchTeam;
import me.googas.bot.api.Guido;
import me.googas.bot.api.types.discord.BotGuild;
import me.googas.commons.Lots;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.EmbedBuilder;

public class Matches {

  /** The keys to ignore from the details of the match when making the information */
  public static final Set<String> toIgnore =
      Lots.set("thumbnail", "guild", "type", "winners-difference", "losers-difference");

  @NonNull
  private static final Map<MatchStatus, Color> colors =
      Maps.builder(MatchStatus.WAITING, Colors.getColor("#f9313b"))
          .append(MatchStatus.READY, Colors.getColor("#efd004"))
          .append(MatchStatus.STARTING, Colors.getColor("#47db30"))
          .append(MatchStatus.PLAYING, Colors.getColor("#f99613"))
          .append(MatchStatus.VOIDED, Colors.getColor("#1e1e1e"))
          .append(MatchStatus.FINISHED, Colors.getColor("#f9313b"))
          .build();

  /**
   * Get the guild in which a match was played
   *
   * @param match the match to get the guild from
   * @return the guild of the match or null if not found
   */
  public static BotGuild getGuild(@NonNull Match match) {
    return Guido.getHandlers().getDiscordLoader().getGuild(match.getGuildId());
  }

  /**
   * Get the information from a match
   *
   * @param locale the locale that will read the information
   * @return the information of the match as a embed query
   */
  @NonNull
  public static EmbedQuery getInformation(@NonNull Match match, @NonNull LocaleFile locale) {
    EmbedBuilder builder = new EmbedBuilder();
    String thumbnail = match.getDetails().get("thumbnail", String.class);
    builder.setTitle(locale.get("match.title", Maps.singleton("id", match.getId())));
    builder.setFooter(locale.get("footer"));
    builder.setColor(Matches.getColor(match.getStatus()));
    builder.setDescription(locale.get("match.description", Maps.singleton("id", match.getId())));
    if (thumbnail != null) {
      builder.setThumbnail(thumbnail);
    }
    Matches.appendDetails(match, locale, builder);
    Matches.appendTeams(match, builder);
    return new EmbedQuery(builder);
  }

  public static void appendTeams(@NonNull Match match, @NonNull EmbedBuilder builder) {
    for (MatchTeam matchTeam : match.getTeams()) {
      builder.addField(
          Matches.getTitle(matchTeam, match),
          Lots.pretty(matchTeam.getMemberSingles(), "[]"),
          false);
    }
  }

  public static String getTitle(@NonNull MatchTeam team, @NonNull Match match) {
    if (match.getWinners() == null) {
      return team.getName() + " (+0)";
    }
    if (team.equals(match.getWinners())) {
      return team.getName()
          + " (+"
          + match.getDetails().getOr("winners-difference", Number.class, 0).intValue()
          + ")";
    } else {
      return team.getName()
          + " (-"
          + match.getDetails().getOr("losers-difference", Number.class, 0).intValue()
          + ")";
    }
  }

  public static void appendDetails(
      @NonNull Match match, @NonNull LocaleFile locale, @NonNull EmbedBuilder builder) {
    match
        .getDetails()
        .getMap()
        .forEach(
            (key, value) -> {
              if (!Matches.toIgnore.contains(key)) {
                String fieldDesc;
                if (value instanceof Collection) {
                  fieldDesc = Lots.pretty((Collection<?>) value);
                } else {
                  fieldDesc = value.toString();
                }
                builder.addField(locale.get(key), fieldDesc, true);
              }
            });
  }

  @NonNull
  public static Color getColor(@NonNull MatchStatus status) {
    Color color = Matches.colors.get(status);
    if (color != null) return color;
    return Matches.getColor(MatchStatus.VOIDED);
  }
}
