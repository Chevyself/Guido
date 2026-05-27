package com.starfishst.bukkit.dependencies.events;

public
class GuidoTeamParser /*implements TeamParser TODO team parser do not longer exists gotta figure it out whats the new way of registering teams on Events */ {

  /** Enables the team parser to be used in the event plugin */
  public static void enable() {
    // EventsPl.get().setTeamParser(new GuidoTeamParser());
  }

  /*
  @Override
  public TournamentTeam getTeam(String s) {
     TODO teams requests have not been implemented
    if (s == null) return null;
    try {
      Team data = new BukkitRequest<>(Team.class, "team-by-name", Maps.singleton("name", s)).send();
      if (data != null) {
        return GuidoTournamentTeam.parse(data);
      }
    } catch (MessengerListenFailException e) {
      e.printStackTrace();
    }

    return null;
  }

  @Override
  public Collection<TournamentTeam> getTeams() {
    return new ArrayList<>();
  }*/
}
