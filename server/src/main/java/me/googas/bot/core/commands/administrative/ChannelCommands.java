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
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

/** Commands for setting channels */
public class ChannelCommands {

  @Parent
  @GuidoJdaPermission("guido.channels")
  @Command(aliases = "channels", description = "channels.desc")
  public Result channels(LocaleFile locale, Guild guild, GuidoGuild botGuild) {
    Map<String, String> placeholders = Maps.singleton("name", guild.getName());
    Map<String, Long> channels = botGuild.getChannels();
    if (channels.isEmpty()) {
      return Result.of(locale.get("channels.empty", placeholders));
    } else {
      StringBuilder builder = new StringBuilder();
      builder.append(locale.get("channels.title", placeholders));
      channels.forEach(
          (key, channelId) -> {
            TextChannel channel = guild.getTextChannelById(channelId);
            builder.append(
                locale.get(
                    "channels.category",
                    Maps.builder("key", key)
                        .put(
                            "name",
                            channel == null ? String.valueOf(channelId) : channel.getName())));
          });
      return Result.of(builder.toString());
    }
  }

  @GuidoJdaPermission("guido.channels.set")
  @Command(aliases = "set", description = "channels.set.desc")
  public Result set(
      LocaleFile locale,
      GuidoGuild guild,
      TextChannel channel,
      @Required(name = "channels.set.key", description = "channels.set.key.desc") String key) {
    guild.getChannels().put(key, channel.getIdLong());
    return Result.of(
        locale.get(
            "channels.set.success", Maps.builder("key", key).put("name", channel.getName())));
  }
}
