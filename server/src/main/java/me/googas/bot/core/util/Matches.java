package me.googas.bot.core.util;

import com.starfishst.commands.jda.utils.embeds.EmbedQuery;
import java.awt.*;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import lombok.NonNull;
import me.googas.api.lang.LocaleFile;
import me.googas.api.matches.AbstractMatch;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.MatchTeam;
import me.googas.bot.api.Guido;
import me.googas.bot.core.discord.GuidoGuild;
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
   * Get the guild in which a abstractMatch was played
   *
   * @param abstractMatch the abstractMatch to getId the guild from
   * @return the guild of the abstractMatch or null if not found
   */
  public static GuidoGuild getGuild(@NonNull AbstractMatch abstractMatch) {
    return Guido.getHandlers().getDiscordLoader().getGuild(abstractMatch.getGuildId());
  }

  /**
   * Get the information from a abstractMatch
   *
   * @param locale the locale that will read the information
   * @return the information of the abstractMatch as a embed query
   */
  @NonNull
  public static EmbedBuilder getInformation(
      @NonNull AbstractMatch abstractMatch, @NonNull LocaleFile locale) {
    EmbedBuilder builder = new EmbedBuilder();
    String thumbnail = abstractMatch.getString(null, "thumbnail", "");
    builder.setTitle(locale.get("match.title", Maps.singleton("id", abstractMatch.getId())));
    builder.setFooter(locale.get("footer"));
    builder.setColor(Matches.getColor(abstractMatch.getStatus()));
    builder.setDescription(
        locale.get("match.description", Maps.singleton("id", abstractMatch.getId())));
    if (thumbnail != null && !thumbnail.isEmpty()) {
      builder.setThumbnail(thumbnail);
    }
    Matches.appendDetails(abstractMatch, locale, builder);
    Matches.appendTeams(abstractMatch, builder);
    return new EmbedQuery(builder);
  }

  public static void appendTeams(
      @NonNull AbstractMatch abstractMatch, @NonNull EmbedBuilder builder) {
    for (MatchTeam matchTeam : abstractMatch.getTeams()) {
      builder.addField(
          Matches.getTitle(matchTeam, abstractMatch),
          Lots.pretty(matchTeam.getMemberSingles(), "[]"),
          false);
    }
  }

  public static String getTitle(@NonNull MatchTeam team, @NonNull AbstractMatch abstractMatch) {
    if (abstractMatch.getWinners() == null) {
      return team.getName() + " (+0)";
    }
    if (team.equals(abstractMatch.getWinners())) {
      return team.getName() + " (+" + abstractMatch.getInt(null, "winners-difference", 0) + ")";
    } else {
      return team.getName() + " (-" + abstractMatch.getInt(null, "losers-difference", 0) + ")";
    }
  }

  public static void appendDetails(
      @NonNull AbstractMatch abstractMatch,
      @NonNull LocaleFile locale,
      @NonNull EmbedBuilder builder) {
    abstractMatch
        .getInformation("global")
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
