package com.starfishst.bot.handlers.responsive.types.lang;

import com.starfishst.bot.Guido;
import com.starfishst.bot.api.events.responsive.ResponsiveMessageUnloadedEvent;
import com.starfishst.bot.handlers.lang.GuidoLocaleFile;
import com.starfishst.bot.handlers.responsive.types.GuidoResponsiveMessage;
import me.googas.api.links.LinkedData;
import com.starfishst.jda.utils.responsive.ReactionResponse;
import java.util.HashSet;
import java.util.Set;
import me.googas.commons.cache.Catchable;
import me.googas.commons.time.Time;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;

/** A responsive message made to allow an user to change the language */
public class LangChangeResponsiveMessage extends Catchable implements GuidoResponsiveMessage {

  /** The id of the message */
  private final long id;

  /** The id of the channel where this was sent to */
  private final long channelId;

  /** The set of reactions which this allows */
  private final Set<ReactionResponse> reactions = new HashSet<>();

  /**
   * Create the lang responsive message
   *
   * @param toChange the user that has to change its language
   * @param message the message that is the responsive message in discord
   */
  public LangChangeResponsiveMessage(@NotNull User toChange, @NotNull Message message) {
    super(Time.fromString("30s"));
    this.id = message.getIdLong();
    this.channelId = message.getChannel().getIdLong();
    LinkedData userData = Guido.getDataLoader().getDiscordUserData(toChange.getIdLong());
    for (GuidoLocaleFile file : Guido.getLanguageHandler().getFiles()) {
      if (!file.getLang()
          .equalsIgnoreCase(userData.getPreferences().getValueOr("lang", String.class, "en"))) {
        this.addReactionResponse(
            new LangChangeReactionResponse(toChange.getIdLong(), file.getUnicode()), message);
      }
    }
  }

  @Override
  public void onSecondPassed() {}

  @Override
  public void onRemove() {
    new ResponsiveMessageUnloadedEvent(this).call();
  }

  @Override
  public long guildId() {
    return -1;
  }

  @Override
  public void save(@NotNull JDA jda) {
    TextChannel channel = jda.getTextChannelById(this.channelId);
    if (channel != null) {
      channel.deleteMessageById(this.id).queue();
    }
  }

  @Override
  public long getId() {
    return this.id;
  }

  @Override
  public @NotNull Set<ReactionResponse> getReactions() {
    return this.reactions;
  }
}
