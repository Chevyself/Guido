package me.googas.bot.core.commands.providers;

import com.starfishst.commands.jda.context.CommandContext;
import com.starfishst.commands.jda.providers.type.JdaArgumentProvider;
import com.starfishst.core.exceptions.ArgumentProviderException;
import lombok.NonNull;
import me.googas.api.loader.GroupLoader;
import me.googas.api.permissions.Group;
import me.googas.bot.api.Guido;
import me.googas.bot.core.util.Lang;
import me.googas.commons.maps.Maps;

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
