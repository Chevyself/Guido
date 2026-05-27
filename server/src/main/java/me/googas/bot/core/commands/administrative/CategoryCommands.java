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
import net.dv8tion.jda.api.entities.channel.concrete.Category;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;

/** Commands for changing the categories in a guild */
public class CategoryCommands {

  @Parent
  @GuidoJdaPermission("guido.categories")
  @Command(aliases = "categories", description = "categories.desc")
  public Result categories(LocaleFile locale, Guild guild, GuidoGuild botGuild) {
    Map<String, String> placeholders = Maps.singleton("name", guild.getName());
    Map<String, Long> categories = botGuild.getCategories();
    if (categories.isEmpty()) {
      return Result.of(locale.get("categories.empty", placeholders));
    } else {
      StringBuilder builder = new StringBuilder();
      builder.append(locale.get("categories.title", placeholders));
      categories.forEach(
          (key, categoryId) -> {
            Category category = guild.getCategoryById(categoryId);
            builder.append(
                locale.get(
                    "categories.category",
                    Maps.builder("key", key)
                        .put(
                            "name",
                            category == null ? String.valueOf(categoryId) : category.getName())));
          });
      return Result.of(builder.toString());
    }
  }

  @GuidoJdaPermission("guido.categories.set")
  @Command(aliases = "set", description = "categories.set.desc")
  public Result set(
      LocaleFile locale,
      GuidoGuild guild,
      TextChannel channel,
      @Required(name = "categories.set.key", description = "categories.set.key.desc") String key) {
    Category parent = channel.getParentCategory();
    if (parent == null) {
      guild.getCategories().remove(key);
      return Result.of(locale.get("categories.set.unset", Maps.singleton("key", key)));
    } else {
      guild.getCategories().put(key, parent.getIdLong());
      return Result.of(
          locale.get(
              "categories.set.success", Maps.builder("key", key).put("name", parent.getName())));
    }
  }
}
