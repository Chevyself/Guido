package me.googas.bot.core.commands;

import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.annotations.Perm;
import com.starfishst.jda.result.Result;
import java.util.Set;
import me.googas.api.matches.Team;
import me.googas.api.matches.TeamRole;
import me.googas.bot.api.types.BotLinkableData;
import me.googas.bot.core.Guido;
import me.googas.bot.core.types.GuidoMatch;
import me.googas.bot.core.types.GuidoTeam;
import me.googas.bot.core.types.GuidoTeamMember;
import me.googas.bot.core.types.maps.GuidoLinkedValuesMap;
import me.googas.bot.core.util.Discord;
import me.googas.commons.Lots;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Member;

public class TestCommands {

  @Command(aliases = "test", permission = @Perm(node = "user:guido.test"))
  public void test(Member member) {
    Discord.removeAllPermission(member.getVoiceState().getChannel(), Permission.VIEW_CHANNEL);
  }
}
