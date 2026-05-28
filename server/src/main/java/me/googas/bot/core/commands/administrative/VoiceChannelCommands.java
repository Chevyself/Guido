package me.googas.bot.core.commands.administrative;

import com.github.chevyself.starbox.annotations.Command;
import com.github.chevyself.starbox.annotations.Parent;
import com.github.chevyself.starbox.annotations.Required;
import com.github.chevyself.starbox.result.Result;
import java.util.Map;
import me.googas.api.lang.LocaleFile;
import me.googas.api.utility.Maps;
import me.googas.bot.core.discord.GuidoGuild;
import me.googas.bungee.commands.middleware.GuidoJdaPermission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.concrete.VoiceChannel;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;

/** Set the voice channels of a g uild */
public class VoiceChannelCommands {

  /**
   * See all the channels from a guild
   *
   * @param locale the locale of the sender of the message
   * @param guild the actual discord guild
   * @param botGuild the data of the guild
   * @return the channels in the guild
   */
  @Parent
  @GuidoJdaPermission("guido.channels")
  @Command(
      aliases = {"voiceChannels", "vc"},
      description = "vc.desc")
  public Result categories(LocaleFile locale, Guild guild, GuidoGuild botGuild) {
    Map<String, String> placeholders = Maps.singleton("name", guild.getName());
    Map<String, Long> voiceChannels = botGuild.getVoiceChannels();
    if (voiceChannels.isEmpty()) {
      return Result.of(locale.get("vc.empty", placeholders));
    } else {
      StringBuilder builder = new StringBuilder();
      builder.append(locale.get("vc.title", placeholders));
      voiceChannels.forEach(
          (key, channelId) -> {
            VoiceChannel channel = guild.getVoiceChannelById(channelId);
            builder.append(
                locale.get(
                    "vc.channel",
                    Maps.builder("key", key)
                        .put(
                            "name",
                            channel == null ? String.valueOf(channelId) : channel.getName())));
          });
      return Result.of(builder.toString());
    }
  }

  @GuidoJdaPermission("guido.channels.set")
  @Command(aliases = "set", description = "vc.set.desc")
  public Result set(
      LocaleFile locale,
      GuidoGuild guild,
      Member member,
      @Required(name = "vc.set.key", description = "vc.set.key.desc") String key) {
    GuildVoiceState state = member.getVoiceState();
    if (state != null) {
      AudioChannelUnion channel = state.getChannel();
      if (channel != null) {
        guild.getVoiceChannels().put(key, channel.getIdLong());
        return Result.of(
            locale.get("vc.set.success", Maps.builder("key", key).put("name", channel.getName())));
      }
    }
    return Result.of(locale.get("vc.set.connect"));
  }
}
