package me.googas.guido.database.sql;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import lombok.Getter;
import lombok.NonNull;
import me.googas.guido.MemberStats;
import me.googas.guido.database.MembersLoader;
import net.dv8tion.jda.api.entities.Member;

public class SqlMembersLoader implements MembersLoader, SqlDataLoader {

  @NonNull @Getter private final SqlLoader loader;

  public SqlMembersLoader(@NonNull SqlLoader loader) {
    this.loader = loader;
  }

  @Override
  public @NonNull SqlMembersLoader initialize() throws SQLException {
    if (this.loader.hasConnection()) {
      PreparedStatement statement = this.loader.statementOf("members.create-table");
      statement.execute();
    }
    return this;
  }

  @Override
  public MemberStats getMemberStats(long id, long guild) {
    MemberStats memberStats =
        this.loader
            .getCache()
            .get(MemberStats.class, stats -> stats.getId() == id && stats.getGuild() == guild);
    if (memberStats == null) {
      try {
        PreparedStatement statement = this.loader.statementOf("members.stats");
        statement.setLong(1, id);
        statement.setLong(2, guild);
        ResultSet resultSet = statement.executeQuery();
        while (resultSet.next()) {
          memberStats =
              new MemberStats(
                  id,
                  guild,
                  resultSet.getInt("elo"),
                  resultSet.getInt("wins"),
                  resultSet.getInt("loses"));
          this.loader.getCache().add(memberStats);
        }
      } catch (SQLException e) {
        e.printStackTrace();
      }
    }
    return memberStats;
  }

  @Override
  public boolean createMemberStats(@NonNull MemberStats stats) {
    try {
      PreparedStatement statement =
          this.loader.statementOf("members.create-stats", stats.getId(), stats.getGuild());
      int update = statement.executeUpdate();
      return update == 1;
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public MemberStats getMemberStats(Member member) {
    return this.getMemberStats(member.getIdLong(), member.getGuild().getIdLong());
  }
}
