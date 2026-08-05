import { getDb } from './client';
import { COLLECTIONS, getGuildIdLong } from './env';
import { LadderNotFoundError } from './errors';
import type { GuildDoc, LadderDoc, LadderInfo } from './types';

const GLOBAL_LADDER_NAME = 'global';

export async function getLadders(): Promise<LadderInfo[]> {
	const db = await getDb();
	const guild = await db
		.collection<GuildDoc>(COLLECTIONS.guilds)
		.findOne({ _id: getGuildIdLong() });

	if (!guild?.ladders?.length) {
		return [];
	}

	return guild.ladders
		.filter((ladder) => ladder.name && ladder.name.toLowerCase() !== GLOBAL_LADDER_NAME)
		.map((ladder) => ({ name: ladder.name }))
		.sort((a, b) => a.name.localeCompare(b.name));
}

export async function getLadderByName(name: string): Promise<LadderDoc> {
	const ladders = await getLadders();
	const ladder = ladders.find((entry) => entry.name.toLowerCase() === name.toLowerCase());

	if (!ladder) {
		throw new LadderNotFoundError(`Ladder "${name}" not found`);
	}

	const db = await getDb();
	const guild = await db
		.collection<GuildDoc>(COLLECTIONS.guilds)
		.findOne({ _id: getGuildIdLong() });
	const doc = guild?.ladders?.find((entry) => entry.name.toLowerCase() === name.toLowerCase());

	if (!doc) {
		throw new LadderNotFoundError(`Ladder "${name}" not found`);
	}

	return doc;
}
