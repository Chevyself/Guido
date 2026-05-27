package me.googas.bot.core.commands.providers;

import com.github.chevyself.starbox.exceptions.ArgumentProviderException;
import com.github.chevyself.starbox.jda.context.CommandContext;
import com.github.chevyself.starbox.jda.providers.type.JdaArgumentProvider;
import lombok.NonNull;
import me.googas.api.loader.GroupLoader;
import me.googas.api.permissions.Group;
import me.googas.api.utility.Maps;
import me.googas.bot.api.Guido;
import me.googas.bot.core.util.Lang;

public class GroupProvider implements JdaArgumentProvider<Group> {
  @Override
  public @NonNull Class<Group> getClazz() {
    return Group.class;
  }

  @Override
  public @NonNull Group fromString(@NonNull String s, @NonNull CommandContext context)
      throws ArgumentProviderException {
    GroupLoader groups = Guido.getHandlers().getLoader().getGroups();
    Group group = groups.getGroupByBane(s);
    if (group == null) group = groups.getGroup(s);
    if (group != null) return group;
    throw Lang.getException("invalid.group", Maps.singleton("string", s), context);
  }
}
