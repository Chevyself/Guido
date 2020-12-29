package me.googas.bot.core.server.receptors.links;

import java.util.Map;
import java.util.Set;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.api.permissions.PermissionStack;
import me.googas.bot.Guido;
import me.googas.bot.core.GuidoValuesMap;
import me.googas.bot.core.links.GuidoLinkable;
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

  // @Receptor("link/recognition")
  // Each recognition in a link must be updated specifying its type

}
