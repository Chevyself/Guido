package me.googas.api.links;

import lombok.NonNull;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;

/** This interface represents a linkable Minecraft account */
public interface DiscordLinkable extends Linkable {

  default User getUser(@NonNull JDA jda) {
    return jda.getUserById(this.getId());
  }

  default Member getMember(@NonNull Guild guild) {
    return guild.getMemberById(this.getId());
  }

  default Member getMember(long guild, @NonNull JDA jda) {
    Guild guildById = jda.getGuildById(guild);
    if (guildById != null) return this.getMember(guildById);
    return null;
  }

  long getId();
}
