// See https://svelte.dev/docs/kit/types#app.d.ts
// for information about these interfaces
declare global {
	namespace App {
		// interface Error {}
		// interface Locals {}
		// interface PageData {}
		// interface PageState {}
		// interface Platform {}
	}
}

declare module '$env/static/private' {
	export const MONGO_URI: string;
	export const MONGO_DATABASE: string;
	export const GUIDO_GUILD_ID: string;
	export const GUIDO_HOST: string;
	export const GUIDO_PORT: string;
	export const GUIDO_TOKEN: string;
}

export {};
