package me.googas.guido;

import lombok.NonNull;
import net.dv8tion.jda.api.entities.IMentionable;

import java.util.ArrayList;
import java.util.List;

public class Discord {

    @NonNull
    public static List<String> getMentions(@NonNull List<? extends IMentionable> mentionables) {
        List<String> mentions = new ArrayList<>(mentionables.size());
        for (IMentionable mentionable : mentionables) {
            mentions.add(mentionable.getAsMention());
        }
        return mentions;
    }

    @NonNull
    public static String pretty(@NonNull List<? extends IMentionable> mentionables) {
        return getMentions(mentionables).toString().replace("[", "").replace("]", "");
    }

}
