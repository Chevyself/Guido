package dev.xevy.guido.bot;

import net.dv8tion.jda.api.entities.Guild;

public class CleanUp {
  public static void main(String[] args) {
    Guild guild = GuidoTestRuntime.createRuntime().getJdaProvider().getGuild();
    guild.getVoiceChannels().forEach(channel -> channel.delete().complete());
    guild.getTextChannels().forEach(channel -> channel.delete().complete());
    guild.getCategories().forEach(category -> category.delete().complete());
  }
}
