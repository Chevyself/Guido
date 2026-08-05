import { UUID, type Binary } from 'mongodb';
import { getDb } from './client';
import { COLLECTIONS } from './env';
import { PlayerNotFoundError } from './errors';
import { getLadderByName } from './guild';
import type { LeaderboardAggregationRow, MinecraftLinkDoc, StatsDoc } from './types';
import {
	EMPTY_CONTEXT,
	LADDER_ELO_SUFFIX,
	LADDER_LOSES_SUFFIX,
	LADDER_WINS_SUFFIX,
	type LeaderboardRow,
	type PlayerStatsResponse
} from '$lib/stats/types';

export const LADDER_LEADERBOARD_PAGE_SIZE = 10;
export const STAT_RANKING_PAGE_SIZE = 20;

function toUuidString(value: UUID | Binary | string | undefined | null): string {
	if (value == null) return '';
	if (value instanceof UUID) return value.toString();
	if (typeof value === 'string') return value;
	if (typeof value === 'object' && 'toUUID' in value && typeof value.toUUID === 'function') {
		return value.toUUID().toString();
	}
	if (typeof value === 'object' && 'sub_type' in value && Buffer.isBuffer((value as Binary).buffer)) {
		return new UUID((value as Binary).buffer).toString();
	}
	return String(value);
}

export async function getPlayerStatsByNickname(
	nickname: string,
	context = EMPTY_CONTEXT
): Promise<PlayerStatsResponse> {
	const db = await getDb();
	const link = await db.collection<MinecraftLinkDoc>(COLLECTIONS.minecraftLinks).findOne({
		nickname: { $regex: nickname }
	});

	if (!link) {
		throw new PlayerNotFoundError();
	}

	const uuid = toUuidString(link._id);
	const statsDoc = await db.collection<StatsDoc>(COLLECTIONS.stats).findOne({
		'_id.linkableId': link._id,
		'_id.context': context
	});

	return {
		uuid,
		nickname: link.nickname,
		context,
		linked: link.linkedUserId != null,
		online: link.online ?? false,
		stats: statsDoc?.values ?? {}
	};
}

async function internalGetLeaderboard(
	context: string,
	sortKey: string,
	page: number,
	limit: number,
	includeWinsLosses: boolean
): Promise<LeaderboardRow[]> {
	const db = await getDb();
	const valueField = `$values.${sortKey}`;
	const winsField = includeWinsLosses ? `$values.${sortKey.replace(LADDER_ELO_SUFFIX, LADDER_WINS_SUFFIX)}` : 0;
	const losesField = includeWinsLosses
		? `$values.${sortKey.replace(LADDER_ELO_SUFFIX, LADDER_LOSES_SUFFIX)}`
		: 0;

	const pipeline = [
		{ $match: { '_id.context': context } },
		{ $sort: { [`values.${sortKey}`]: -1 } },
		{ $skip: page * limit },
		{ $limit: limit },
		{
			$lookup: {
				from: COLLECTIONS.minecraftLinks,
				localField: '_id.linkableId',
				foreignField: '_id',
				as: 'minecraft'
			}
		},
		{ $unwind: '$minecraft' },
		{
			$project: {
				_id: 0,
				display: '$minecraft.nickname',
				value: valueField,
				wins: winsField,
				loses: losesField
			}
		}
	];

	const rows = await db
		.collection<StatsDoc>(COLLECTIONS.stats)
		.aggregate<LeaderboardAggregationRow>(pipeline)
		.toArray();

	return rows.map((row, index) => ({
		rank: page * limit + index + 1,
		display: row.display,
		value: row.value ?? 0,
		wins: row.wins ?? 0,
		losses: row.loses ?? 0
	}));
}

export async function getLeaderboardByLadder(
	ladderName: string,
	page: number,
	context = EMPTY_CONTEXT
): Promise<{ ladder: string; page: number; rows: LeaderboardRow[]; hasNext: boolean }> {
	await getLadderByName(ladderName);
	const sortKey = `${ladderName}${LADDER_ELO_SUFFIX}`;
	const safePage = Math.max(0, page);
	const rows = await internalGetLeaderboard(
		context,
		sortKey,
		safePage,
		LADDER_LEADERBOARD_PAGE_SIZE,
		true
	);

	return {
		ladder: ladderName,
		page: safePage,
		rows,
		hasNext: rows.length === LADDER_LEADERBOARD_PAGE_SIZE
	};
}

export async function getRankingByStat(
	stat: string,
	page: number,
	context = EMPTY_CONTEXT
): Promise<{ stat: string; page: number; rows: LeaderboardRow[]; hasNext: boolean }> {
	const safePage = Math.max(0, page);
	const rows = await internalGetLeaderboard(
		context,
		stat,
		safePage,
		STAT_RANKING_PAGE_SIZE,
		false
	);

	return {
		stat,
		page: safePage,
		rows,
		hasNext: rows.length === STAT_RANKING_PAGE_SIZE
	};
}

export async function countStatsForContext(context = EMPTY_CONTEXT): Promise<number> {
	const db = await getDb();
	return db.collection<StatsDoc>(COLLECTIONS.stats).countDocuments({ '_id.context': context });
}
