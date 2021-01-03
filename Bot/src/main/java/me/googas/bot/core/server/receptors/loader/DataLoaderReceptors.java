package me.googas.bot.core.server.receptors.loader;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import lombok.NonNull;
import me.googas.api.ValuesMap;
import me.googas.api.links.Linkable;
import me.googas.api.links.LinkableInfo;
import me.googas.api.links.LinkableType;
import me.googas.api.loader.DataLoader;
import me.googas.api.matches.Match;
import me.googas.api.matches.MatchInfo;
import me.googas.api.matches.MatchStatus;
import me.googas.api.matches.team.Team;
import me.googas.api.permissions.Group;
import me.googas.api.permissions.GroupInfo;
import me.googas.api.punishment.Punishment;
import me.googas.api.punishment.PunishmentStatus;
import me.googas.api.token.AuthToken;
import me.googas.api.user.UserData;
import me.googas.bot.Guido;
import me.googas.messaging.json.ParamName;
import me.googas.messaging.json.Receptor;

public class DataLoaderReceptors {

  @NonNull
  public static DataLoader loader() {
    return Guido.getDataLoader();
  }

  @Receptor("user")
  public UserData getUser(@ParamName("id") String id) {
    return DataLoaderReceptors.loader().getUserData(id);
  }

  @Receptor("token")
  public AuthToken getToken(@ParamName("token") String token) {
    return DataLoaderReceptors.loader().getAuthToken(token);
  }

  @Receptor("links")
  public Collection<Linkable> getLinks(@ParamName("id") String id) {
    UserData data = DataLoaderReceptors.loader().getUserData(id);
    if (data == null) return new ArrayList<>();
    return DataLoaderReceptors.loader().getLinks(data);
  }

  @Receptor("link")
  public Linkable getLink(@ParamName("id") String id, @ParamName("type") LinkableType type) {
    UserData data = DataLoaderReceptors.loader().getUserData(id);
    if (data == null) return null;
    return DataLoaderReceptors.loader().getLink(data, type);
  }

  @Receptor("links-all")
  public Collection<LinkableInfo> getAllLinks(
      @ParamName("page") int page,
      @ParamName("size") int size,
      @ParamName("types") LinkableType[] types) {
    return DataLoaderReceptors.loader().getLinks(page, size, types);
  }

  @Receptor("tokens")
  public Collection<AuthToken> getTokens(@ParamName("id") String id) {
    UserData data = DataLoaderReceptors.loader().getUserData(id);
    if (data == null) return null;
    return DataLoaderReceptors.loader().getTokens(data);
  }

  @Receptor("link")
  public Linkable getLink(
      @ParamName("type") LinkableType type,
      @ParamName("identification") ValuesMap identification,
      @ParamName("recognition") ValuesMap recognition) {
    return DataLoaderReceptors.loader().getLink(type, identification, recognition);
  }

  @Receptor("link-identification")
  public Linkable getLink(
      @ParamName("type") LinkableType type, @ParamName("identification") ValuesMap identification) {
    return DataLoaderReceptors.loader().getLink(type, identification);
  }

  @Receptor("link-recognition")
  public Linkable getLinkByRecognition(
      @ParamName("type") LinkableType type, @ParamName("recognition") ValuesMap recognition) {
    return DataLoaderReceptors.loader().getLinkByRecognition(type, recognition);
  }

  @Receptor("match")
  public Match getMatch(@ParamName("id") String id) {
    return DataLoaderReceptors.loader().getMatch(id);
  }

  @Receptor("punishment")
  public Punishment getPunishment(@ParamName("id") String id) {
    return DataLoaderReceptors.loader().getPunishment(id);
  }

  @Receptor("group")
  public Group getGroup(@ParamName("id") String id) {
    return DataLoaderReceptors.loader().getGroup(id);
  }

  @Receptor("groups")
  public Collection<GroupInfo> getGroups(@ParamName("page") int page, @ParamName("size") int size) {
    return DataLoaderReceptors.loader().getGroups(page, size);
  }

  @Receptor("all-groups")
  public Collection<Group> getGroups() {
    return DataLoaderReceptors.loader().getGroups();
  }

  @Receptor("leaderboard")
  public Map<Integer, LinkableInfo> getLeaderboard(
      @ParamName("stat") String stat,
      @ParamName("page") int page,
      @ParamName("size") int size,
      @ParamName("inverted") boolean inverted) {
    return DataLoaderReceptors.loader().getLeaderboard(stat, page, size, inverted);
  }

  @Receptor("matches")
  public Collection<MatchInfo> getMatches(
      @ParamName("page") int page,
      @ParamName("size") int size,
      @ParamName("statuses") MatchStatus... statuses) {
    return DataLoaderReceptors.loader().getMatches(page, size, statuses);
  }

  @Receptor("delete-team")
  public boolean deleteTeam(@ParamName("id") String id) {
    return DataLoaderReceptors.loader().deleteTeam(id);
  }

  @Receptor("delete-group")
  public boolean deleteGroup(@ParamName("id") String id) {
    return DataLoaderReceptors.loader().deleteGroup(id);
  }

  @Receptor("team")
  public Team getTeam(@ParamName("id") String id) {
    return DataLoaderReceptors.loader().getTeam(id);
  }

  @Receptor("team-by-name")
  public Team getTeamByName(@ParamName("name") String name) {
    return DataLoaderReceptors.loader().getTeamByName(name);
  }

  @Receptor("page-leaderboard")
  public long getMaxPageLeaderboard(@ParamName("stat") String stat, @ParamName("size") int size) {
    return DataLoaderReceptors.loader().maxPageLeaderboard(stat, size);
  }

  @Receptor("page-groups")
  public long getMaxPageGroups(@ParamName("size") int size) {
    return DataLoaderReceptors.loader().maxPageGroups(size);
  }

  @Receptor("link-punishments")
  public Collection<Punishment> getPunishments(
      @ParamName("link") LinkableInfo link, @ParamName("status") PunishmentStatus[] statuses) {
    return DataLoaderReceptors.loader().getPunishments(link, statuses);
  }
}
