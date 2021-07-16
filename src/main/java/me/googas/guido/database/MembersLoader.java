package me.googas.guido.database;

import lombok.NonNull;
import me.googas.guido.MemberStats;
import net.dv8tion.jda.api.entities.Member;

public interface MembersLoader {

  /**
   * Get the the stats of a {@link net.dv8tion.jda.api.entities.Member} if it does not exist create
   * one
   *
   * @param id {@link Member#getIdLong()}
   * @param guild the id of the {@link Member#getGuild()}
   * @return the member stats if found else null
   */
  MemberStats getMemberStats(long id, long guild);

  boolean createMemberStats(@NonNull MemberStats stats);

    MemberStats getMemberStats(Member member);
}
