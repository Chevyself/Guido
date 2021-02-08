package me.googas.bot.core.handlers.responsive.lang;

import com.starfishst.commands.jda.utils.responsive.ReactionResponse;
import java.util.HashSet;
import java.util.Set;
import lombok.NonNull;
import me.googas.api.GuidoCatchable;
import me.googas.api.links.ref.DiscordLinkable;
import me.googas.bot.api.Guido;
import me.googas.bot.core.handlers.responsive.GuidoResponsiveMessage;
import me.googas.bot.core.lang.GuidoLocaleFile;
import me.googas.bot.core.util.Discord;
import me.googas.commons.time.Time;
import me.googas.commons.time.Unit;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

/** A responsive message made to allow an user to change the language */
public class LangChangeResponsiveMessage implements GuidoResponsiveMessage, GuidoCatchable {

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
  public LangChangeResponsiveMessage(@NonNull User toChange, @NonNull Message message) {
    this.id = message.getIdLong();
    this.channelId = message.getChannel().getIdLong();
    DiscordLinkable userData = Discord.getUser(toChange);
    for (GuidoLocaleFile file : Guido.getHandlers().getLanguageHandler().getFiles()) {
      if (!file.getLang().equalsIgnoreCase(userData.getString(null, "lang", "en"))) {
        this.addReactionResponse(
            new LangChangeReactionResponse(toChange.getIdLong(), file.getUnicode()), message);
      }
    }
  }

  @Override
  public void onRemove() throws Throwable {}

  @Override
  public @NonNull Time getToRemove() {
    return new Time(30, Unit.SECONDS);
  }

  @Override
  public long guildId() {
    return -1;
  }

  @Override
  public void save(@NonNull JDA jda) {
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
  public @NonNull Set<ReactionResponse> getReactions() {
    return this.reactions;
  }
}
