export type PlayerStatsResponse = {
	uuid: string;
	nickname: string;
	context: string;
	linked: boolean;
	online: boolean;
	stats: Record<string, number>;
};

export type LeaderboardRow = {
	rank: number;
	display: string;
	value: number;
	wins: number;
	losses: number;
};

export const EMPTY_CONTEXT = 'no-context';

export const LADDER_ELO_SUFFIX = '-elo';
export const LADDER_WINS_SUFFIX = '-wins';
export const LADDER_LOSES_SUFFIX = '-loses';
