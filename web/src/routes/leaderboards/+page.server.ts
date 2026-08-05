import { getLadders } from '$lib/mongo/guild';
import type { PageServerLoad } from './$types';

export const load: PageServerLoad = async () => {
	const ladders = await getLadders();
	return { ladders };
};
