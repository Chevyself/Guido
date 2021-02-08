package me.googas.api;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import lombok.NonNull;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.api.matches.AbstractMatch;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.MatchTeam;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.permissions.AbstractPermission;
import me.googas.api.permissions.Group;
import me.googas.api.permissions.GroupInfo;
import me.googas.api.permissions.PermissionStack;
import me.googas.api.punishment.Punishment;
import me.googas.api.punishment.PunishmentStatus;
import me.googas.api.punishment.PunishmentType;
import me.googas.api.utility.SortedStats;
import me.googas.messaging.RequestBuilder;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

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
    @NonNull public static final String PREFIX = "group/";
    @NonNull public static final String GROUP = "group";
    @NonNull public static final String GROUPS_SIZE = Groups.PREFIX + "group-size";
    @NonNull public static final String GROUPS = Groups.PREFIX + "groups";
    @NonNull public static final String DELETE = Groups.PREFIX + "delete";
    @NonNull public static final String ALL_GROUPS = Groups.PREFIX + "all-groups";
    @NonNull public static final String CREATE = Groups.PREFIX + "create";
    @NonNull public static final String WEIGHT = Groups.PREFIX + "weight";
    @NonNull public static final String NAME = Groups.PREFIX + "name";
    @NonNull public static final String PREFERENCE = Groups.PREFIX + "preference";
    @NonNull public static final String REMOVE_PREFERENCE = Groups.PREFIX + "remove-preference";
    @NonNull public static final String ADD_PERMISSION = Groups.PREFIX + "add-permission";
    @NonNull public static final String REMOVE_PERMISSION = Groups.PREFIX + "remove-permission";
    @NonNull public static final String PARENT = Groups.PREFIX + "parent";
    @NonNull public static final String REMOVE_PARENT = Groups.PREFIX + "remove-parent";

    @NonNull
    public static RequestBuilder<Group> getGroup(@NonNull String id) {
      return new RequestBuilder<>(Group.class, "group").put("id", id);
    }

    @NonNull
    public static RequestBuilder<Long> getGroupsSize(int size) {
      return new RequestBuilder<>(Long.class, Groups.GROUPS_SIZE).put("size", size);
    }

    @NonNull
    public static RequestBuilder<GroupInfo[]> getGroups(int page, int size) {
      return new RequestBuilder<>(GroupInfo[].class, Groups.GROUPS)
          .put("page", page)
          .put("size", size);
    }

    @NonNull
    public static RequestBuilder<Boolean> delete(@NonNull String id) {
      return new RequestBuilder<>(Boolean.class, Groups.DELETE).put("id", id);
    }

    @NonNull
    public static RequestBuilder<Group> create(
        @NonNull List<String> parents,
        @NonNull Map<String, Map<String, Object>> information,
        @NonNull Set<PermissionStack> permissions,
        @NonNull String name,
        int weight) {
      return new RequestBuilder<>(Group.class, Groups.CREATE)
          .put("parents", parents)
          .put("information", information)
          .put("permissions", permissions)
          .put("name", name)
          .put("weight", weight);
    }

    @NonNull
    public static RequestBuilder<Boolean> setWeight(@NonNull String id, int weight) {
      return new RequestBuilder<>(Boolean.class, Groups.WEIGHT).put("id", id).put("weight", weight);
    }

    @NonNull
    public static RequestBuilder<Boolean> setName(@NonNull String id, @NonNull String name) {
      return new RequestBuilder<>(Boolean.class, Groups.NAME).put("id", id).put("name", name);
    }

    @NonNull
    public static RequestBuilder<Boolean> setPreference(
        @NonNull String id, @NonNull String key, @NonNull Object value) {
      return new RequestBuilder<>(Boolean.class, Groups.PREFERENCE)
          .put("id", id)
          .put("key", key)
          .put("value", value);
    }

    @NonNull
    public static RequestBuilder<Boolean> removePreference(
        @NonNull String id, @NonNull String key) {
      return new RequestBuilder<>(Boolean.class, Groups.REMOVE_PREFERENCE)
          .put("id", id)
          .put("key", key);
    }

    @NonNull
    public static RequestBuilder<Boolean> addPermission(
        @NonNull String id,
        @NonNull String context,
        @NonNull AbstractPermission abstractPermission) {
      return new RequestBuilder<>(Boolean.class, Groups.ADD_PERMISSION)
          .put("id", id)
          .put("context", context)
          .put("abstractPermission", abstractPermission);
    }

    @NonNull
    public static RequestBuilder<Boolean> removePermission(
        @NonNull String id, @NonNull String context, @NonNull String node) {
      return new RequestBuilder<>(Boolean.class, Groups.REMOVE_PERMISSION)
          .put("id", id)
          .put("context", context)
          .put("node", node);
    }

    @NonNull
    public static RequestBuilder<Boolean> addParent(@NonNull String id, @NonNull String parent) {
      return new RequestBuilder<>(Boolean.class, Groups.PARENT).put("id", id).put("parent", parent);
    }

    @NonNull
    public static RequestBuilder<Boolean> removeParent(@NonNull String id, @NonNull String parent) {
      return new RequestBuilder<>(Boolean.class, Groups.REMOVE_PARENT)
          .put("id", id)
          .put("parent", parent);
    }

    @NonNull
    public static RequestBuilder<Group[]> getGroups() {
      return new RequestBuilder<>(Group[].class, Groups.ALL_GROUPS);
    }
  }

  public static class Server {

    @NonNull public static final String DISCONNECT = "disconnect";
    @NonNull public static final String CLIENT_INFO = "client-info";
    @NonNull public static final String AUTH = "auth";
    @NonNull public static final String LINK_CODE = "link-code";

    @NonNull
    public static RequestBuilder<Boolean> disconnect() {
      return new RequestBuilder<>(Boolean.class, Server.DISCONNECT);
    }

    @NonNull
    public static RequestBuilder<Boolean> clientInfo(@NonNull ValuesMap info) {
      return new RequestBuilder<>(Boolean.class, Server.CLIENT_INFO).put("info", info);
    }

    @NonNull
    public static RequestBuilder<Boolean> auth(@NonNull String token) {
      return new RequestBuilder<>(Boolean.class, Server.AUTH).put("token", token);
    }

    @NonNull
    public static RequestBuilder<String> linkCode(@NonNull LinkableInfo link) {
      return new RequestBuilder<>(String.class, Server.LINK_CODE).put("link", link);
    }
  }

  public static class Links {

    @NonNull public static final String PREFIX = "link/";
    @NonNull public static final String USER_LINKS = Links.PREFIX + "user-links";
    @NonNull public static final String USER_TYPE = Links.PREFIX + "user-type";
    @NonNull public static final String LINKS_SIZE = Links.PREFIX + "links-size";
    @NonNull public static final String LINKS = Links.PREFIX + "links";
    @NonNull public static final String LINK = "link";
    @NonNull public static final String IDENTIFICATION = Links.PREFIX + "identification";
    @NonNull public static final String RECOGNITION = Links.PREFIX + "recognition";
    @NonNull public static final String CREATE = Links.PREFIX + "create";
    @NonNull public static final String IS_LINKED = Links.PREFIX + "is-linked";
    @NonNull public static final String LINK_LINK = Links.PREFIX + "link";
    @NonNull public static final String EXISTS = Links.PREFIX + "exists";
    @NonNull public static final String SET_RECOGNITION = Links.PREFIX + "set-recognition";
    @NonNull public static final String PREFERENCE = Links.PREFIX + "preference";
    @NonNull public static final String REMOVE_PREFERENCE = Links.PREFIX + "remove-preference";
    @NonNull public static final String PERMISSIONS = Links.PREFIX + "permissions";
    @NonNull public static final String PERMISSION = Links.PREFIX + "permission";
    @NonNull public static final String REMOVE_PERMISSION = Links.PREFIX + "remove-permission";
    @NonNull public static final String STATS = Links.PREFIX + "stats";
    @NonNull public static final String SAVE_STATS = Links.PREFIX + "save-stats";
    @NonNull public static final String RESET_STATS = Links.PREFIX + "reset-stats";

    @NonNull
    public static RequestBuilder<Linkable[]> getLinks(@NonNull String userId) {
      return new RequestBuilder<>(Linkable[].class, Links.USER_LINKS).put("id", userId);
    }

    @NonNull
    public static RequestBuilder<Linkable> getLink(
        @NonNull String userId, @NonNull LinkableType type) {
      return new RequestBuilder<>(Linkable.class, Links.USER_TYPE)
          .put("id", userId)
          .put("type", type);
    }

    @NonNull
    public static RequestBuilder<Long> getLinksSize(@NonNull Collection<LinkableType> types) {
      return new RequestBuilder<>(Long.class, Links.LINKS_SIZE).put("types", types);
    }

    @NonNull
    public static RequestBuilder<LinkableInfo[]> getLinks(
        int page, int size, @NonNull Collection<LinkableType> types) {
      return new RequestBuilder<>(LinkableInfo[].class, Links.LINKS)
          .put("page", page)
          .put("size", size)
          .put("types", types);
    }

    @NonNull
    public static RequestBuilder<Linkable> getLink(
        @NonNull LinkableType type,
        @NonNull Map<String, Object> identification,
        @NonNull Map<String, Object> recognition) {
      return new RequestBuilder<>(Linkable.class, "link")
          .put("type", type)
          .put("identification", identification)
          .put("recognition", recognition);
    }

    @NonNull
    public static RequestBuilder<Linkable> getLinkByIdentification(
        @NonNull LinkableType type, @NonNull Map<String, Object> identification) {
      return new RequestBuilder<>(Linkable.class, Links.IDENTIFICATION)
          .put("type", type)
          .put("identification", identification);
    }

    @NonNull
    public static RequestBuilder<Linkable> getLinkByRecognition(
        @NonNull LinkableType type, @NonNull Map<String, Object> recognition) {
      return new RequestBuilder<>(Linkable.class, Links.RECOGNITION)
          .put("type", type)
          .put("recognition", recognition);
    }

    @NonNull
    public static RequestBuilder<Linkable> create(
        @NonNull LinkableType type,
        @NonNull Map<String, Object> identification,
        @NonNull Map<String, Object> recognition,
        @NonNull Map<String, Double> accounts,
        @NonNull Set<PermissionStack> permissions,
        @NonNull Map<String, Map<String, Object>> information,
        @NonNull Map<String, Map<String, Float>> stats) {
      return new RequestBuilder<>(Linkable.class, Links.CREATE)
          .put("type", type)
          .put("identification", identification)
          .put("recognition", recognition)
          .put("accounts", accounts)
          .put("permissions", permissions)
          .put("information", information)
          .put("stats", stats);
    }

    @NonNull
    public static RequestBuilder<Boolean> isLinked(@NonNull LinkableInfo link) {
      return new RequestBuilder<>(Boolean.class, Links.IS_LINKED).put("link", link);
    }

    @NonNull
    public static RequestBuilder<Boolean> link(@NonNull LinkableInfo link, @NonNull String id) {
      return new RequestBuilder<>(Boolean.class, Links.LINK_LINK).put("link", link).put("id", id);
    }

    @NonNull
    public static RequestBuilder<Boolean> exists(@NonNull LinkableInfo link) {
      return new RequestBuilder<>(Boolean.class, Links.EXISTS).put("link", link);
    }

    @NonNull
    public static RequestBuilder<Boolean> setRecognition(
        @NonNull LinkableInfo link, @NonNull String key, @NonNull Object value) {
      return new RequestBuilder<>(Boolean.class, Links.SET_RECOGNITION)
          .put("link", link)
          .put("key", key)
          .put("value", value);
    }

    @NonNull
    public static RequestBuilder<Boolean> preference(
        @NonNull LinkableInfo link, @NonNull String key, @NonNull Object value) {
      return new RequestBuilder<>(Boolean.class, Links.PREFERENCE)
          .put("link", link)
          .put("key", key)
          .put("value", value);
    }

    @NonNull
    public static RequestBuilder<Boolean> removePreference(
        @NonNull LinkableInfo link, @NonNull String key) {
      return new RequestBuilder<>(Boolean.class, Links.REMOVE_PREFERENCE)
          .put("link", link)
          .put("key", key);
    }

    @NonNull
    public static RequestBuilder<PermissionStack> permissions(
        @NonNull LinkableInfo link, @NonNull String context, boolean global) {
      return new RequestBuilder<>(PermissionStack.class, Links.PERMISSIONS)
          .put("link", link)
          .put("context", context)
          .put("global", global);
    }

    @NonNull
    public static RequestBuilder<Boolean> permission(
        @NonNull LinkableInfo link,
        @NonNull String context,
        @NonNull AbstractPermission abstractPermission) {
      return new RequestBuilder<>(Boolean.class, Links.PERMISSION)
          .put("link", link)
          .put("context", context)
          .put("abstractPermission", abstractPermission);
    }

    @NonNull
    public static RequestBuilder<Boolean> removePermission(
        @NonNull LinkableInfo link, @NonNull String context, @NonNull String permission) {
      return new RequestBuilder<>(Boolean.class, Links.REMOVE_PERMISSION)
          .put("link", link)
          .put("context", context)
          .put("permission", permission);
    }

    @NonNull
    public static RequestBuilder<SortedStats> stats(@NonNull LinkableInfo link) {
      return new RequestBuilder<>(SortedStats.class, Links.STATS).put("link", link);
    }

    @NonNull
    public static RequestBuilder<Boolean> saveStats(
        @NonNull LinkableInfo link, @NonNull Map<String, Float> stats) {
      return new RequestBuilder<>(Boolean.class, Links.SAVE_STATS)
          .put("link", link)
          .put("stats", stats);
    }

    @NonNull
    public static RequestBuilder<Boolean> resetStats(@NonNull LinkableInfo link) {
      return new RequestBuilder<>(Boolean.class, Links.PREFIX + "reset-stats").put("link", link);
    }
  }

  public static class Matches {
    @NonNull public static final String PREFIX = "match/";
    @NonNull public static final String MATCH = "match";
    @NonNull public static final String CREATE = Matches.PREFIX + "create";
    @NonNull public static final String FINISH = Matches.PREFIX + "finish";
    @NonNull public static final String ADD_TEAM = Matches.PREFIX + "add-team";
    @NonNull public static final String REMOVE_TEAM = Matches.PREFIX + "remove-team";
    @NonNull public static final String STATUS = Matches.PREFIX + "status";
    @NonNull public static final String DETAIL = Matches.PREFIX + "detail";
    @NonNull public static final String REMOVE_DETAIL = Matches.PREFIX + "remove-detail";
    @NonNull public static final String LADDER = Matches.PREFIX + "ladder";

    @NonNull
    public static RequestBuilder<AbstractMatch> create(
        @NonNull Map<String, Map<String, Object>> information, @NonNull Set<MatchTeam> teams) {
      return new RequestBuilder<>(AbstractMatch.class, Matches.CREATE)
          .put("information", information)
          .put("teams", teams);
    }

    @NonNull
    public static RequestBuilder<Boolean> finish(@NonNull String id, int team) {
      return new RequestBuilder<>(Boolean.class, Matches.FINISH).put("id", id).put("team", team);
    }

    @NonNull
    public static RequestBuilder<Integer> addTeam(@NonNull String id, @NonNull MatchTeam team) {
      return new RequestBuilder<>(Integer.class, Matches.ADD_TEAM).put("id", id).put("team", team);
    }

    @NonNull
    public static RequestBuilder<Boolean> removeTeam(@NonNull String id, int team) {
      return new RequestBuilder<>(Boolean.class, Matches.REMOVE_TEAM)
          .put("id", id)
          .put("team", team);
    }

    @NonNull
    public static RequestBuilder<Boolean> status(@NonNull String id, @NonNull MatchStatus status) {
      return new RequestBuilder<>(Boolean.class, Matches.STATUS)
          .put("id", id)
          .put("status", status);
    }

    @NonNull
    public static RequestBuilder<Boolean> detail(
        @NonNull String id, @NonNull String key, @NonNull Object value) {
      return new RequestBuilder<>(Boolean.class, Matches.DETAIL)
          .put("id", id)
          .put("key", key)
          .put("value", value);
    }

    @NonNull
    public static RequestBuilder<Boolean> removeDetail(@NonNull String id, @NonNull String key) {
      return new RequestBuilder<>(Boolean.class, Matches.REMOVE_DETAIL)
          .put("id", id)
          .put("key", key);
    }

    @NonNull
    public static RequestBuilder<Ladder> getLadder(@NonNull String name) {
      return new RequestBuilder<>(Ladder.class, Matches.LADDER).put("name", name);
    }
  }

  public static class Punishments {
    @NonNull public static final String PREFIX = "punishment/";
    @NonNull public static final String PUNISHMENT = "punishment";
    @NonNull public static final String PUNISHMENTS = Punishments.PREFIX + "punishments";
    @NonNull public static final String CREATE = Punishments.PREFIX + "create";
    @NonNull public static final String STATUS = Punishments.PREFIX + "status";
    @NonNull public static final String EXPIRES = Punishments.PREFIX + "getExpires";
    @NonNull public static final String DETAIL = Punishments.PREFIX + "detail";
    @NonNull public static final String REMOVE_DETAIL = Punishments.PREFIX + "remove-detail";

    @NonNull
    public static RequestBuilder<Punishment> getPunishment(@NonNull String id) {
      return new RequestBuilder<>(Punishment.class, Punishments.PUNISHMENT).put("id", id);
    }

    @NonNull
    public static RequestBuilder<Punishment[]> getPunishments(
        @NonNull LinkableInfo link, @NonNull Collection<PunishmentStatus> statuses) {
      return new RequestBuilder<>(Punishment[].class, Punishments.PUNISHMENTS)
          .put("link", link)
          .put("statuses", statuses);
    }

    @NonNull
    public static RequestBuilder<Punishment> create(
        @NonNull PunishmentType type,
        @NonNull PunishmentStatus status,
        @NonNull Map<String, Map<String, Object>> information,
        @NonNull LinkableInfo punisher,
        @NonNull LinkableInfo punished,
        long expires) {
      return new RequestBuilder<>(Punishment.class, Punishments.CREATE)
          .put("type", type)
          .put("status", status)
          .put("information", information)
          .put("punisher", punisher)
          .put("punished", punished)
          .put("getExpires", expires);
    }

    @NonNull
    public static RequestBuilder<Boolean> expires(@NonNull String id, long expires) {
      return new RequestBuilder<>(Boolean.class, Punishments.EXPIRES)
          .put("id", id)
          .put("getExpires", expires);
    }

    @NonNull
    public static RequestBuilder<Boolean> addDetail(
        @NonNull String id, @NonNull String key, @NonNull Object value) {
      return new RequestBuilder<>(Boolean.class, Punishments.DETAIL)
          .put("id", id)
          .put("key", key)
          .put("value", value);
    }

    @NonNull
    public static RequestBuilder<Boolean> removeDetail(@NonNull String id, @NonNull String key) {
      return new RequestBuilder<>(Boolean.class, Punishments.REMOVE_DETAIL)
          .put("id", id)
          .put("key", key);
    }
  }

  public static class MatchServer {

    @NonNull public static final String PREFIX = "server/";
    @NonNull public static final String CAN_HOST = MatchServer.PREFIX + "can-host";
    @NonNull public static final String HOST = MatchServer.PREFIX + "host";
    @NonNull public static final String SERVER_READY = MatchServer.PREFIX + "server-ready";

    @NotNull
    public static RequestBuilder<Boolean> canHost(@NonNull AbstractMatch abstractMatch) {
      return new RequestBuilder<>(Boolean.class, MatchServer.CAN_HOST)
          .put("abstractMatch", abstractMatch);
    }

    // Must return the server IP
    @NonNull
    public static RequestBuilder<String> host(@NonNull AbstractMatch abstractMatch) {
      return new RequestBuilder<>(String.class, MatchServer.HOST)
          .put("abstractMatch", abstractMatch);
    }

    @NonNls
    public static RequestBuilder<Boolean> serverReady() {
      return new RequestBuilder<>(Boolean.class, MatchServer.SERVER_READY);
    }
  }

  public static class Bungee {

    @NonNull public static final String PREFIX = "bungee/";
    @NonNull public static final String SEND_SERVER = Bungee.PREFIX + "send-to-server";
    @NonNull public static final String SEND_SERVER_IP = Bungee.PREFIX + "send-to-server-by-ip";
    @NonNull public static final String SEND_MESSAGE = Bungee.PREFIX + "send-message";
    @NonNull public static final String SEND_LOCALIZED = Bungee.PREFIX + "send-message-localized";
    @NonNull public static final String ADD_QUEUE = Bungee.PREFIX + "add-queue";
    @NonNull public static final String REMOVE_QUEUE = Bungee.PREFIX + "remove-queue";
    @NonNull public static final String IS_ONLINE = Bungee.PREFIX + "is-online";
    @NonNull public static final String SERVER_NAME = Bungee.PREFIX + "server-name";

    @NonNull
    public static RequestBuilder<Boolean> sendToServer(@NonNull UUID uuid, @NonNull String server) {
      return new RequestBuilder<>(Boolean.class, Bungee.SEND_SERVER)
          .put("uuid", uuid)
          .put("server", server);
    }

    @NonNull
    public static RequestBuilder<Boolean> sendToServerByIp(
        @NonNull List<UUID> uuids, @NonNull String ip) {
      return new RequestBuilder<>(Boolean.class, Bungee.SEND_SERVER_IP)
          .put("uuids", uuids)
          .put("server", ip);
    }

    @NonNull
    public static RequestBuilder<Boolean> sendMessage(@NonNull UUID uuid, @NonNull String message) {
      return new RequestBuilder<>(Boolean.class, Bungee.SEND_MESSAGE)
          .put("uuid", uuid)
          .put("message", message);
    }

    @NonNull
    public static RequestBuilder<Boolean> sendLocalized(
        @NonNull UUID uuid, @NonNull String key, @NonNull Map<String, String> placeholders) {
      return new RequestBuilder<>(Boolean.class, Bungee.SEND_LOCALIZED)
          .put("uuid", uuid)
          .put("key", key)
          .put("placeholders", placeholders);
    }

    @NonNull
    public static RequestBuilder<Boolean> addQueue(@NonNull UUID uuid) {
      return new RequestBuilder<>(Boolean.class, Bungee.ADD_QUEUE).put("uuid", uuid);
    }

    @NonNull
    public static RequestBuilder<Boolean> removeQueue(@NonNull UUID uuid) {
      return new RequestBuilder<>(Boolean.class, Bungee.REMOVE_QUEUE).put("uuid", uuid);
    }

    @NonNull
    @Deprecated
    public static RequestBuilder<Boolean> isOnline(@NonNull UUID uuid) {
      return new RequestBuilder<>(Boolean.class, Bungee.IS_ONLINE).put("uuid", uuid);
    }

    @NonNull
    public static RequestBuilder<String> serverName(@NonNull String ip) {
      return new RequestBuilder<>(String.class, Bungee.SERVER_NAME).put("ip", ip);
    }
  }

  public static class Deploy {

    @NonNull public static final String PREFIX = "deploy/";
    @NonNull public static final String ADD_PERMISSION = Deploy.PREFIX + "add-permission";
    @NonNull public static final String REMOVE_PERMISSION = Deploy.PREFIX + "remove-permission";

    @NonNull
    public static RequestBuilder<Boolean> addPermission(
        @NonNull LinkableInfo link,
        @NonNull String context,
        @NonNull String node,
        boolean enabled,
        long expires) {
      return new RequestBuilder<>(Boolean.class, Deploy.ADD_PERMISSION)
          .put("link", link)
          .put("context", context)
          .put("node", node)
          .put("enabled", enabled)
          .put("getExpires", expires);
    }

    @NonNull
    public static RequestBuilder<Boolean> removePermission(
        @NonNull LinkableInfo link, @NonNull String context, @NonNull String node) {
      return new RequestBuilder<>(Boolean.class, Deploy.REMOVE_PERMISSION)
          .put("link", link)
          .put("context", context)
          .put("node", node);
    }
  }

  public static class Client {
    @NonNull public static final String PREFIX = "client/";
    @NonNull public static final String DISCONNECTED = Client.PREFIX + "disconnected";

    @NonNull
    public static RequestBuilder<Boolean> disconnected() {
      return new RequestBuilder<>(Boolean.class, Client.DISCONNECTED);
    }
  }
}
