import { MongoClient } from 'mongodb';
import { mongoEnv } from './env';

let client: MongoClient | null = null;
let connectPromise: Promise<MongoClient> | null = null;

export async function getMongoClient(): Promise<MongoClient> {
	if (client) return client;

	if (!connectPromise) {
		connectPromise = MongoClient.connect(mongoEnv.uri).then((connected) => {
			client = connected;
			return connected;
		});
	}

	return connectPromise;
}

export async function getDb() {
	const mongoClient = await getMongoClient();
	return mongoClient.db(mongoEnv.database);
}
