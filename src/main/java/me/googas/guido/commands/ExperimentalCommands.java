package me.googas.guido.commands;

import me.googas.commands.annotations.Optional;
import me.googas.commands.annotations.Parent;
import me.googas.commands.annotations.Required;
import me.googas.commands.jda.annotations.Command;
import me.googas.commands.jda.result.Result;
import me.googas.commands.jda.result.ResultType;
import me.googas.guido.GuidoBot;
import me.googas.guido.MemberStats;
import me.googas.guido.database.MembersLoader;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.Permission;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;

public class ExperimentalCommands {

    @Parent
    @Command(aliases = "experimental", description = "Parent command which differentiates experimental to production commands", permission = Permission.ADMINISTRATOR)
    public Result experimental() {
        return new Result("This command is reserved to quick tests you can try with any of the subcommands");
    }

    @Command(aliases = "register", description = "Register to the elo system")
    public Result register(
            Member member, @Required(name = "nickname", description = "Minecraft nickname") String nick) {
        MembersLoader members = GuidoBot.getLoader().getMembers();
        MemberStats memberStats = members.getMemberStats(member);
        if (memberStats != null) return new Result(ResultType.USAGE, "You are registered");
        if (members.createMemberStats(new MemberStats(member))) {
            return new Result("You have been registered!");
        } else {
            return new Result("You could not be registered");
        }
    }


    @Command(aliases = "current", description = "Check you elo or someone else's")
    public Result current(
            JDA jda,
            Member sender,
            @Optional(name = "who", description = "Who do you want to check elo from") MemberStats member) {
        boolean isSelf = member == null;
        if (isSelf) {
            MemberStats stats = GuidoBot.getLoader().getMembers().getMemberStats(sender);
            if (stats == null) {
                return new Result(ResultType.USAGE, "You must register first");
            } else {
                return new Result("You have " + stats.getElo() + " elo");
            }

        } else {
            String mention = String.valueOf(member.getId());
            Guild guild = jda.getGuildById(member.getGuild());
            if (guild != null) {
                Member guildMember = guild.getMemberById(member.getId());
                if (guildMember != null) mention = guildMember.getAsMention();
            }
            return new Result(mention + " has " + member.getElo() + " elo");
        }
    }

}
