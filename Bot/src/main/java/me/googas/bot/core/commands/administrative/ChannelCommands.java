package me.googas.bot.core.commands.administrative;

import com.starfishst.core.annotations.Parent;
import com.starfishst.core.annotations.Required;
import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.result.Result;
import java.util.Map;
import me.googas.api.lang.LocaleFile;
import me.googas.bot.api.types.discord.BotGuild;
import me.googas.commons.Strings;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

/** Commands for setting channels */
public class ChannelCommands {

  @Parent
  @Command(aliases = "channels", description = "channels.desc", node = "guido.channels")
  public Result channels(LocaleFile locale, Guild guild, BotGuild botGuild) {
    Map<String, String> placeholders = Maps.singleton("name", guild.getName());
    Map<String, Long> channels = botGuild.getChannels();
    if (channels.isEmpty()) {
      return new Result(locale.get("channels.empty", placeholders));
    } else {
      StringBuilder builder = Strings.getBuilder();
      builder.append(locale.get("channels.title", placeholders));
      channels.forEach(
          (key, channelId) -> {
            TextChannel channel = guild.getTextChannelById(channelId);
            builder.append(
                locale.get(
                    "channels.category",
                    Maps.builder("key", key)
                        .append(
                            "name",
                            channel == null ? String.valueOf(channelId) : channel.getName())));
          });
      return new Result(builder.toString());
    }
  }

  @Command(aliases = "set", description = "channels.set.desc", node = "guido.channels.set")
  public Result set(
      LocaleFile locale,
      BotGuild guild,
      TextChannel channel,
      @Required(name = "channels.set.key", description = "channels.set.key.desc") String key) {
    guild.getChannels().put(key, channel.getIdLong());
    return new Result(
        locale.get(
            "channels.set.success", Maps.builder("key", key).append("name", channel.getName())));
  }
}
