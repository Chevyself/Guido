package com.starfishst.bot.api.data;

import com.starfishst.bot.handlers.data.GuidoPermission;
import com.starfishst.guido.api.data.links.LinkedData;

/** An extension for linked data to use in the bot */
public interface BotLinkedData extends LinkedData<GuidoPermission>, BotPermissible {}
