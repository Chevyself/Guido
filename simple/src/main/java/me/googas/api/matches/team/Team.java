package me.googas.api.matches.team;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.api.GuidoCatchable;
import me.googas.api.events.match.team.TeamDataUnloadedEvent;
import me.googas.api.lang.Localized;
import me.googas.api.links.Linkable;
import me.googas.api.matches.queue.Queueable;

/** This is a team which can be saved to the database */
public class Team implements GuidoCatchable, Localized, Queueable {

  @NonNull @Getter private final String id;
  @NonNull @Getter private final Set<TeamMember> members;
  @NonNull @Getter @Setter private String name;

  /**
   * Create the team
   *
   * @param id the id of the team
   * @param members the members of the team
   * @param name the name of the team
   */
  public Team(@NonNull String id, @NonNull Set<TeamMember> members, @NonNull String name) {
    this.id = id;
    this.members = members;
    this.name = name;
  }

  /**
   * Add the member to the team
   *
   * @param member the member to add
   * @return whether the member was added
   */
  public boolean add(@NonNull TeamMember member) {
    return this.getMembers().add(member);
  }

  /**
   * Removes a member from the team
   *
   * @param member the member to remove
   * @return whether the user was removed
   */
  public boolean remove(@NonNull TeamMember member) {
    return this.getMembers().removeIf(teamMember -> member.getLink().compare(teamMember.getLink()));
  }

  /**
   * Check whether the link is a member of this team
   *
   * @param linkable the link to check if it is a member of the team
   * @return true if it is in this team
   */
  public boolean contains(Linkable linkable) {
    if (linkable == null) return false;
    for (TeamMember member : this.getMembers()) {
      if (member.getLink().compare(linkable)) return true;
    }
    return false;
  }

  @Override
  public @NonNull String getSingle() {
    return this.name;
  }

  @Override
  public @NonNull Map<String, Map<String, Float>> getStats() {
    // TODO implement
    return new HashMap<>();
  }

  @Override
  public void sendMessage(@NonNull String message) {
    for (TeamMember member : this.getMembers()) {
      Linkable link = member.getLink().getLink();
      if (link == null) return;
      link.sendMessage(message);
    }
  }

  @Override
  public void sendLocalized(@NonNull String key) {
    for (TeamMember member : this.getMembers()) {
      Linkable link = member.getLink().getLink();
      if (link == null) return;
      link.sendLocalized(key);
    }
  }

  @Override
  public void sendLocalized(@NonNull String key, @NonNull Map<String, String> placeholders) {
    for (TeamMember member : this.getMembers()) {
      Linkable link = member.getLink().getLink();
      if (link == null) return;
      link.sendLocalized(key, placeholders);
    }
  }

  @Override
  @NonNull
  public String getLang() {
    return "en";
  }

  @Override
  public void setLang(@NonNull String lang) {
    throw new UnsupportedOperationException("Cannot change the lang of a team");
  }

  @Override
  public void onRemove() {
    new TeamDataUnloadedEvent(this).call();
  }
}
