package me.googas.bot.api.types;

import com.starfishst.jda.utils.embeds.EmbedQuery;
import java.util.Collection;
import java.util.Set;
import lombok.NonNull;
import me.googas.api.discord.GuildData;
import me.googas.api.lang.LocaleFile;
import me.googas.api.matches.Ladder;
import me.googas.api.matches.Match;
import me.googas.api.matches.Team;
import me.googas.bot.core.Guido;
import me.googas.commons.Lots;
import me.googas.commons.Strings;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.EmbedBuilder;

/** An extension of match */
public interface BotMatch extends Match, BotCatchable {

  /** The keys to ignore from the details of the match when making the information */
  Set<String> toIgnore = Lots.set("thumbnail", "guild", "type");

  /**
   * Get the information from a match
   *
   * @param locale the locale that will read the information
   * @return the information of the match as a embed query
   */
  @NonNull
  default EmbedQuery getInformation(@NonNull LocaleFile locale) {
    EmbedBuilder builder = new EmbedBuilder();
    String thumbnail = this.getDetails().get("thumbnail", String.class);
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
  default GuildData getGuild() {
    return Guido.getDataLoader().getGuildData(this.getGuildId());
  }

  @Override
  default Ladder getLadder() {
    String ladderName = this.getDetails().get("ladder", String.class);
    BotGuild guild = Guido.getDataLoader().getGuildData(this.getGuildId());
    if (ladderName == null || guild == null) return null;
    return guild.getLadder(ladderName);
  }
}
