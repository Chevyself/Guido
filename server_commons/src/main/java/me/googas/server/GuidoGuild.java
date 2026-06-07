package me.googas.server;

import java.util.Objects;
import java.util.Optional;
import lombok.NonNull;
import me.googas.api.matches.MinecraftTeamSelectionType;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.utility.ImmutableCollection;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

/** This object represents the data for a guild that is using this bot */
public interface GuidoGuild {

  long getId();

  @NonNull
  ImmutableCollection<? extends Ladder> getLadders();

  @NonNull
  ImmutableCollection<? extends RankRange> getRanges();

  long getMatchesChannelId();

  void setMatchesChannelId(long idLong);

  long getMatchesCategoryId();

  void setMatchesCategoryId(long idLong);

  Optional<? extends Ladder> addLadder(
      @NonNull String name,
      int playersPerTeam,
      int baseElo,
      int teamsPerMatch,
      double winMultiplier,
      double loseMultiplier,
      MinecraftTeamSelectionType teamSelectionType);

  boolean removeLadderByName(String name);

  /**
   * Get a ladder using its name
   *
   * @param name the name of the ladder
   * @return the ladder if found else null
   */
  default Optional<? extends Ladder> getLadder(@NonNull String name) {
    Ladder ladder = null;
    for (Ladder thisLadders : this.getLadders()) {
      if (thisLadders.getName().equalsIgnoreCase(name)) {
        ladder = thisLadders;
        break;
      }
    }
    return Optional.ofNullable(ladder);
  }

  default RankRange getRange(long id) {
    for (RankRange range : this.getRanges()) {
      if (range.getRoleId() == id) return range;
    }
    return null;
  }

  /**
   * Get the data as a discord guild
   *
   * @return the discord guild
   */
  @NonNull
  default Guild toDiscord(@NonNull JDA jda) {
    return Objects.requireNonNull(
        jda.getGuildById(this.getId()),
        "Seems like the guild with the id " + this.getId() + " no longer exists");
  }

  @NonNull
  default TextChannel getMatchesTextChannel(@NonNull JDA jda) {
    Guild guild = toDiscord(jda);
    TextChannel channel = guild.getTextChannelById(this.getMatchesChannelId());
    if (channel == null) {
      channel = guild.createTextChannel("Matches").complete();
      this.setMatchesChannelId(channel.getIdLong());
    }
    return channel;
  }

  @NonNull
  default Category getMatchesCategory(@NonNull JDA jda) {
    Guild guild = toDiscord(jda);
    Category category = guild.getCategoryById(this.getMatchesCategoryId());
    if (category == null) {
      category = guild.createCategory("Matches").complete();
      this.setMatchesCategoryId(category.getIdLong());
    }
    return category;
  }

  @NonNull
  Optional<? extends RankRange> addRange(
      @NonNull String ladderName, @NonNull String name, int min, int max, long roleId);

  boolean removeRangeByName(@NonNull String name);

  long getWaitingVoiceChannelId();

  void setWaitingVoiceChannelId(long idLong);

  @NonNull
  default VoiceChannel getWaitingVoiceChannel(@NonNull JDA jda) {
    Guild guild = toDiscord(jda);
    VoiceChannel channel = guild.getVoiceChannelById(this.getWaitingVoiceChannelId());
    if (channel == null) {
      channel = guild.createVoiceChannel("Waiting").complete();
      this.setWaitingVoiceChannelId(channel.getIdLong());
    }
    return channel;
  }
}
