package me.googas.api;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import lombok.NonNull;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.MatchTeam;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.permissions.Group;
import me.googas.api.permissions.GroupInfo;
import me.googas.api.permissions.Permission;
import me.googas.api.permissions.PermissionStack;
import me.googas.api.punishment.Punishment;
import me.googas.api.punishment.PunishmentStatus;
import me.googas.api.punishment.PunishmentType;
import me.googas.api.utility.SortedStats;
import me.googas.messaging.RequestBuilder;

/** Static utilities for requests */
public class Requests {

  @NonNull
  public static <T> Consumer<Optional<T>> ifPresentElse(
      @NonNull Consumer<T> ifPresent, @NonNull Runnable elseRun) {
    return optional -> {
      if (optional.isPresent()) {
        ifPresent.accept(optional.get());
      } else {
        elseRun.run();
      }
    };
  }

  public static class Groups {
    @NonNull public static String PREFIX = "group/";

    @NonNull
    public static RequestBuilder<Group> getGroup(@NonNull String id) {
      return new RequestBuilder<>(Group.class, "group").put("id", id);
    }

    @NonNull
    public static RequestBuilder<Long> getGroupsSize(int size) {
      return new RequestBuilder<>(Long.class, Groups.PREFIX + "groups-size").put("size", size);
    }

    @NonNull
    public static RequestBuilder<GroupInfo[]> getGroups(int page, int size) {
      return new RequestBuilder<>(GroupInfo[].class, Groups.PREFIX + "groups")
          .put("page", page)
          .put("size", size);
    }

    @NonNull
    public static RequestBuilder<Boolean> delete(@NonNull String id) {
      return new RequestBuilder<>(Boolean.class, Groups.PREFIX + "delete").put("id", id);
    }

    @NonNull
    public static RequestBuilder<Group[]> getGroups() {
      return new RequestBuilder<>(Group[].class, Groups.PREFIX + "all-groups");
    }

    @NonNull
    public static RequestBuilder<Group> create(
        int weight,
        @NonNull ValuesMap preferences,
        @NonNull Set<PermissionStack> permissions,
        @NonNull String name,
        @NonNull List<String> parents) {
      return new RequestBuilder<>(Group.class, Groups.PREFIX + "create")
          .put("weight", weight)
          .put("preferences", preferences)
          .put("permissions", permissions)
          .put("name", name)
          .put("parents", parents);
    }

    @NonNull
    public static RequestBuilder<Boolean> setWeight(@NonNull String id, int weight) {
      return new RequestBuilder<>(Boolean.class, Groups.PREFIX + "weight")
          .put("id", id)
          .put("weight", weight);
    }

    @NonNull
    public static RequestBuilder<Boolean> setName(@NonNull String id, @NonNull String name) {
      return new RequestBuilder<>(Boolean.class, Groups.PREFIX + "name")
          .put("id", id)
          .put("name", name);
    }

    @NonNull
    public static RequestBuilder<Boolean> setPreference(
        @NonNull String id, @NonNull String key, @NonNull Object value) {
      return new RequestBuilder<>(Boolean.class, Groups.PREFIX + "preference")
          .put("id", id)
          .put("key", key)
          .put("value", value);
    }

    @NonNull
    public static RequestBuilder<Boolean> removePreference(
        @NonNull String id, @NonNull String key) {
      return new RequestBuilder<>(Boolean.class, Groups.PREFIX + "remove-preference")
          .put("id", id)
          .put("key", key);
    }

    @NonNull
    public static RequestBuilder<Boolean> addParent(@NonNull String id, @NonNull String parent) {
      return new RequestBuilder<>(Boolean.class, Groups.PREFIX + "parent")
          .put("id", id)
          .put("parent", parent);
    }

    @NonNull
    public static RequestBuilder<Boolean> removeParent(@NonNull String id, @NonNull String parent) {
      return new RequestBuilder<>(Boolean.class, Groups.PREFIX + "remove-parent")
          .put("id", id)
          .put("parent", parent);
    }
  }

  public static class Server {

    @NonNull
    public RequestBuilder<Boolean> disconnect() {
      return new RequestBuilder<>(Boolean.class, "disconnect");
    }

    @NonNull
    public RequestBuilder<Boolean> clientInfo(@NonNull ValuesMap info) {
      return new RequestBuilder<>(Boolean.class, "client-info").put("info", info);
    }

    @NonNull
    public RequestBuilder<Boolean> auth(@NonNull String token) {
      return new RequestBuilder<>(Boolean.class, "auth").put("token", token);
    }
  }

