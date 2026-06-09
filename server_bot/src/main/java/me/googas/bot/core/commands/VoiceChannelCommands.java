package me.googas.bot.core.commands;

import com.github.chevyself.starbox.annotations.Command;
import com.github.chevyself.starbox.annotations.Required;
import com.github.chevyself.starbox.result.Result;
import me.googas.api.lang.LocaleFile;
import me.googas.api.matches.ladder.Ladder;
import me.googas.api.utility.Maps;
import me.googas.bot.core.commands.middleware.GuidoJdaPermission;
import me.googas.server.GuidoGuild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.channel.unions.AudioChannelUnion;

@Command(aliases = "vc")
public class VoiceChannelCommands {
  @GuidoJdaPermission("guido.channels.set")
  @Command(aliases = "set", description = "vc.set.desc")
  public Result set(
      LocaleFile locale,
      GuidoGuild guild,
      Member member,
      @Required(name = "vc.set.ladder", description = "vc.set.key.ladder") Ladder ladder) {
    GuildVoiceState state = member.getVoiceState();
    if (state == null) {
      return Result.of(locale.get("vc.set.connect"));
    }
    AudioChannelUnion channel = state.getChannel();
    if (channel == null) {
      return Result.of(locale.get("vc.set.connect"));
    }
    guild.setLadderToJoin(channel.getIdLong(), ladder);
    return Result.of(
        locale.get(
            "vc.set.success",
            Maps.builder("ladder", ladder.getName()).put("name", channel.getName())));
  }
}
