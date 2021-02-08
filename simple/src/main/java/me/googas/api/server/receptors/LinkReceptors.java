package me.googas.api.server.receptors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import lombok.NonNull;
import me.googas.annotations.Nullable;
import me.googas.api.Requests;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.api.loader.LinksLoader;
import me.googas.api.permissions.AbstractPermission;
import me.googas.api.permissions.PermissionStack;
import me.googas.api.user.UserData;
import me.googas.api.utility.SortedStats;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;

public class LinkReceptors {

  @NonNull private final LinksLoader loader;

  public LinkReceptors(@NonNull LinksLoader loader) {
    this.loader = loader;
  }

  @Nullable
  private UserData getUser(@NonNull String id) {
    return this.loader.getLoader().getUsers().getUserData(id);
  }

  @Receptor(Requests.Links.USER_LINKS)
  public Collection<Linkable> getLinks(@ParamName("id") String id) {
    UserData user = this.getUser(id);
    if (user == null) return new ArrayList<>();
    return this.loader.getLinks(user);
  }

  @Receptor(Requests.Links.USER_TYPE)
  public Linkable getLink(@ParamName("id") String id, @ParamName("type") LinkableType type) {
    UserData user = this.getUser(id);
    if (user == null) return null;
    return this.loader.getLink(user, type);
  }

  @Receptor(Requests.Links.LINKS_SIZE)
  public long countLinks(@ParamName("types") LinkableType[] types) {
    return this.loader.countLinks(types);
  }

  @Receptor(Requests.Links.LINKS)
  public Collection<LinkableInfo> getLinks(
      @ParamName("page") int page,
      @ParamName("size") int size,
      @ParamName("types") LinkableType[] types) {
    return this.loader.getLinks(page, size, types);
  }

  @Receptor(Requests.Links.LINK)
  public Linkable getLink(
      @ParamName("type") LinkableType type,
      @ParamName("identification") Map<String, Object> identification,
      @ParamName("recognition") Map<String, Object> recognition) {
    return this.loader.getLink(type, identification, recognition);
  }

  @Receptor(Requests.Links.IDENTIFICATION)
  public Linkable getLink(
      @ParamName("type") LinkableType type,
      @ParamName("identification") Map<String, Object> identification) {
    return this.loader.getLink(type, identification);
  }

  @Receptor(Requests.Links.RECOGNITION)
  public Linkable getLinkByRecognition(
      @ParamName("type") LinkableType type,
      @ParamName("recognition") Map<String, Object> recognition) {
    return this.loader.getLinkByRecognition(type, recognition);
  }

  // TODO Add support to leaderboard

  @Receptor(Requests.Links.CREATE)
  public Linkable create(
      @ParamName("type") LinkableType type,
      @ParamName("identification") Map<String, Object> identification,
      @ParamName("recognition") Map<String, Object> recognition,
      @ParamName("accounts") Map<String, Double> accounts,
      @ParamName("permissions") Set<PermissionStack> permissions,
      @ParamName("information") Map<String, Map<String, Object>> information,
      @ParamName("stats") Map<String, Map<String, Double>> stats) {
    return new Linkable(
            type, identification, recognition, accounts, permissions, information, stats, null)
        .cache();
  }

  @Receptor(Requests.Links.IS_LINKED)
  public boolean isLinked(@ParamName("link") LinkableInfo info) {
    Linkable link = info.getLink();
    if (link == null) return false;
    return link.getLinkedUser() != null;
  }

  @Receptor(Requests.Links.LINK_LINK)
  public boolean link(@ParamName("link") LinkableInfo info, @ParamName("id") String id) {
    Linkable link = info.getLink();
    if (link == null) return false;
    link.setLinkedUser(this.loader.getLoader().getUsers().getUserData(id));
    return true;
  }

  @Receptor(Requests.Links.EXISTS)
  public boolean exists(@ParamName("link") LinkableInfo info) {
    return info.getLink() != null;
  }

