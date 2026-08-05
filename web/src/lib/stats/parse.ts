export type LadderRow = {
	ladder: string;
	elo: number;
	wins: number;
	losses: number;
	played: number;
};

const LADDER_SUFFIXES = {
	elo: '-elo',
	wins: '-wins',
	losses: '-loses',
	played: '-played'
} as const;

export function parseStatsMap(map: Record<string, number>): {
	ladders: LadderRow[];
	other: { key: string; value: number }[];
} {
	const ladderData = new Map<string, Partial<LadderRow>>();

	for (const [key, value] of Object.entries(map)) {
		for (const [field, suffix] of Object.entries(LADDER_SUFFIXES)) {
			if (!key.endsWith(suffix)) continue;

			const ladderName = key.slice(0, -suffix.length);
			const row = ladderData.get(ladderName) ?? { ladder: ladderName };
			row[field as keyof Omit<LadderRow, 'ladder'>] = value;
			ladderData.set(ladderName, row);
			break;
		}
	}

	const ladders: LadderRow[] = [...ladderData.entries()]
		.map(([ladder, row]) => ({
			ladder,
			elo: row.elo ?? 0,
			wins: row.wins ?? 0,
			losses: row.losses ?? 0,
			played: row.played ?? 0
		}))
		.sort((a, b) => a.ladder.localeCompare(b.ladder));

	const other = Object.entries(map)
		.filter(([key]) => !isLadderKey(key))
		.map(([key, value]) => ({ key, value }))
		.sort((a, b) => a.key.localeCompare(b.key));

	return { ladders, other };
}

function isLadderKey(key: string): boolean {
	return Object.values(LADDER_SUFFIXES).some((suffix) => key.endsWith(suffix));
}

export function winLossRatio(wins: number, losses: number): string {
	if (wins === 0 && losses === 0) return '—';
	if (losses === 0) return wins.toString();
	return (wins / losses).toFixed(2);
}