  public static class Links {

    @NonNull public static String PREFIX = "link/";

    @NonNull
    public static RequestBuilder<Linkable[]> getLinks(@NonNull String userId) {
      return new RequestBuilder<>(Linkable[].class, Links.PREFIX + "user-links").put("id", userId);
    }

    @NonNull
    public static RequestBuilder<Linkable> getLink(
        @NonNull String userId, @NonNull LinkableType type) {
      return new RequestBuilder<>(Linkable.class, Links.PREFIX + "user-type")
          .put("id", userId)
          .put("type", type);
    }

    @NonNull
    public static RequestBuilder<Long> getLinksSize(@NonNull Collection<LinkableType> types) {
      return new RequestBuilder<>(Long.class, Links.PREFIX + "links-size").put("types", types);
    }

    @NonNull
    public static RequestBuilder<LinkableInfo[]> getLinks(
        int page, int size, @NonNull Collection<LinkableType> types) {
      return new RequestBuilder<>(LinkableInfo[].class, Links.PREFIX + "links")
          .put("page", page)
          .put("size", size)
          .put("types", types);
    }

    @NonNull
    public static RequestBuilder<Linkable> getLink(
        @NonNull LinkableType type,
        @NonNull ValuesMap identification,
        @NonNull ValuesMap recognition) {
      return new RequestBuilder<>(Linkable.class, "link")
          .put("type", type)
          .put("identification", identification)
          .put("recognition", recognition);
    }

    @NonNull
    public static RequestBuilder<Linkable> getLinkByIdentification(
        @NonNull LinkableType type, @NonNull ValuesMap identification) {
      return new RequestBuilder<>(Linkable.class, Links.PREFIX + "identification")
          .put("type", type)
          .put("identification", identification);
    }

    @NonNull
    public static RequestBuilder<Linkable> getLinkByRecognition(
        @NonNull LinkableType type, @NonNull ValuesMap recognition) {
      return new RequestBuilder<>(Linkable.class, Links.PREFIX + "recognition")
          .put("type", type)
          .put("identification", recognition);
    }

    @NonNull
    public static RequestBuilder<Linkable> create(
        @NonNull LinkableType type,
        @NonNull ValuesMap recognition,
        @NonNull ValuesMap identification,
        @NonNull ValuesMap preferences,
        @NonNull Map<String, Float> stats,
        @NonNull Collection<PermissionStack> permissions) {
      return new RequestBuilder<>(Linkable.class, Links.PREFIX + "create")
          .put("type", type)
          .put("recognition", recognition)
          .put("identification", identification)
          .put("preferences", preferences)
          .put("stats", stats)
          .put("permissions", permissions);
    }

    @NonNull
    public static RequestBuilder<Boolean> isLinked(@NonNull LinkableInfo link) {
      return new RequestBuilder<>(Boolean.class, Links.PREFIX + "is-linked").put("link", link);
    }

    @NonNull
    public static RequestBuilder<Boolean> link(@NonNull LinkableInfo link, @NonNull String id) {
      return new RequestBuilder<>(Boolean.class, Links.PREFIX + "link")
          .put("link", link)
          .put("id", id);
    }

    @NonNull
    public static RequestBuilder<Boolean> exists(@NonNull LinkableInfo link) {
      return new RequestBuilder<>(Boolean.class, Links.PREFIX + "exists").put("link", link);
    }

    @NonNull
    public static RequestBuilder<Boolean> preference(
        @NonNull LinkableInfo link, @NonNull String key, @NonNull Object value) {
      return new RequestBuilder<>(Boolean.class, Links.PREFIX + "preference")
          .put("link", link)
          .put("key", key)
          .put("value", value);
    }

    @NonNull
    public static RequestBuilder<Boolean> removePreference(
        @NonNull LinkableInfo link, @NonNull String key) {
      return new RequestBuilder<>(Boolean.class, Links.PREFIX + "remove-preference")
          .put("link", link)
          .put("key", key);
    }

    @NonNull
    public static RequestBuilder<PermissionStack> permissions(
        @NonNull LinkableInfo link, @NonNull String context, boolean global) {
      return new RequestBuilder<>(PermissionStack.class, Links.PREFIX + "permissions")
          .put("link", link)
          .put("context", context)
          .put("global", global);
    }

    @NonNull
    public static RequestBuilder<Boolean> permission(
        @NonNull LinkableInfo link, @NonNull String context, @NonNull Permission permission) {
      return new RequestBuilder<>(Boolean.class, Links.PREFIX + "permission")
          .put("link", link)
          .put("context", context)
          .put("permission", permission);
    }

