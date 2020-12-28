package me.googas.bot.core.handlers.responsive;

import com.starfishst.jda.utils.responsive.ResponsiveMessage;
import com.starfishst.jda.utils.responsive.controller.ResponsiveMessageController;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import lombok.NonNull;
import me.googas.bot.Guido;
import me.googas.bot.api.events.responsive.ResponsiveMessageUnloadedEvent;
import me.googas.bot.core.handlers.GuidoHandler;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;

/** The controller for responsive messages */
public class GuidoMessagesController implements ResponsiveMessageController, GuidoHandler {

  /** The messages that this is currently controlling */
  @NonNull private final Set<ResponsiveMessage> messages = new HashSet<>();

  /**
   * Adds a message to listen to in the set
   *
   * @param message the message added in the set
   */
  public void addMessage(@NonNull ResponsiveMessage message) {
    this.messages.add(message);
  }

  /**
   * Listen for when a message is unloaded to save it
   *
   * @param event the event of a message being unloaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onResponsiveMessageUnload(@NonNull ResponsiveMessageUnloadedEvent event) {
    JDA jda = Guido.getConnection().getJda();
    if (jda != null && event.getMessage() instanceof GuidoResponsiveMessage) {
      ((GuidoResponsiveMessage) event.getMessage()).save(jda);
    }
  }

  @Override
  public void close() {}

  @SubscribeEvent
  @Override
  public void onMessageReactionAdd(MessageReactionAddEvent event) {
    ResponsiveMessageController.super.onMessageReactionAdd(event);
  }

  @Override
  public boolean acceptBots() {
    return false;
  }

  @Override
  public @NonNull Collection<ResponsiveMessage> getResponsiveMessages(Guild guild) {
    if (guild != null) {
      return new HashSet<>(
          Guido.getDataLoader().getGuildDataOrCreate(guild.getIdLong()).getMessages());
    } else {
      return this.messages;
    }
  }
}
