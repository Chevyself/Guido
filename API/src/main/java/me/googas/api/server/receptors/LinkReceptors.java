package me.googas.api.server.receptors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.annotations.Nullable;
import me.googas.api.ValuesMap;
import me.googas.api.client.data.permissions.SimplePermissionStack;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.api.loader.LinksLoader;
import me.googas.api.permissions.Permission;
import me.googas.api.permissions.PermissionStack;
import me.googas.api.user.UserData;
import me.googas.api.utility.SortedStats;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;

public class LinkReceptors {

  @NonNull private final LinksLoader loader;
  @NonNull @Getter @Setter private LinkableSupplier linkableSupplier;

  public LinkReceptors(@NonNull LinksLoader loader, @NonNull LinkableSupplier linkableSupplier) {
    this.loader = loader;
    this.linkableSupplier = linkableSupplier;
  }

  @Nullable
  private UserData getUser(@NonNull String id) {
    return this.loader.getLoader().getUsers().getUserData(id);
  }

  @Receptor("link/user-links")
  public Collection<Linkable> getLinks(@ParamName("id") String id) {
    UserData user = this.getUser(id);
    if (user == null) return new ArrayList<>();
    return this.loader.getLinks(user);
  }

  @Receptor("link/user-type")
  public Linkable getLink(@ParamName("id") String id, @ParamName("type") LinkableType type) {
    UserData user = this.getUser(id);
    if (user == null) return null;
    return this.loader.getLink(user, type);
  }

  @Receptor("link/links-size")
  public long countLinks(@ParamName("types") LinkableType[] types) {
    return this.loader.countLinks(types);
  }

  @Receptor("link/links")
  public Collection<LinkableInfo> getLinks(
      @ParamName("page") int page,
      @ParamName("size") int size,
      @ParamName("types") LinkableType[] types) {
    return this.loader.getLinks(page, size, types);
  }

  @Receptor("link")
  public Linkable getLink(
      @ParamName("type") LinkableType type,
      @ParamName("identification") ValuesMap identification,
      @ParamName("recognition") ValuesMap recognition) {
    return this.loader.getLink(type, identification, recognition);
  }

  @Receptor("link/identification")
  public Linkable getLink(
      @ParamName("type") LinkableType type, @ParamName("identification") ValuesMap identification) {
    return this.loader.getLink(type, identification);
  }

  @Receptor("link/recognition")
  public Linkable getLinkByRecognition(
      @ParamName("type") LinkableType type, @ParamName("recognition") ValuesMap recognition) {
    return this.loader.getLinkByRecognition(type, recognition);
  }

  // TODO Add support to leaderboard

  @Receptor("link/create")
  public Linkable create(
      @ParamName("type") LinkableType type,
      @ParamName("recognition") ValuesMap recognition,
      @ParamName("identification") ValuesMap identification,
      @ParamName("preferences") ValuesMap preferences,
      @ParamName("stats") Map<String, Float> stats,
      @ParamName("permissions") Set<PermissionStack> permissions) {
    return this.linkableSupplier.create(
        type, recognition, identification, preferences, stats, permissions);
  }

  @Receptor("link/is-linked")
  public boolean isLinked(@ParamName("link") LinkableInfo info) {
    Linkable link = info.getLink();
    if (link == null) return false;
    return link.getLinkedUser() != null;
  }

  @Receptor("link/link")
  public boolean link(@ParamName("link") LinkableInfo info, @ParamName("id") String id) {
    Linkable link = info.getLink();
    if (link == null) return false;
    link.setLinkedUser(this.loader.getLoader().getUsers().getUserData(id));
    return true;
  }

  @Receptor("link/exists")
  public boolean exists(@ParamName("link") LinkableInfo info) {
    return info.getLink() != null;
  }

  @Receptor("link/set-recognition")
  public boolean setRecognition(
      @ParamName("link") LinkableInfo link,
      @ParamName("key") String key,
      @ParamName("value") Object value) {
    Linkable linkable = link.getLink();
    if (linkable == null) return false;
    linkable.getRecognition().put(key, value);
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

  @Receptor("link/permissions")
  public PermissionStack permissions(
      @ParamName("link") LinkableInfo info,
      @ParamName("context") String context,
      @ParamName("global") boolean global) {
    Linkable link = info.getLink();
    if (link != null) {
      PermissionStack stack = link.getPermissions(context);
      PermissionStack newStack = new SimplePermissionStack(context, new HashSet<>());
      if (stack != null) {
        newStack.addAll(stack);
      }
      if (global) {
        newStack.addAll(link.getPermissions("global"));
      }
      return newStack;
    }
    return new SimplePermissionStack(context, new HashSet<>());
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
      @ParamName("permission") String permission) {
    Linkable link = info.getLink();
    if (link == null) return false;
    return link.removePermission(context, permission);
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

  public interface LinkableSupplier {
    @NonNull
    Linkable create(
        @NonNull LinkableType type,
        @NonNull ValuesMap recognition,
        @NonNull ValuesMap identification,
        @NonNull ValuesMap preferences,
        @NonNull Map<String, Float> stats,
        @NonNull Set<PermissionStack> permissions);
  }
}
