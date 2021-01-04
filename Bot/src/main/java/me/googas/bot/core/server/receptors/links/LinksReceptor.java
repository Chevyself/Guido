package me.googas.bot.core.server.receptors.links;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import me.googas.api.SortedStats;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.api.permissions.Permission;
import me.googas.api.permissions.PermissionStack;
import me.googas.bot.Guido;
import me.googas.bot.core.GuidoValuesMap;
import me.googas.bot.core.links.GuidoLinkable;
import me.googas.bot.core.permissions.GuidoPermissionStack;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;

public class LinksReceptor {

  @Receptor("link/create")
  public Linkable create(
      @ParamName("type") LinkableType type,
      @ParamName("recognition") GuidoValuesMap recognition,
      @ParamName("identification") GuidoValuesMap identification,
      @ParamName("preferences") GuidoValuesMap preferences,
      @ParamName("stats") Map<String, Float> stats,
      @ParamName("permissions") Set<PermissionStack> permissions) {
    return new GuidoLinkable(
            type, recognition, null, identification, preferences, stats, permissions)
        .cache();
  }

  @Receptor("link/link")
  public boolean link(@ParamName("link") LinkableInfo info, @ParamName("id") String id) {
    Linkable link = info.getLink();
    if (link == null) return false;
    link.setLinkedUser(Guido.getDataLoader().getUserData(id));
    return true;
  }

  @Receptor("link/exists")
  public boolean exists(@ParamName("link") LinkableInfo info) {
    return info.getLink() != null;
  }

  @Receptor("link/preference")
  public boolean preference(
      @ParamName("link") LinkableInfo info,
      @ParamName("key") String key,
      @ParamName("value") Object value) {
    Linkable link = info.getLink();
    if (link == null) return false;
    link.getPreferences().put(key, value);
    return true;
  }

  @Receptor("link/remove-preference")
  public boolean removePreference(
      @ParamName("link") LinkableInfo info, @ParamName("key") String key) {
    Linkable link = info.getLink();
    if (link == null) return false;
    link.getPreferences().remove(key);
    return true;
  }

  @Receptor("link/is-linked")
  public boolean isLinked(@ParamName("link") LinkableInfo info) {
    Linkable link = info.getLink();
    if (link == null) return false;
    return link.getLinkedUser() != null;
  }

  @Receptor("link/permissions")
  public PermissionStack permissions(
      @ParamName("link") LinkableInfo info,
      @ParamName("context") String context,
      @ParamName("global") boolean global) {
    Linkable link = info.getLink();
    if (link != null) {
      PermissionStack stack = link.getPermissions(context);
      if (stack != null) {
        GuidoPermissionStack newStack = new GuidoPermissionStack(stack);
        if (global) {
          newStack.addAll(link.getPermissions("global"));
        }
        return newStack;
      }
    }
    return new GuidoPermissionStack("context", new HashSet<>());
  }

  @Receptor("link/permission")
  public boolean permission(
      @ParamName("link") LinkableInfo info,
      @ParamName("context") String context,
      @ParamName("permission") Permission permission) {
    Linkable link = info.getLink();
    if (link == null) return false;
    return link.addPermission(
        context, permission.getNode(), permission.isEnabled(), permission.expires());
  }

  @Receptor("link/remove-permission")
  public boolean removePermission(
      @ParamName("link") LinkableInfo info,
      @ParamName("context") String context,
      @ParamName("permission") Permission permission) {
    Linkable link = info.getLink();
    if (link == null) return false;
    return link.removePermission(context, permission.getNode());
  }

  @Receptor("link/stats")
  public SortedStats stats(@ParamName("link") LinkableInfo info) {
    Linkable link = info.getLink();
    return link == null ? new SortedStats() : link.getOrganized(null);
  }

  @Receptor("link/reset-stats")
  public boolean resetStats(@ParamName("link") LinkableInfo info) {
    Linkable link = info.getLink();
    if (link == null) return false;
    link.reset(false);
    return true;
  }

}
