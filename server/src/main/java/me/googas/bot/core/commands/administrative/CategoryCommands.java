package me.googas.bot.core.commands.administrative;

import com.starfishst.commands.jda.annotations.Command;
import com.starfishst.commands.jda.result.Result;
import com.starfishst.core.annotations.Parent;
import com.starfishst.core.annotations.Required;
import java.util.Map;
import me.googas.api.lang.LocaleFile;
import me.googas.bot.core.discord.GuidoGuild;
import me.googas.commons.Strings;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

/** Commands for changing the categories in a guild */
public class CategoryCommands {

  @Parent
  @Command(aliases = "categories", description = "categories.desc", node = "guido.categories")
  public Result categories(LocaleFile locale, Guild guild, GuidoGuild botGuild) {
    Map<String, String> placeholders = Maps.singleton("name", guild.getName());
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

  @Command(aliases = "set", description = "categories.set.desc", node = "guido.categories.set")
  public Result set(
      LocaleFile locale,
      GuidoGuild guild,
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