    @NonNull
    public static RequestBuilder<Boolean> removePermission(
        @NonNull LinkableInfo link, @NonNull String context, @NonNull String permission) {
      return new RequestBuilder<>(Boolean.class, Links.PREFIX + "remove-permission")
          .put("link", link)
          .put("context", context)
          .put("permission", permission);
    }

    @NonNull
    public static RequestBuilder<SortedStats> stats(@NonNull LinkableInfo link) {
      return new RequestBuilder<>(SortedStats.class, Links.PREFIX + "stats").put("link", link);
    }

    @NonNull
    public static RequestBuilder<Boolean> resetStats(@NonNull LinkableInfo link) {
      return new RequestBuilder<>(Boolean.class, Links.PREFIX + "reset-stats").put("link", link);
    }
  }

  public static class Matches {
    @NonNull public static String PREFIX = "match/";

    @NonNull
    public static RequestBuilder<Match> create(
        long guildId, @NonNull Collection<MatchTeam> teams, @NonNull ValuesMap details) {
      return new RequestBuilder<>(Match.class, Matches.PREFIX + "create")
          .put("guild", guildId)
          .put("teams", teams)
          .put("details", details);
    }

    @NonNull
    public static RequestBuilder<Boolean> finish(@NonNull String id, int team) {
      return new RequestBuilder<>(Boolean.class, Matches.PREFIX + "finish")
          .put("id", id)
          .put("team", team);
    }

    @NonNull
    public static RequestBuilder<Integer> addTeam(@NonNull String id, @NonNull MatchTeam team) {
      return new RequestBuilder<>(Integer.class, Matches.PREFIX + "add-team")
          .put("id", id)
          .put("team", team);
    }

    @NonNull
    public static RequestBuilder<Boolean> removeTeam(@NonNull String id, int team) {
      return new RequestBuilder<>(Boolean.class, Matches.PREFIX + "remove-team")
          .put("id", id)
          .put("team", team);
    }

    @NonNull
    public static RequestBuilder<Boolean> status(@NonNull String id, @NonNull MatchStatus status) {
      return new RequestBuilder<>(Boolean.class, Matches.PREFIX + "status")
          .put("id", id)
          .put("status", status);
    }

    @NonNull
    public static RequestBuilder<Boolean> detail(
        @NonNull String id, @NonNull String key, @NonNull Object value) {
      return new RequestBuilder<>(Boolean.class, Matches.PREFIX + "detail")
          .put("id", id)
          .put("key", key)
          .put("value", value);
    }

    @NonNull
    public static RequestBuilder<Boolean> removeDetail(@NonNull String id, @NonNull String key) {
      return new RequestBuilder<>(Boolean.class, Matches.PREFIX + "remove-detail")
          .put("id", id)
          .put("key", key);
    }

    @NonNull
    public static RequestBuilder<Ladder> getLadder(@NonNull String name) {
      return new RequestBuilder<>(Ladder.class, Matches.PREFIX + "ladder").put("name", name);
    }
  }

  public static class Punishments {
    @NonNull public static String PREFIX = "link/";

    @NonNull
    public static RequestBuilder<Punishment> create(
        @NonNull PunishmentType type,
        @NonNull PunishmentStatus status,
        @NonNull LinkableInfo punisher,
        @NonNull LinkableInfo punished,
        @NonNull ValuesMap details,
        long expires) {
      return new RequestBuilder<>(Punishment.class, Punishments.PREFIX + "create")
          .put("type", type)
          .put("status", status)
          .put("punisher", punisher)
          .put("punished", punished)
          .put("details", details)
          .put("expires", expires);
    }

    @NonNull
    public static RequestBuilder<Boolean> expires(@NonNull String id, long expires) {
      return new RequestBuilder<>(Boolean.class, Punishments.PREFIX + "expires")
          .put("id", id)
          .put("expires", expires);
    }

    @NonNull
    public static RequestBuilder<Boolean> addDetail(
        @NonNull String id, @NonNull String key, @NonNull Object value) {
      return new RequestBuilder<>(Boolean.class, Punishments.PREFIX + "detail")
          .put("id", id)
          .put("key", key)
          .put("value", value);
    }

    @NonNull
    public static RequestBuilder<Boolean> removeDetail(@NonNull String id, @NonNull String key) {
      return new RequestBuilder<>(Boolean.class, Punishments.PREFIX + "remove-detail")
          .put("id", id)
          .put("key", key);
    }
  }
}
