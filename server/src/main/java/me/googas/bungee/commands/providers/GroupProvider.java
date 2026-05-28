package me.googas.bungee.commands.providers;

import com.github.chevyself.starbox.bungee.context.CommandContext;
import com.github.chevyself.starbox.bungee.providers.type.BungeeArgumentProvider;
import com.github.chevyself.starbox.exceptions.ArgumentProviderException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.NonNull;
import me.googas.api.API;
import me.googas.api.permissions.Group;
import me.googas.api.utility.Maps;
import me.googas.bungee.utility.Chat;

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
    Group group = API.getLoader().getGroups().getGroupByBane(s);
    if (group != null) {
      return group;
    }
    throw Chat.exception("invalid.group", Maps.singleton("string", s), commandContext);
  }

  @Override
  public @NonNull List<String> getSuggestions(
      @NonNull String s, @NonNull CommandContext commandContext) {
    Collection<Group> groups = API.getLoader().getGroups().getGroups();
    List<String> names = new ArrayList<>();
    for (Group group : groups) {
      if (group.getName().toLowerCase().startsWith(s.toLowerCase())) {
        names.add(group.getName());
      }
    }
    return names;
  }
}
