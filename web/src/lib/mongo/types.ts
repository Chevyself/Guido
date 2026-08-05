import type { Binary, Long, UUID } from 'mongodb';

export type MinecraftLinkDoc = {
	_id: UUID | Binary | string;
	nickname: string;
	ip?: string;
	linkedUserId?: UUID | Binary | string | null;
	online?: boolean;
};

export type StatsIdDoc = {
	linkableId: UUID | Binary | string;
	context: string;
};

export type StatsDoc = {
	_id: StatsIdDoc;
	values?: Record<string, number>;
};

export type LadderDoc = {
	name: string;
	playersPerTeam?: number;
	baseValue?: number;
	teamsPerMatch?: number;
	winMultiplier?: number;
	loseMultiplier?: number;
	teamSelectionType?: string;
};

export type GuildDoc = {
	_id: Long | number | string;
	ladders?: LadderDoc[];
};

export type LadderInfo = {
	name: string;
};

export type LeaderboardAggregationRow = {
	display: string;
	value: number;
	wins?: number;
	loses?: number;
};
