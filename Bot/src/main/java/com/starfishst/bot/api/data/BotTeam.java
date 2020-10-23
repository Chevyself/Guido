package com.starfishst.bot.api.data;

import com.starfishst.guido.api.data.matches.Team;
import com.starfishst.guido.api.data.matches.TeamRole;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/** An extension of team */
public interface BotTeam extends Team {

  @Override
  @NotNull
  Map<? extends BotLinkedInfo, TeamRole> getMembers();

  /**
   * Get the single identification for all the members
   *
   * @return the single identification
   */
  default Collection<String> getMemberSingles() {
    List<String> singles = new ArrayList<>();
    for (BotLinkedInfo info : this.getMembers().keySet()) {
      BotLinkedData data = info.getData();
      if (data != null) {
        singles.add(data.getSingle());
      }
    }
    return singles;
  }
}
