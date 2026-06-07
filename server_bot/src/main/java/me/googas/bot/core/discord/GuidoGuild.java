package me.googas.bot.core.discord;

import java.util.Objects;
import java.util.Optional;
import lombok.NonNull;
import me.googas.api.matches.MinecraftTeamSelectionType;
import me.googas.api.utility.ImmutableCollection;
import me.googas.bot.DiscordRankRange;
import me.googas.bot.api.Guido;
import me.googas.bot.core.matches.ladder.PlayableLadder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;

/** This object represents the data for a guild that is using this bot */
public interface GuidoGuild {

  long getId();

  @NonNull
  ImmutableCollection<? extends PlayableLadder> getLadders();

  @NonNull
  ImmutableCollection<DiscordRankRange> getRanges();

  long getMatchesChannelId();

  void setMatchesChannelId(long idLong);

  long getMatchesCategoryId();

  void setMatchesCategoryId(long idLong);

  void addLadder(
      @NonNull String name,
      int playersPerTeam,
      int baseElo,
      int teamsPerMatch,
      double winMultiplier,
      double loseMultiplier,
      MinecraftTeamSelectionType teamSelectionType);

  void removeLadderByName(String name);

  /**
   * Get a ladder using its name
   *
   * @param name the name of the ladder
   * @return the ladder if found else null
   */
  default Optional<PlayableLadder> getLadder(@NonNull String name) {
    PlayableLadder ladder = null;
    for (PlayableLadder thisLadders : this.getLadders()) {
      if (thisLadders.getName().equalsIgnoreCase(name)) {
        ladder = thisLadders;
        break;
      }
    }
    return Optional.ofNullable(ladder);
  }

  default DiscordRankRange getRange(long id) {
    for (DiscordRankRange range : this.getRanges()) {
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
  default Guild toDiscord() {
    return Objects.requireNonNull(
        Guido.getConnection().getJda().getGuildById(this.getId()),
        "Seems like the guild with the id " + this.getId() + " no longer exists");
  }

  @NonNull
  default TextChannel getMatchesTextChannel() {
    Guild guild = toDiscord();
    TextChannel channel = guild.getTextChannelById(this.getMatchesChannelId());
    if (channel == null) {
      channel = guild.createTextChannel("Matches").complete();
      this.setMatchesChannelId(channel.getIdLong());
    }
    return channel;
  }

  @NonNull
  default Category getMatchesCategory() {
    Guild guild = toDiscord();
    Category category = guild.getCategoryById(this.getMatchesCategoryId());
    if (category == null) {
      category = guild.createCategory("Matches").complete();
      this.setMatchesCategoryId(category.getIdLong());
    }
    return category;
  }

  @NonNull
  Optional<DiscordRankRange> addRange(
      @NonNull String ladderName, @NonNull String name, int min, int max, long roleId);

  void removeRange(@NonNull DiscordRankRange range);

  long getWaitingVoiceChannelId();

  void setWaitingVoiceChannelId(long idLong);

  @NonNull
  default VoiceChannel getWaitingVoiceChannel() {
    Guild guild = toDiscord();
    VoiceChannel channel = guild.getVoiceChannelById(this.getWaitingVoiceChannelId());
    if (channel == null) {
      channel = guild.createVoiceChannel("Waiting").complete();
      this.setWaitingVoiceChannelId(channel.getIdLong());
    }
    return channel;
  }
}
