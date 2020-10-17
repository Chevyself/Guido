package com.starfishst.bot.api.data;

import com.starfishst.bot.Guido;
import com.starfishst.bot.handlers.data.GuidoPermission;
import com.starfishst.guido.api.data.lang.LocaleFile;
import com.starfishst.guido.api.data.links.LinkedData;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/** An extension for linked data for bot use */
public interface BotLinkedData extends LinkedData<GuidoPermission> {

  /**
   * Get the linked data as a readable string
   *
   * @param locale the locale that needs to read it
   * @return the readable string
   */
  @NotNull
  default String getReadable(@NotNull LocaleFile locale) {
    JDA jda = Guido.getConnection().validatedJda();
    switch (this.getType()) {
      case DISCORD_GUILD:
        User guildUser =
            jda.getUserById(this.getIdentification().getValueOr("id", Long.class, -1L));
        Guild guild =
            jda.getGuildById(this.getIdentification().getValueOr("guild", Long.class, -1L));
        if (guildUser != null && guild != null) {
          return locale.get(
              "link.discord-guild",
              Maps.builder("mention", guildUser.getAsMention()).append("guild", guild.getName()));
        } else {
          return locale.get("link.invalid");
        }
      case DISCORD:
        User user = jda.getUserById(this.getIdentification().getValueOr("id", Long.class, -1L));
        if (user != null) {
          return locale.get("link.discord", Maps.builder("mention", user.getAsMention()));
        } else {
          return locale.get("link.invalid");
        }
      case MINECRAFT:
        String nickname = this.getIdentification().getValue("nickname", String.class);
        if (nickname != null) {
          return locale.get("link.minecraft", Maps.singleton("nickname", nickname));
        } else {
          return locale.get("link.invalid");
        }
      default:
        throw new IllegalArgumentException(this.getType() + " is not a valid type");
    }
  }

  @Override
  @Nullable
  BotUser getLinkedUser();
}
