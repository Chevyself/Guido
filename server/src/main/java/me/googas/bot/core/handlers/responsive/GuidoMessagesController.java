package me.googas.bot.core.handlers.responsive;

import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import lombok.NonNull;
import me.googas.bot.api.Guido;
import me.googas.bot.api.events.responsive.ResponsiveMessageUnloadedEvent;
import me.googas.bot.core.handlers.GuidoHandler;
import me.googas.starbox.events.ListenPriority;
import me.googas.starbox.events.Listener;
import me.googas.starbox.jda.responsive.ResponsiveMessage;
import me.googas.starbox.jda.responsive.ResponsiveMessageController;
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
  public void onDisable() {}

  @SubscribeEvent
  @Override
  public void onMessageReactionAdd(MessageReactionAddEvent event) {
    ResponsiveMessageController.super.onMessageReactionAdd(event);
  }

  @Override
  public @NonNull Optional<? extends ResponsiveMessage> getResponsiveMessage(
      Guild guild, long messageId) {
    if (guild != null) {
      ResponsiveMessage message =
          Guido.getHandlers().getDiscordLoader().getGuild(guild.getIdLong()).getMessage(messageId);
      return Optional.of(message);
    } else {
      return this.messages.stream().filter(message -> message.getId() == messageId).findFirst();
    }
  }

  @Override
  public void removeMessage(Guild guild, @NonNull ResponsiveMessage message) {}

  @Override
  public boolean acceptBots() {
    return false;
  }
}
