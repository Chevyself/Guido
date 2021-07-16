package me.googas.guido.commands.providers.experimental;

import lombok.NonNull;
import me.googas.commands.exceptions.ArgumentProviderException;
import me.googas.commands.jda.context.CommandContext;
import me.googas.commands.jda.providers.type.JdaArgumentProvider;
import me.googas.guido.GuidoBot;
import me.googas.guido.MemberStats;
import net.dv8tion.jda.api.entities.Member;

public class MemberStatsProvider implements JdaArgumentProvider<MemberStats> {
    @Override
    public @NonNull Class<MemberStats> getClazz() {
        return MemberStats.class;
    }

    @Override
    public @NonNull MemberStats fromString(@NonNull String s, @NonNull CommandContext context) throws ArgumentProviderException {
        Member member = context.get(s, Member.class, context);
        MemberStats stats = GuidoBot.getLoader().getMembers().getMemberStats(member);
        if (stats == null) {
            throw new ArgumentProviderException(member.getAsMention() + " has not been registered");
        }
        return stats;
    }
}
