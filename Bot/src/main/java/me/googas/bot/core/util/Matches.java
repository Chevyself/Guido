package me.googas.bot.core.util;

import com.starfishst.jda.utils.embeds.EmbedQuery;
import lombok.NonNull;
import me.googas.api.lang.LocaleFile;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchTeam;
import me.googas.bot.Guido;
import me.googas.bot.api.types.discord.BotGuild;
import me.googas.commons.Lots;
import me.googas.commons.Strings;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.EmbedBuilder;

import java.util.Collection;
import java.util.Set;

public class Matches {

  /** The keys to ignore from the details of the match when making the information */
  public static Set<String> toIgnore = Lots.set("thumbnail", "guild", "type");

  /**
   * Get the guild in which a match was played
   *
   * @param match the match to get the guild from
   * @return the guild of the match or null if not found
   */
  public static BotGuild getGuild(@NonNull Match match) {
    return Guido.getDataLoader().getGuildData(match.getGuildId());
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
    StringBuilder stringBuilder = Strings.getBuilder();
    builder.setTitle(locale.get("match.title", Maps.singleton("id", match.getId())));
    builder.setFooter(locale.get("footer"));
    builder.setColor(Guido.getCommandManager().getManagerOptions().getSuccess());
    builder.setDescription(locale.get("match.description"));
    if (thumbnail != null) {
      builder.setThumbnail(thumbnail);
    }
    match.getDetails()
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
    for (MatchTeam matchTeam : match.getTeams()) {
      stringBuilder.append(
              locale.get(
                      "match.team",
                      Maps.builder("name", matchTeam.getName())
                              .append("members", Lots.pretty(matchTeam.getMemberSingles()))));
    }
    builder.addField("teams", stringBuilder.toString(), false);
    stringBuilder.setLength(0);
    if (match.getWinners() != null) {
      builder.addField(
              "winners",
              locale.get(
                      "match.team",
                      Maps.builder("name", match.getWinners().getName())
                              .append("members", Lots.pretty(match.getWinners().getMemberSingles()))),
              false);
    }
    return new EmbedQuery(builder);
  }
}
