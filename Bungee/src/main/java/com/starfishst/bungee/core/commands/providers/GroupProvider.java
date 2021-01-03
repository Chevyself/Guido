package com.starfishst.bungee.core.commands.providers;

import com.starfishst.bungee.api.Guido;
import com.starfishst.bungee.context.CommandContext;
import com.starfishst.bungee.core.listeners.GroupListener;
import com.starfishst.bungee.core.utility.Chat;
import com.starfishst.bungee.providers.type.BungeeArgumentProvider;
import com.starfishst.core.exceptions.ArgumentProviderException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.NonNull;
import me.googas.api.permissions.Group;
import me.googas.commons.maps.Maps;

/** Provides groups in commands */
public class GroupProvider implements BungeeArgumentProvider<Group> {
  @Override
  public @NonNull Class<Group> getClazz() {
    return Group.class;
  }

  @NonNull
  @Override
  public Group fromString(@NonNull String s, @NonNull CommandContext commandContext)
      throws ArgumentProviderException {
    Collection<Group> groups = Guido.getListener(GroupListener.class).getGroups();
    for (Group group : groups) {
      if (group.getName().equalsIgnoreCase(s) || group.getId().equalsIgnoreCase(s)) {
        return group;
      }
    }
    throw Chat.exception("invalid.group", Maps.singleton("string", s), commandContext);
  }

  @Override
  public @NonNull List<String> getSuggestions(CommandContext commandContext) {
    Collection<Group> groups = Guido.getListener(GroupListener.class).getGroups();
    List<String> names = new ArrayList<>();
    for (Group group : groups) {
      names.add(group.getName());
    }
    return names;
  }
}
