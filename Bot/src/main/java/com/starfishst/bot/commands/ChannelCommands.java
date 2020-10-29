package com.starfishst.bot.commands;

import com.starfishst.bot.api.data.BotGuild;
import com.starfishst.core.annotations.Parent;
import com.starfishst.core.annotations.Required;
import me.googas.api.lang.LocaleFile;
import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.annotations.Perm;
import com.starfishst.jda.result.Result;
import java.util.HashMap;
import java.util.Map;
import me.googas.commons.Strings;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

/** Commands for setting channels */
public class ChannelCommands {

  /**
   * See all the channels from a guild
   *
   * @param locale the locale of the sender of the message
   * @param guild the actual discord guild
   * @param botGuild the data of the guild
   * @return the channels in the guild
   */
  @Parent
  @Command(
      aliases = "channels",
      description = "channels.desc",
      permission = @Perm(node = "guido.channels"))
  public Result categories(LocaleFile locale, Guild guild, BotGuild botGuild) {
    HashMap<String, String> placeholders = Maps.singleton("name", guild.getName());
    Map<String, Long> categories = botGuild.getChannels();
    if (categories.isEmpty()) {
      return new Result(locale.get("channels.empty", placeholders));
    } else {
      StringBuilder builder = Strings.getBuilder();
      builder.append(locale.get("channels.title", placeholders));
      categories.forEach(
          (key, categoryId) -> {
            Category category = guild.getCategoryById(categoryId);
            builder.append(
                locale.get(
                    "channels.category",
                    Maps.builder("key", key)
                        .append(
                            "name",
                            category == null ? String.valueOf(categoryId) : category.getName())));
          });
      return new Result(builder.toString());
    }
  }

  /**
   * Set the channels in the guild
   *
   * @param locale the locale of the command sender
   * @param guild the guild data
   * @param channel the channel to set
   * @param key the key of the channel to set
   * @return whether the channel was set or unset
   */
  @Command(
      aliases = "set",
      description = "channels.set.desc",
      permission = @Perm(node = "guido.channels.set"))
  public Result set(
      LocaleFile locale,
      BotGuild guild,
      TextChannel channel,
      @Required(name = "channels.set.key", description = "channels.set.key.desc") String key) {
    guild.getCategories().put(key, channel.getIdLong());
    return new Result(
        locale.get(
            "channels.set.success", Maps.builder("key", key).append("name", channel.getName())));
  }
}
