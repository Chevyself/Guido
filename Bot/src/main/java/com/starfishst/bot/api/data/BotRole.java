package com.starfishst.bot.api.data;

import com.starfishst.bot.handlers.data.GuidoPermission;
import com.starfishst.guido.api.data.discord.RoleData;

/** A role data for the bot */
public interface BotRole extends RoleData<GuidoPermission>, BotPermissible {}
