import { getRankingByStat } from '$lib/mongo/stats';
import type { PageServerLoad } from './$types';

export const load: PageServerLoad = async ({ params, url }) => {
	const stat = params.stat.trim();
	const page = Math.max(0, Number(url.searchParams.get('page') ?? '0') || 0);
	return getRankingByStat(stat, page);
};
