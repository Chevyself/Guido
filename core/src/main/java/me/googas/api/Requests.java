package me.googas.api;

import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.NonNull;
import me.googas.api.immutable.ImmutableLadder;
import me.googas.api.immutable.ImmutableMinecraftMatch;
import me.googas.api.immutable.ImmutableMinecraftMatchTeam;
import me.googas.api.links.generic.ImmutableMinecraftLinkable;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.minecraft.MinecraftMatchTeam;
import me.googas.net.api.messages.RequestBuilder;

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

  public static class Server {

    @NonNull public static final String DISCONNECT = "disconnect";
    @NonNull public static final String CLIENT_INFO = "client-info";
    @NonNull public static final String AUTH = "auth";

    @NonNull
    public static RequestBuilder<Boolean> disconnect() {
      return new RequestBuilder<>(Boolean.class, Server.DISCONNECT);
    }

    @NonNull
    public static RequestBuilder<Boolean> auth(@NonNull String token) {
      return new RequestBuilder<>(Boolean.class, Server.AUTH).put("token", token);
    }
  }

  public static class MinecraftLinks {
    @NonNull public static final String PREFIX = "mclinks/";
    @NonNull public static final String SAVE_STATS = PREFIX + "save-stats";
    @NonNull public static final String SAVE_STATS_UUID = "uuid";
    @NonNull public static final String SAVE_STATS_CONTEXT = "context";
    @NonNull public static final String SAVE_STATS_STATS = "stats";
    @NonNull public static final String UPDATE_STATUS = PREFIX + "update-status";
    @NonNull public static final String UPDATE_STATUS_UUID = "uuid";
    @NonNull public static final String UPDATE_STATUS_NICKNAME = "nickname";
    @NonNull public static final String UPDATE_STATUS_IP = "ip";
    @NonNull public static final String UPDATE_STATUS_ONLINE = "online";
    @NonNull public static final String UPDATE_ONLINE = PREFIX + "update-online";
    @NonNull public static final String UPDATE_ONLINE_UUID = "uuid";
    @NonNull public static final String UPDATE_ONLINE_ONLINE = "uuid";
    @NonNull public static final String IS_LINKED = PREFIX + "is-linked";
    @NonNull public static final String IS_LINKED_UUID = "uuid";
    @NonNull public static final String LINK_NEW = PREFIX + "link-new";
    @NonNull public static final String LINK_NEW_UUID = "uuid";

    @NonNull
    public static RequestBuilder<Void> saveStats(
        @NonNull UUID uuid, @NonNull String context, @NonNull Map<String, Double> stats) {
      return new RequestBuilder<>(Void.class, MinecraftLinks.SAVE_STATS)
          .put(MinecraftLinks.SAVE_STATS_UUID, uuid)
          .put(MinecraftLinks.SAVE_STATS_CONTEXT, context)
          .put(MinecraftLinks.SAVE_STATS_STATS, stats);
    }

    @NonNull
    public static RequestBuilder<ImmutableMinecraftLinkable> updateStatus(
        @NonNull UUID uuid, @NonNull String nickname, @NonNull String ip, boolean online) {
      return new RequestBuilder<>(ImmutableMinecraftLinkable.class, MinecraftLinks.UPDATE_STATUS)
          .put(MinecraftLinks.UPDATE_STATUS_UUID, uuid)
          .put(MinecraftLinks.UPDATE_STATUS_NICKNAME, nickname)
          .put(MinecraftLinks.UPDATE_STATUS_IP, ip)
          .put(MinecraftLinks.UPDATE_STATUS_ONLINE, online);
    }

    @NonNull
    public static RequestBuilder<Void> updateOnline(@NonNull UUID uuid, boolean online) {
      return new RequestBuilder<>(Void.class, MinecraftLinks.UPDATE_ONLINE)
          .put(MinecraftLinks.UPDATE_ONLINE_UUID, uuid)
          .put(MinecraftLinks.UPDATE_ONLINE_ONLINE, online);
    }

    public static RequestBuilder<Boolean> isLinked(@NonNull UUID uuid) {
      return new RequestBuilder<>(Boolean.class, MinecraftLinks.IS_LINKED)
          .put(MinecraftLinks.IS_LINKED_UUID, uuid);
    }

    @NonNull
    public static RequestBuilder<String> linkNew(@NonNull UUID minecraftId) {
      return new RequestBuilder<>(String.class, MinecraftLinks.LINK_NEW)
          .put(MinecraftLinks.LINK_NEW_UUID, minecraftId);
    }
  }

  public static class MatchServer {

    @NonNull public static final String PREFIX = "server/";
    @NonNull public static final String CAN_HOST = MatchServer.PREFIX + "can-host";
    @NonNull public static final String CAN_HOST_MATCH = "match";
    @NonNull public static final String HOST = MatchServer.PREFIX + "host";
    @NonNull public static final String HOST_MATCH = "match";
    @NonNull public static final String SERVER_READY = MatchServer.PREFIX + "server-ready";

    @NonNull
    public static RequestBuilder<Void> serverReady() {
      return new RequestBuilder<>(Void.class, MatchServer.SERVER_READY);
    }

    public static RequestBuilder<Boolean> canHost(@NonNull ImmutableMinecraftMatch match) {
      return new RequestBuilder<>(Boolean.class, MatchServer.CAN_HOST).put(CAN_HOST_MATCH, match);
    }

    public static RequestBuilder<String> host(@NonNull ImmutableMinecraftMatch match) {
      return new RequestBuilder<>(String.class, MatchServer.HOST).put(HOST_MATCH, match);
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

  public static class Client {
    @NonNull public static final String PREFIX = "client/";
    @NonNull public static final String DISCONNECTED = Client.PREFIX + "disconnected";

    @NonNull
    public static RequestBuilder<Boolean> disconnected() {
      return new RequestBuilder<>(Boolean.class, Client.DISCONNECTED);
    }
  }

  // TODO receptor
  public static class MinecraftMatches {

    @NonNull public static final String PREFIX = "mcmatch/";
    @NonNull public static final String SET_TEAMS = PREFIX + "set-teams";
    @NonNull public static final String SET_TEAMS_MATCH_ID = "match-id";
    @NonNull public static final String SET_TEAMS_TEAMS = "team";
    @NonNull public static final String UPDATE_STATUS = PREFIX + "update-status";
    @NonNull public static final String UPDATE_STATUS_MATCH_ID = "match-id";
    @NonNull public static final String UPDATE_STATUS_STATUS = "status";
    @NonNull public static final String SET_MAP = PREFIX + "set-map";
    @NonNull public static final String SET_MAP_MATCH_ID = "match-id";
    @NonNull public static final String SET_MAP_MAP_NAME = "map_name";
    @NonNull public static final String ON_FINISH = PREFIX + "finish";
    @NonNull public static final String ON_FINISH_MATCH_ID = "match-id";
    @NonNull public static final String ON_FINISH_WINNERS_ID = "winners-id";
    @NonNull public static final String LADDER = PREFIX + "ladder";
    @NonNull public static final String LADDER_NAME = "ladder";

    public static RequestBuilder<SetTeamsData> setTeams(@NonNull UUID matchId, SetTeamsData teams) {
      return new RequestBuilder<>(SetTeamsData.class, MinecraftMatches.SET_TEAMS)
          .put(MinecraftMatches.SET_TEAMS_MATCH_ID, matchId)
          .put(MinecraftMatches.SET_TEAMS_TEAMS, teams);
    }

    public static RequestBuilder<Void> updateStatus(
        @NonNull UUID matchId, @NonNull MatchStatus status) {
      return new RequestBuilder<>(Void.class, MinecraftMatches.UPDATE_STATUS)
          .put(MinecraftMatches.UPDATE_STATUS_MATCH_ID, matchId)
          .put(MinecraftMatches.UPDATE_STATUS_STATUS, status);
    }

    public static RequestBuilder<Void> setMap(@NonNull UUID matchId, @NonNull String mapName) {
      return new RequestBuilder<>(Void.class, MinecraftMatches.SET_MAP)
          .put(MinecraftMatches.SET_MAP_MATCH_ID, matchId)
          .put(MinecraftMatches.SET_MAP_MAP_NAME, mapName);
    }

    public static RequestBuilder<Void> onFinish(@NonNull UUID matchId, int winnersId) {
      return new RequestBuilder<>(Void.class, MinecraftMatches.ON_FINISH)
          .put(MinecraftMatches.ON_FINISH_MATCH_ID, matchId)
          .put(MinecraftMatches.ON_FINISH_WINNERS_ID, winnersId);
    }

    @NonNull
    public static RequestBuilder<ImmutableLadder> getLadder(@NonNull String name) {
      return new RequestBuilder<>(ImmutableLadder.class, MinecraftMatches.LADDER)
          .put(MinecraftMatches.LADDER_NAME, name);
    }
  }

  public static class SetTeamsData {
    @NonNull @Getter private final List<ImmutableMinecraftMatchTeam> teams;

    public SetTeamsData(@NonNull List<? extends MinecraftMatchTeam> result) {
      this.teams =
          result.stream().map(ImmutableMinecraftMatchTeam::new).collect(Collectors.toList());
    }
  }
}
