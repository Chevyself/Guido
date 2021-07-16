package me.googas.guido.commands.providers.experimental;

import lombok.NonNull;
import me.googas.commands.exceptions.ArgumentProviderException;
import me.googas.commands.jda.context.CommandContext;
import me.googas.commands.jda.context.GuildCommandContext;
import me.googas.commands.jda.providers.type.JdaExtraArgumentProvider;
import me.googas.guido.GuidoBot;
import me.googas.guido.MemberStats;

public class MemberStatsSenderProvider implements JdaExtraArgumentProvider<MemberStats> {
    @Override
    public @NonNull Class<MemberStats> getClazz() {
        return MemberStats.class;
    }

    @Override
    public @NonNull MemberStats getObject(@NonNull CommandContext context) throws ArgumentProviderException {
        if (context instanceof GuildCommandContext) {
            MemberStats stats = GuidoBot.getLoader().getMembers().getMemberStats(((GuildCommandContext) context).getMember());
            if (stats != null) return stats;
            throw new ArgumentProviderException("You haven't registered yet");
        }
        throw new ArgumentProviderException(context.getMessagesProvider().guildOnly(context));
    }
}
