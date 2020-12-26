package me.googas.bot.core.commands.providers;

import com.starfishst.core.exceptions.ArgumentProviderException;
import com.starfishst.jda.context.CommandContext;
import com.starfishst.jda.providers.type.JdaArgumentProvider;
import lombok.NonNull;
import me.googas.api.links.ref.DiscordGuildLinkable;
import me.googas.bot.Guido;
import net.dv8tion.jda.api.entities.Member;

public class DiscordGuildLinkableProvider implements JdaArgumentProvider<DiscordGuildLinkable> {
  @Override
  public @NonNull Class<DiscordGuildLinkable> getClazz() {
    return DiscordGuildLinkable.class;
  }

  @Override
  public @NonNull DiscordGuildLinkable fromString(
      @NonNull String s, @NonNull CommandContext commandContext) throws ArgumentProviderException {
    Member member = commandContext.get(s, Member.class, commandContext);
    return new DiscordGuildLinkable(
        Guido.getDataLoader().getMemberData(member.getIdLong(), member.getGuild().getIdLong()));
  }
}
