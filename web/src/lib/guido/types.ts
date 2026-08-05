export type { PlayerStatsResponse } from '$lib/stats/types';

export type GuidoRequest = {
	id: string;
	method: string;
	parameters: Record<string, unknown>;
};

export type GuidoResponse<T> = {
	id: string;
	object: T | { cause: string } | null;
	error: boolean;
};

export class GuidoError extends Error {
	constructor(
		message: string,
		public readonly code?: string
	) {
		super(message);
		this.name = 'GuidoError';
	}
}

export class GuidoNotFoundError extends GuidoError {
	constructor(message = 'Player not found') {
		super(message, 'stats.player-not-found');
		this.name = 'GuidoNotFoundError';
	}
}
