package me.googas.guido;

import java.util.StringJoiner;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import me.googas.net.cache.Catchable;
import me.googas.starbox.time.Time;
import me.googas.starbox.time.unit.Unit;
import net.dv8tion.jda.api.entities.Member;

public class MemberStats implements Catchable {

  @Getter private final long id;
  @Getter private final long guild;
  @Getter @Setter private int elo;
  @Getter @Setter private int wins;
  @Getter @Setter private int loses;

  public MemberStats(long id, long guild, int elo, int wins, int loses) {
    this.id = id;
    this.guild = guild;
    this.elo = elo;
    this.wins = wins;
    this.loses = loses;
  }

  public MemberStats(@NonNull Member member) {
    this(member.getIdLong(), member.getGuild().getIdLong(), 0, 0, 0);
  }

    @Override
  public @NonNull Time getToRemove() {
    return Time.of(3, Unit.MINUTES);
  }

  @Override
  public String toString() {
    return new StringJoiner(", ", MemberStats.class.getSimpleName() + "[", "]")
        .add("id=" + this.id)
        .add("guild=" + this.guild)
        .add("elo=" + this.elo)
        .add("wins=" + this.wins)
        .add("loses=" + this.loses)
        .toString();
  }
}
