import { error } from '@sveltejs/kit';
import { getLeaderboardByLadder } from '$lib/mongo/stats';
import { LadderNotFoundError } from '$lib/mongo/errors';
import type { PageServerLoad } from './$types';

export const load: PageServerLoad = async ({ params, url }) => {
	const ladder = params.ladder.trim();
	if (!ladder) {
		error(404, 'Ladder not found');
	}

	const page = Math.max(0, Number(url.searchParams.get('page') ?? '0') || 0);

	try {
		return await getLeaderboardByLadder(ladder, page);
	} catch (e) {
		if (e instanceof LadderNotFoundError) {
			error(404, 'Ladder not found');
		}
		throw e;
	}
};
