package me.googas.bot.core.discord;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import lombok.Getter;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.GuidoCatchable;
import me.googas.api.matches.ladder.GlobalLadder;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.ranks.RankRange;
import me.googas.bot.api.Guido;
import me.googas.bot.api.events.data.guild.GuidoGuildUnloadedEvent;
import me.googas.bot.api.types.messages.ResponsiveMesage;
import me.googas.commons.Validate;
import me.googas.commons.builder.ToStringBuilder;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

/** This object represents the data for a guild that is using this bot */
public class GuidoGuild implements GuidoCatchable {

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

  public @NonNull Collection<ResponsiveMesage> getMessages() {
    return this.messages;
  }

  public ResponsiveMesage getMessage(long id) {
    for (ResponsiveMesage message : this.messages) {
      if (message != null && message.getId() == id) return message;
    }
    return null;
  }

  public long getId() {
    return this.id;
  }

  @NonNull
  public Set<Ladder> getLadders() {
    return this.ladders;
  }

  @NonNull
  public Set<RankRange> getRanges() {
    return this.ranges;
  }

  public @NonNull Map<String, Long> getChannels() {
    return this.channels;
  }

  public @NonNull Map<String, Long> getVoiceChannels() {
    return this.voiceChannels;
  }

  public @NonNull Map<String, Long> getCategories() {
    return this.categories;
  }

  /**
   * Get a ladder using its name
   *
   * @param name the name of the ladder
   * @return the ladder if found else null
   */
  public Ladder getLadder(@NonNull String name) {
    if (name.equalsIgnoreCase("global")) {
      return GlobalLadder.INSTANCE;
    } else {
      for (Ladder ladder : this.getLadders()) {
        if (ladder.getName().equalsIgnoreCase(name)) {
          return ladder;
        }
      }
      return null;
    }
  }

  @Override
  public @NonNull Time getToRemove() {
    return new Time(10, Unit.MINUTES);
  }

  @Nullable
  public RankRange getRange(long id) {
    for (RankRange range : this.getRanges()) {
      Long rangeId = range.get(null, "id", Long.class);
      if (rangeId != null && rangeId == id) return range;
    }
    return null;
  }

  /**
   * Get the discord text channel for the given key
   *
   * @param key the key to getId the channel
   * @return the channel
   */
  @NonNull
  public TextChannel getTextChannel(@NonNull String key) {
    Guild guild = this.toDiscord();
    TextChannel channel = guild.getTextChannelById(this.getChannels().getOrDefault(key, -1L));
    if (channel == null) {
      channel = guild.createTextChannel(key).complete();
      this.getChannels().put(key, channel.getIdLong());
    }
    return channel;
  }

  /**
   * Get the discord text channel for the given key
   *
   * @param key the key to getId the channel
   * @return the channel
   */
  @NonNull
  public VoiceChannel getVoiceChannel(@NonNull String key) {
    Guild guild = this.toDiscord();
    VoiceChannel channel =
        guild.getVoiceChannelById(this.getVoiceChannels().getOrDefault(key, -1L));
    if (channel == null) {
      channel = guild.createVoiceChannel(key).complete();
      this.getVoiceChannels().put(key, channel.getIdLong());
    }
    return channel;
  }

  /**
   * Get the discord category for the given key
   *
   * @param key the key to getId the category
   * @return the category
   */
  @NonNull
  public Category getCategory(@NonNull String key) {
    Guild guild = this.toDiscord();
    Category category = guild.getCategoryById(this.getCategories().getOrDefault(key, -1L));
    if (category == null) {
      category = guild.createCategory(key).complete();
      this.getChannels().put(key, category.getIdLong());
    }
    return category;
  }

  /**
   * Get the key of a voice channel matching the id
   *
   * @param id the id of the channel to match
   * @return the key that contains that channel if found else null
   */
  public String getVoiceChannel(long id) {
    AtomicReference<String> atomic = new AtomicReference<>();
    this.getVoiceChannels()
        .forEach(
            (key, channel) -> {
              if (channel == id) atomic.set(key);
            });
    return atomic.get();
  }

  /**
   * Get the data as a discord guild
   *
   * @return the discord guild
   */
  @NonNull
  public Guild toDiscord() {
    return Validate.notNull(
        Guido.getConnection().validatedJda().getGuildById(this.getId()),
        "Seems like the guild with the id " + this.getId() + " no longer exists");
  }

  @Override
  public void onRemove() {
    new GuidoGuildUnloadedEvent(this).call();
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

  @Override
  public @NonNull GuidoGuild cache() {
    return (GuidoGuild) GuidoCatchable.super.cache();
  }
}