  @Receptor(Requests.Links.SET_RECOGNITION)
  public boolean setRecognition(
      @ParamName("link") LinkableInfo link,
      @ParamName("key") String key,
      @ParamName("value") Object value) {
    Linkable linkable = link.getLink();
    if (linkable == null) return false;
    linkable.getRecognition().put(key, value);
    return true;
  }

  @Receptor(Requests.Links.PREFERENCE)
  public boolean preference(
      @ParamName("link") LinkableInfo info,
      @ParamName("key") String key,
      @ParamName("value") Object value) {
    Linkable link = info.getLink();
    if (link == null) return false;
    link.set(null, key, value);
    return true;
  }

  @Receptor(Requests.Links.REMOVE_PREFERENCE)
  public boolean removePreference(
      @ParamName("link") LinkableInfo info, @ParamName("key") String key) {
    Linkable link = info.getLink();
    if (link == null) return false;
    link.set(null, key, null);
    return true;
  }

  @Receptor(Requests.Links.SET_PREFERENCE)
  public boolean setPreference(
      @ParamName("link") LinkableInfo link,
      @ParamName("context") String context,
      @ParamName("key") String key,
      @ParamName("value") String value) {
    Linkable linkable = link.getLink();
    if (linkable == null) return false;
    linkable.set(context, key, value);
    return true;
  }

  @Receptor(Requests.Links.PERMISSIONS)
  public PermissionStack permissions(
      @ParamName("link") LinkableInfo info,
      @ParamName("context") String context,
      @ParamName("global") boolean global) {
    Linkable link = info.getLink();
    if (link != null) {
      PermissionStack stack = link.getPermissions(context);
      PermissionStack newStack = new PermissionStack(context, new ArrayList<>());
      newStack.addAll(stack);
      if (global) {
        newStack.addAll(link.getPermissions("global"));
      }
      return newStack;
    }
    return new PermissionStack(context, new ArrayList<>());
  }

  @Receptor(Requests.Links.PERMISSION)
  public boolean permission(
      @ParamName("link") LinkableInfo info,
      @ParamName("context") String context,
      @ParamName("abstractPermission") AbstractPermission abstractPermission) {
    Linkable link = info.getLink();
    if (link == null) return false;
    return link.addPermission(
        context,
        abstractPermission.getNode(),
        abstractPermission.isEnabled(),
        abstractPermission.getExpires());
  }

  @Receptor(Requests.Links.REMOVE_PERMISSION)
  public boolean removePermission(
      @ParamName("link") LinkableInfo info,
      @ParamName("context") String context,
      @ParamName("permission") String permission) {
    Linkable link = info.getLink();
    if (link == null) return false;
    return link.removePermission(context, permission);
  }

  @Receptor(Requests.Links.STATS)
  public SortedStats stats(@ParamName("link") LinkableInfo info) {
    Linkable link = info.getLink();
    return link == null ? new SortedStats() : link.getOrganized(null);
  }

  @Receptor(Requests.Links.SAVE_STATS)
  public boolean saveStats(
      @ParamName("link") LinkableInfo link, @ParamName("stats") Map<String, Number> stats) {
    Linkable data = link.getLink();
    if (data == null) return false;
    stats.forEach((key, value) -> data.increaseStat("none", key, value.floatValue()));
    return true;
  }

  @Receptor(Requests.Links.RESET_STATS)
  public boolean resetStats(@ParamName("link") LinkableInfo info) {
    Linkable link = info.getLink();
    if (link == null) return false;
    link.reset(false);
    return true;
  }

  @Receptor(Requests.Links.WITHDRAW)
  public boolean withdraw(
      @ParamName("link") LinkableInfo link,
      @ParamName("context") String context,
      @ParamName("amount") Number amount) {
    Linkable linkable = link.getLink();
    if (linkable == null || !linkable.has(context, amount.doubleValue())) return false;
    return linkable.withdraw(context, amount.doubleValue());
  }

  @Receptor(Requests.Links.DEPOSIT)
  public boolean deposit(
      @ParamName("link") LinkableInfo link,
      @ParamName("context") String context,
      @ParamName("amount") Number amount) {
    Linkable linkable = link.getLink();
    if (linkable == null) return false;
    return linkable.deposit(context, amount.doubleValue());
  }
}
