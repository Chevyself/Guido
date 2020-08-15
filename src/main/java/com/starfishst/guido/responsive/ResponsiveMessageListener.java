package com.starfishst.guido.responsive;

import com.starfishst.commands.utils.responsive.ResponsiveMessage;
import com.starfishst.commands.utils.responsive.controller.ResponsiveMessageController;
import com.starfishst.guido.GuildConfiguration;
import java.util.Collection;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.SubscribeEvent;
import org.jetbrains.annotations.NotNull;

/** The responsive messages listener */
public class ResponsiveMessageListener implements ResponsiveMessageController {

  @Override
  public boolean acceptBots() {
    return false;
  }

  @SubscribeEvent
  @Override
  public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent event) {
    ResponsiveMessageController.super.onGuildMessageReactionAdd(event);
  }

  @Override
  public @NotNull Collection<ResponsiveMessage> getResponsiveMessages(@NotNull Guild guild) {
    return GuildConfiguration.getConfiguration(guild).getMessages();
  }
}
