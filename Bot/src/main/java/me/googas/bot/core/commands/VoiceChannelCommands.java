package me.googas.bot.core.commands;

import com.starfishst.core.annotations.Parent;
import com.starfishst.core.annotations.Required;
import com.starfishst.jda.annotations.Command;
import com.starfishst.jda.annotations.Perm;
import com.starfishst.jda.result.Result;
import com.starfishst.jda.result.ResultType;
import me.googas.api.lang.LocaleFile;
import me.googas.bot.api.types.BotGuild;
import me.googas.commons.Strings;
import me.googas.commons.maps.Maps;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.VoiceChannel;

import java.util.Map;

/**
 * Set the voice channels of a g uild
 */
public class VoiceChannelCommands {

    /**
     * See all the channels from a guild
     *
     * @param locale the locale of the sender of the message
     * @param guild the actual discord guild
     * @param botGuild the data of the guild
     * @return the channels in the guild
     */
    @Parent
    @Command(
            aliases = {"voiceChannels", "vc"},
            description = "vc.desc",
            permission = @Perm(node = "guido.channels"))
    public Result categories(LocaleFile locale, Guild guild, BotGuild botGuild) {
        Map<String, String> placeholders = Maps.singleton("name", guild.getName());
        Map<String, Long> voiceChannels = botGuild.getVoiceChannels();
        if (voiceChannels.isEmpty()) {
            return new Result(locale.get("vc.empty", placeholders));
        } else {
            StringBuilder builder = Strings.getBuilder();
            builder.append(locale.get("vc.title", placeholders));
            voiceChannels.forEach(
                    (key, channelId) -> {
                        VoiceChannel channel = guild.getVoiceChannelById(channelId);
                        builder.append(
                                locale.get(
                                        "vc.channel",
                                        Maps.builder("key", key)
                                                .append(
                                                        "name",
                                                        channel == null ? String.valueOf(channelId) : channel.getName())));
                    });
            return new Result(builder.toString());
        }
    }

    /**
     * Set the channels in the guild
     *
     * @param locale the locale of the command sender
     * @param guild the guild data
     * @param member the member to get the voice status
     * @param key the key of the channel to set
     * @return whether the channel was set or unset
     */
    @Command(
            aliases = "set",
            description = "vc.set.desc",
            permission = @Perm(node = "guido.channels.set"))
    public Result set(
            LocaleFile locale,
            BotGuild guild,
            Member member,
            @Required(name = "vc.set.key", description = "vc.set.key.desc") String key) {
        GuildVoiceState state = member.getVoiceState();
        if (state != null) {
            VoiceChannel channel = state.getChannel();
            if (channel != null) {
                return new Result(
                        locale.get(
                                "vc.set.success", Maps.builder("key", key).append("name", channel.getName())));
            }
        }
        return new Result(ResultType.USAGE, locale.get("vc.set.connect"));
    }

}
