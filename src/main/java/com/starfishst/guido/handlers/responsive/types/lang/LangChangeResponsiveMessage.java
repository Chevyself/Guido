package com.starfishst.guido.handlers.responsive.types.lang;

import com.starfishst.commands.utils.responsive.ReactionResponse;
import com.starfishst.core.utils.cache.Catchable;
import com.starfishst.core.utils.time.Time;
import com.starfishst.guido.Guido;
import com.starfishst.guido.api.data.UserData;
import com.starfishst.guido.api.events.responsive.ResponsiveMessageUnloadedEvent;
import com.starfishst.guido.handlers.responsive.types.GuidoResponsiveMessage;
import com.starfishst.guido.lang.GuidoLocaleFile;
import java.util.HashSet;
import java.util.Set;
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
    this.channelId = message.getIdLong();
    UserData userData = Guido.getDataLoader().getUserData(toChange.getIdLong());
    for (GuidoLocaleFile file : Guido.getLanguageHandler().getFiles()) {
      if (!file.getLang().equalsIgnoreCase(userData.getLang())) {
        this.addReactionResponse(
            new LangChangeReactionResponse(toChange.getIdLong(), file.getUnicode()), message);
      }
    }
  }

  @Override
  public void onSecondsPassed() {}

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
    return id;
  }

  @Override
  public @NotNull Set<ReactionResponse> getReactions() {
    return this.reactions;
  }
}
