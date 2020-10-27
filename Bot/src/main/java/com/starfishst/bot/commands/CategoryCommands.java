package com.starfishst.bot.commands;

import com.starfishst.bot.api.data.BotGuild;
import com.starfishst.core.annotations.Parent;
import com.starfishst.core.annotations.Required;
import com.starfishst.guido.api.data.lang.LocaleFile;
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

/** Commands for changing the categories in a guild */
public class CategoryCommands {

  /**
   * See all the categories from a guild
   *
   * @param locale the locale of the sender of the message
   * @param guild the actual discord guild
   * @param botGuild the data of the guild
   * @return the categories in the guild
   */
  @Parent
  @Command(
      aliases = "categories",
      description = "categories.desc",
      permission = @Perm(node = "guido.categories"))
  public Result categories(LocaleFile locale, Guild guild, BotGuild botGuild) {
    HashMap<String, String> placeholders = Maps.singleton("name", guild.getName());
    Map<String, Long> categories = botGuild.getCategories();
    if (categories.isEmpty()) {
      return new Result(locale.get("categories.empty", placeholders));
    } else {
      StringBuilder builder = Strings.getBuilder();
      builder.append(locale.get("categories.title", placeholders));
      categories.forEach(
          (key, categoryId) -> {
            Category category = guild.getCategoryById(categoryId);
            builder.append(
                locale.get(
                    "categories.category",
                    Maps.builder("key", key)
                        .append(
                            "name",
                            category == null ? String.valueOf(categoryId) : category.getName())));
          });
      return new Result(builder.toString());
    }
  }

  /**
   * Set the categories in the guild
   *
   * @param locale the locale of the command sender
   * @param guild the guild data
   * @param channel the channel to get the category from
   * @param key the key of the category to set
   * @return whether the category was set or unset
   */
  @Command(
      aliases = "set",
      description = "categories.set.desc",
      permission = @Perm(node = "guido.categories.set"))
  public Result set(
      LocaleFile locale,
      BotGuild guild,
      TextChannel channel,
      @Required(name = "categories.set.key", description = "categories.set.key.desc") String key) {
    Category parent = channel.getParent();
    if (parent == null) {
      guild.getCategories().remove(key);
      return new Result(locale.get("categories.set.unset", Maps.singleton("key", key)));
    } else {
      guild.getCategories().put(key, parent.getIdLong());
      return new Result(
          locale.get(
              "categories.set.success", Maps.builder("key", key).append("name", parent.getName())));
    }
  }
}
