package com.starfishst.bot.api.data;

import com.starfishst.bot.Guido;
import com.starfishst.guido.api.data.lang.LocaleFile;
import com.starfishst.guido.api.data.matches.Match;
import com.starfishst.guido.api.data.matches.Team;
import com.starfishst.jda.utils.embeds.EmbedQuery;
import java.util.Collection;
import java.util.Set;
import me.googas.commons.Lots;
import me.googas.commons.Strings;
import me.googas.commons.cache.ICatchable;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.EmbedBuilder;
import org.jetbrains.annotations.NotNull;

/** An extension of match */
public interface BotMatch extends Match, ICatchable {

  /** The keys to ignore from the details of the match when making the information */
  Set<String> toIgnore = Lots.set("thumbnail", "guild");

  /**
   * Get the information from a match
   *
   * @param locale the locale that will read the information
   * @return the information of the match as a embed query
   */
  @NotNull
  default EmbedQuery getInformation(@NotNull LocaleFile locale) {
    EmbedBuilder builder = new EmbedBuilder();
    String thumbnail = this.getDetails().getValue("thumbnail", String.class);
    StringBuilder stringBuilder = Strings.getBuilder();
    builder.setTitle(locale.get("match.title", Maps.singleton("id", this.getId())));
    builder.setFooter(locale.get("footer"));
    builder.setColor(Guido.getCommandManager().getManagerOptions().getSuccess());
    builder.setDescription(locale.get("match.description"));
    if (thumbnail != null) {
      builder.setThumbnail(thumbnail);
    }
    this.getDetails()
        .getMap()
        .forEach(
            (key, value) -> {
              if (!BotMatch.toIgnore.contains(key)) {
                String fieldDesc;
                if (value instanceof Collection) {
                  fieldDesc = Lots.pretty((Collection<?>) value);
                } else {
                  fieldDesc = value.toString();
                }
                builder.addField(locale.get(key), fieldDesc, true);
              }
            });
    for (Team team : this.getTeams()) {
      stringBuilder.append(
          locale.get(
              "match.team",
              Maps.builder("name", team.getName())
                  .append("members", Lots.pretty(team.getMemberSingles()))));
    }
    builder.addField("teams", stringBuilder.toString(), false);
    stringBuilder.setLength(0);
    if (this.getWinners() != null) {
      builder.addField(
          "winners",
          locale.get(
              "match.team",
              Maps.builder("name", this.getWinners().getName())
                  .append("members", Lots.pretty(this.getWinners().getMemberSingles()))),
          false);
    }
    return new EmbedQuery(builder);
  }

  @Override
  @NotNull
  BotMatch refresh();
}
