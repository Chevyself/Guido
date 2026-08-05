export class MongoError extends Error {
	constructor(message: string) {
		super(message);
		this.name = 'MongoError';
	}
}

export class PlayerNotFoundError extends MongoError {
	constructor(message = 'Player not found') {
		super(message);
		this.name = 'PlayerNotFoundError';
	}
}

export class LadderNotFoundError extends MongoError {
	constructor(message = 'Ladder not found') {
		super(message);
		this.name = 'LadderNotFoundError';
	}
}
