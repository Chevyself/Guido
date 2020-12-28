package me.googas.bot.core.discord;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.ranks.RankRange;
import me.googas.bot.api.events.data.guild.BotGuildUnloadedEvent;
import me.googas.bot.api.types.discord.BotGuild;
import me.googas.bot.api.types.messages.ResponsiveMesage;
import me.googas.commons.builder.ToStringBuilder;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;

/** This object represents the data for a guild that is using this bot */
public class GuidoGuild implements BotGuild {

  /** The version of serialization for the scheme */
  @NonNull @Getter private final String version = "PRE-3";

  private final long id;
  @NonNull private final Set<Ladder> ladders;
  @NonNull private final Set<RankRange> ranges;
  @NonNull private final Map<String, Long> channels;
  @NonNull private final Map<String, Long> voiceChannels;
  @NonNull private final Map<String, Long> categories;
  @NonNull private final Set<ResponsiveMesage> messages;

  /**
   * Create the guido guild
   *
   * @param id the id of the guild
   * @param ladders the ladders of the guild
   * @param ranges the ranges of the guild
   * @param channels the channels map of the guild
   * @param voiceChannels the voice channels map of the guild
   * @param categories the categories map of the guild
   * @param messages the messages that can be used inside the guild
   */
  public GuidoGuild(
      long id,
      @NonNull Set<Ladder> ladders,
      @NonNull Set<RankRange> ranges,
      @NonNull Map<String, Long> channels,
      @NonNull Map<String, Long> voiceChannels,
      @NonNull Map<String, Long> categories,
      @NonNull Set<ResponsiveMesage> messages) {
    this.id = id;
    this.ladders = ladders;
    this.ranges = ranges;
    this.channels = channels;
    this.voiceChannels = voiceChannels;
    this.categories = categories;
    this.messages = messages;
  }

  /** @deprecated this constructor may only be used by gson */
  public GuidoGuild() {
    this(
        0,
        new HashSet<>(),
        new HashSet<>(),
        new HashMap<>(),
        new HashMap<>(),
        new HashMap<>(),
        new HashSet<>());
  }

  @Override
  public @NonNull Collection<ResponsiveMesage> getMessages() {
    return this.messages;
  }

  @Override
  public ResponsiveMesage getMessage(long id) {
    for (ResponsiveMesage message : this.messages) {
      if (message != null && message.getId() == id) return message;
    }
    return null;
  }

  @Override
  public long getId() {
    return this.id;
  }

  @NonNull
  @Override
  public Set<Ladder> getLadders() {
    return this.ladders;
  }

  @NonNull
  @Override
  public Set<RankRange> getRanges() {
    return this.ranges;
  }

  @Override
  public @NonNull Map<String, Long> getChannels() {
    return this.channels;
  }

  @Override
  public @NonNull Map<String, Long> getVoiceChannels() {
    return this.voiceChannels;
  }

  @Override
  public @NonNull Map<String, Long> getCategories() {
    return this.categories;
  }

  @Override
  public void onRemove() {
    new BotGuildUnloadedEvent(this).call();
  }

  @Override
  public @NonNull Time getToRemove() {
    return new Time(10, Unit.MINUTES);
  }

  @Override
  public @NonNull GuidoGuild cache() {
    return (GuidoGuild) BotGuild.super.cache();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("id", this.id)
        .append("ladders", this.ladders)
        .append("ranges", this.ranges)
        .append("channels", this.channels)
        .append("voiceChannels", this.voiceChannels)
        .append("categories", this.categories)
        .append("messages", this.messages)
        .build();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || this.getClass() != o.getClass()) return false;
    GuidoGuild that = (GuidoGuild) o;
    return this.id == that.id;
  }

  @Override
  public int hashCode() {
    return Objects.hash(this.id);
  }
}
