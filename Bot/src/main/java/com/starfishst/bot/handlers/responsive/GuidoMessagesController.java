package com.starfishst.bot.handlers.responsive;

import com.starfishst.bot.Guido;
import com.starfishst.bot.api.events.responsive.ResponsiveMessageUnloadedEvent;
import com.starfishst.bot.handlers.GuidoEventHandler;
import com.starfishst.bot.handlers.responsive.types.GuidoResponsiveMessage;
import com.starfishst.jda.utils.responsive.ResponsiveMessage;
import com.starfishst.jda.utils.responsive.controller.ResponsiveMessageController;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** The controller for responsive messages */
public class GuidoMessagesController implements ResponsiveMessageController, GuidoEventHandler {

  /** The messages that this is currently controlling */
  @NotNull private final Set<ResponsiveMessage> messages = new HashSet<>();

  /**
   * Adds a message to listen to in the set
   *
   * @param message the message added in the set
   */
  public void addMessage(@NotNull ResponsiveMessage message) {
    this.messages.add(message);
  }

  /**
   * Listen for when a message is unloaded to save it
   *
   * @param event the event of a message being unloaded
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onResponsiveMessageUnload(@NotNull ResponsiveMessageUnloadedEvent event) {
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
  public @NotNull Collection<ResponsiveMessage> getResponsiveMessages(@Nullable Guild guild) {
    return this.messages;
  }
}
