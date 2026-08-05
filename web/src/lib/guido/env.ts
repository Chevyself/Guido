import { GUIDO_HOST, GUIDO_PORT, GUIDO_TOKEN } from '$env/static/private';

export const guidoEnv = {
	host: GUIDO_HOST || 'localhost',
	port: Number(GUIDO_PORT || '3366'),
	token: GUIDO_TOKEN || '',
	timeoutMs: 10_000
};
