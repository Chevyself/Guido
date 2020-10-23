package com.starfishst.bot.handlers.matches;

import com.starfishst.bot.Guido;
import com.starfishst.bot.api.data.BotGuild;
import com.starfishst.bot.api.data.BotMatch;
import com.starfishst.bot.api.events.data.match.MatchStatusUpdatedEvent;
import com.starfishst.bot.handlers.GuidoEventHandler;
import com.starfishst.guido.api.data.lang.LocaleFile;
import com.starfishst.jda.utils.embeds.EmbedQuery;
import me.googas.commons.events.ListenPriority;
import me.googas.commons.events.Listener;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.entities.TextChannel;
import org.jetbrains.annotations.NotNull;

/** This handles the match-making process for ranked matches */
public class MatchMakingHandler implements GuidoEventHandler {

  /**
   * Listen to when a match ends to announce it
   *
   * @param event the event of a match updating its status
   */
  @Listener(priority = ListenPriority.HIGHEST)
  public void onMatchStatusUpdatedEvent(@NotNull MatchStatusUpdatedEvent event) {
    BotMatch match = event.getMatch();
    LocaleFile locale = Guido.getLanguageHandler().getDefault();
    Long guildId = match.getDetails().getValue("guild", Long.class);
    if (guildId != null) {
      BotGuild guildData = Guido.getDataLoader().getGuildData(guildId);
      TextChannel channel = guildData.getChannel("matches");
      EmbedQuery information = match.getInformation(locale);
      information
          .getEmbedBuilder()
          .setTitle(locale.get("match.announce.title", Maps.singleton("id", match.getId())));
      information.send(channel);
    }
  }

  @Override
  public void close() {}
}
