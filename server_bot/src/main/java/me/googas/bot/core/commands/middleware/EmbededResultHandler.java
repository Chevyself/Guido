package me.googas.bot.core.commands.middleware;

import com.github.chevyself.starbox.jda.context.CommandContext;
import com.github.chevyself.starbox.middleware.Middleware;
import com.github.chevyself.starbox.result.Result;
import me.googas.bot.core.commands.types.EmbededResult;
import org.jetbrains.annotations.NotNull;

public class EmbededResultHandler implements Middleware<CommandContext> {

    @Override
    public void next(@NotNull CommandContext context, Result result) {
        if (result instanceof EmbededResult embeded) {
            context.getChannel().ifPresent(channel -> channel.sendMessageEmbeds(embeded.build()).queue());
        }
    }
}
