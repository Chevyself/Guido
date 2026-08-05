<script lang="ts">
	import { resolve } from '$app/paths';
	import TrophyIcon from '@lucide/svelte/icons/trophy';
	import * as Card from '$lib/components/ui/card';
	import type { PageData } from './$types';

	let { data }: { data: PageData } = $props();
</script>

<svelte:head>
	<title>Leaderboards · Guido</title>
</svelte:head>

<main class="mx-auto flex w-full max-w-4xl flex-col gap-6 p-4 md:p-8">
	<div class="space-y-2">
		<h1 class="text-3xl font-semibold tracking-tight">Ladder leaderboards</h1>
		<p class="text-muted-foreground">Top players by elo for each competitive ladder.</p>
	</div>

	{#if data.ladders.length === 0}
		<Card.Root>
			<Card.Content class="py-10 text-center text-muted-foreground">
				No ladders configured for this guild.
			</Card.Content>
		</Card.Root>
	{:else}
		<div class="grid gap-4 sm:grid-cols-2">
			{#each data.ladders as ladder (ladder.name)}
				<a href={resolve(`/leaderboards/${encodeURIComponent(ladder.name)}`)}>
					<Card.Root class="transition-colors hover:bg-muted/40">
						<Card.Header class="flex flex-row items-center gap-3">
							<div
								class="flex size-10 items-center justify-center rounded-lg bg-muted text-foreground"
							>
								<TrophyIcon class="size-5" />
							</div>
							<div>
								<Card.Title class="text-base">{ladder.name}</Card.Title>
								<Card.Description>View elo rankings</Card.Description>
							</div>
						</Card.Header>
					</Card.Root>
				</a>
			{/each}
		</div>
	{/if}
</main>
