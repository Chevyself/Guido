import {
	GUIDO_GUILD_ID,
	MONGO_DATABASE,
	MONGO_URI
} from '$env/static/private';
import { Long } from 'mongodb';

const DEFAULT_GUILD_ID = '1511402659767128291';

export const mongoEnv = {
	uri: MONGO_URI || 'mongodb://localhost:27017',
	database: MONGO_DATABASE || 'guido',
	guildId: (GUIDO_GUILD_ID || DEFAULT_GUILD_ID).trim()
};

/** Discord snowflake IDs exceed Number.MAX_SAFE_INTEGER — must use BSON Long for queries. */
export function getGuildIdLong(): Long {
	return Long.fromString(mongoEnv.guildId);
}

export const COLLECTIONS = {
	minecraftLinks: 'minecraft-links',
	stats: 'stats',
	guilds: 'guilds'
} as const;
