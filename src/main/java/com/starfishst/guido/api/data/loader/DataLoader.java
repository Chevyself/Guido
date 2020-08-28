package com.starfishst.guido.api.data.loader;

import com.starfishst.guido.api.data.GuildData;
import com.starfishst.guido.api.data.MemberData;
import com.starfishst.guido.api.data.RoleData;
import net.dv8tion.jda.api.entities.Guild;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Loads the data.
 */
public interface DataLoader {

    /**
     * Load the data of a guild
     *
     * @param id the id of the guild
     * @return the data of the guild or null if not found
     */
    @Nullable
    GuildData getGuildData(long id);

    /**
     * Load the data of a member
     *
     * @param id the id of the member
     * @param guild the guild from which the data of the member must be gotten
     * @return the data of the member or null if not found
     */
    @Nullable
    MemberData getMemberData(long id, @NotNull Guild guild);

    /**
     * Load the data of a role
     *
     * @param id the id of the role
     * @param guild the guild from which the data of the role must be gotten
     * @return the data of the role or null if not found
     */
    @Nullable
    RoleData getRoleData(long id, @NotNull Guild guild);

}
