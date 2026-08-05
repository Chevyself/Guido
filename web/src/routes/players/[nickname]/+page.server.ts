import { error } from '@sveltejs/kit';
import { getPlayerStatsByNickname } from '$lib/mongo/stats';
import { PlayerNotFoundError } from '$lib/mongo/errors';
import { parseStatsMap } from '$lib/stats/parse';
import type { PageServerLoad } from './$types';

export const load: PageServerLoad = async ({ params }) => {
	const nickname = params.nickname.trim();
	if (!nickname) {
		error(404, 'Player not found');
	}

	try {
		const player = await getPlayerStatsByNickname(nickname);
		const parsed = parseStatsMap(player.stats);

		return {
			player,
			ladders: parsed.ladders,
			other: parsed.other
		};
	} catch (e) {
		if (e instanceof PlayerNotFoundError) {
			error(404, 'Player not found');
		}
		throw e;
	}
};
