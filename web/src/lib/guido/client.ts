import { connect, type Socket } from 'node:net';
import { randomUUID } from 'node:crypto';
import { guidoEnv } from './env';
import {
	GuidoError,
	GuidoNotFoundError,
	type GuidoRequest,
	type GuidoResponse
} from './types';

type PendingRequest = {
	resolve: (value: unknown) => void;
	reject: (error: Error) => void;
};

let socket: Socket | null = null;
let lineBuffer = '';
let messageLines: string[] = [];
let authenticated = false;
let connectPromise: Promise<void> | null = null;
const pending = new Map<string, PendingRequest>();

function resetConnection(): void {
	authenticated = false;
	socket = null;
	connectPromise = null;
	lineBuffer = '';
	messageLines = [];
	for (const [, handler] of pending) {
		handler.reject(new GuidoError('Connection closed'));
	}
	pending.clear();
}

function sendLine(payload: string): void {
	if (!socket || socket.destroyed) {
		throw new GuidoError('Not connected to Guido');
	}
	socket.write(`${payload}\n---\n`, 'utf8');
}

function handleIncomingMessage(raw: string): void {
	const trimmed = raw.trim();
	if (!trimmed) return;

	let parsed: GuidoResponse<unknown>;
	try {
		parsed = JSON.parse(trimmed) as GuidoResponse<unknown>;
	} catch {
		return;
	}

	const handler = pending.get(parsed.id);
	if (!handler) return;

	pending.delete(parsed.id);

	if (parsed.error) {
		const cause = extractErrorCause(parsed.object);
		if (cause === 'stats.player-not-found') {
			handler.reject(new GuidoNotFoundError(cause));
			return;
		}
		handler.reject(new GuidoError(cause, cause));
		return;
	}

	handler.resolve(parsed.object);
}

function extractErrorCause(object: GuidoResponse<unknown>['object']): string {
	if (
		object &&
		typeof object === 'object' &&
		'cause' in object &&
		typeof (object as { cause: unknown }).cause === 'string'
	) {
		return (object as { cause: string }).cause;
	}
	return 'Guido request failed';
}

function onSocketData(chunk: Buffer): void {
	lineBuffer += chunk.toString('utf8');

	let newlineIndex = lineBuffer.indexOf('\n');
	while (newlineIndex !== -1) {
		const line = lineBuffer.slice(0, newlineIndex);
		lineBuffer = lineBuffer.slice(newlineIndex + 1);

		if (line.trim() === '---') {
			if (messageLines.length > 0) {
				handleIncomingMessage(messageLines.join('\n'));
				messageLines = [];
			}
		} else {
			messageLines.push(line);
		}

		newlineIndex = lineBuffer.indexOf('\n');
	}
}

function rawRequest<T>(
	method: string,
	parameters: Record<string, unknown>,
	timeoutMs = guidoEnv.timeoutMs
): Promise<T> {
	return new Promise((resolve, reject) => {
		if (!socket || socket.destroyed) {
			reject(new GuidoError('Not connected to Guido'));
			return;
		}

		const id = randomUUID();
		const request: GuidoRequest = { id, method, parameters };

		const timer = setTimeout(() => {
			pending.delete(id);
			reject(new GuidoError(`Request timed out after ${timeoutMs}ms`));
		}, timeoutMs);

		pending.set(id, {
			resolve: (value) => {
				clearTimeout(timer);
				resolve(value as T);
			},
			reject: (error) => {
				clearTimeout(timer);
				reject(error);
			}
		});

		try {
			sendLine(JSON.stringify(request));
		} catch (error) {
			clearTimeout(timer);
			pending.delete(id);
			reject(error instanceof Error ? error : new GuidoError(String(error)));
		}
	});
}

async function connectSocket(): Promise<void> {
	if (socket && !socket.destroyed) return;

	await new Promise<void>((resolve, reject) => {
		const nextSocket = connect(
			{ host: guidoEnv.host, port: guidoEnv.port },
			() => resolve()
		);

		nextSocket.setEncoding('utf8');
		nextSocket.on('data', onSocketData);
		nextSocket.on('error', (error) => {
			resetConnection();
			reject(error);
		});
		nextSocket.on('close', () => {
			resetConnection();
		});

		socket = nextSocket;
	});
}

async function ensureAuthenticated(): Promise<void> {
	if (authenticated && socket && !socket.destroyed) return;

	if (connectPromise) {
		await connectPromise;
		return;
	}

	connectPromise = (async () => {
		await connectSocket();

		if (!guidoEnv.token) {
			throw new GuidoError('GUIDO_TOKEN is not configured');
		}

		const authed = await rawRequest<boolean>('auth', { token: guidoEnv.token });
		if (!authed) {
			throw new GuidoError('Guido authentication failed');
		}

		authenticated = true;
	})();

	try {
		await connectPromise;
	} catch (error) {
		resetConnection();
		throw error;
	} finally {
		connectPromise = null;
	}
}

export async function request<T>(
	method: string,
	parameters: Record<string, unknown> = {}
): Promise<T> {
	await ensureAuthenticated();
	return rawRequest<T>(method, parameters);
}

export { GuidoError, GuidoNotFoundError };
