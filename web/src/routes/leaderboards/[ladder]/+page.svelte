<script lang="ts">
	import { resolve } from '$app/paths';
	import { Button } from '$lib/components/ui/button';
	import * as Card from '$lib/components/ui/card';
	import * as Table from '$lib/components/ui/table';
	import type { PageData } from './$types';

	let { data }: { data: PageData } = $props();

	const prevHref = $derived(
		data.page > 0
			? `${resolve(`/leaderboards/${encodeURIComponent(data.ladder)}`)}?page=${data.page - 1}`
			: null
	);
	const nextHref = $derived(
		data.hasNext
			? `${resolve(`/leaderboards/${encodeURIComponent(data.ladder)}`)}?page=${data.page + 1}`
			: null
	);
</script>

<svelte:head>
	<title>{data.ladder} leaderboard · Guido</title>
</svelte:head>

<main class="mx-auto flex w-full max-w-4xl flex-col gap-6 p-4 md:p-8">
	<div class="space-y-2">
		<a href={resolve('/leaderboards')} class="text-sm text-muted-foreground hover:text-foreground">
			← All leaderboards
		</a>
		<h1 class="text-3xl font-semibold tracking-tight">{data.ladder}</h1>
		<p class="text-muted-foreground">Page {data.page + 1}</p>
	</div>

	<Card.Root>
		<Card.Content class="p-0">
			{#if data.rows.length === 0}
				<p class="px-6 py-10 text-center text-muted-foreground">No entries on this page.</p>
			{:else}
				<div class="overflow-x-auto">
					<Table.Root>
						<Table.Header>
							<Table.Row>
								<Table.Head class="w-16">Rank</Table.Head>
								<Table.Head>Player</Table.Head>
								<Table.Head class="text-right">Elo</Table.Head>
								<Table.Head class="text-right">Wins</Table.Head>
								<Table.Head class="text-right">Losses</Table.Head>
							</Table.Row>
						</Table.Header>
						<Table.Body>
							{#each data.rows as row (row.rank)}
								<Table.Row>
									<Table.Cell class="font-medium">{row.rank}</Table.Cell>
									<Table.Cell>
										<a
											href={resolve(`/players/${encodeURIComponent(row.display)}`)}
											class="hover:underline"
										>
											{row.display}
										</a>
									</Table.Cell>
									<Table.Cell class="text-right">{Math.round(row.value)}</Table.Cell>
									<Table.Cell class="text-right">{Math.round(row.wins)}</Table.Cell>
									<Table.Cell class="text-right">{Math.round(row.losses)}</Table.Cell>
								</Table.Row>
							{/each}
						</Table.Body>
					</Table.Root>
				</div>
			{/if}
		</Card.Content>
	</Card.Root>

	<div class="flex justify-between gap-4">
		{#if prevHref}
			<Button href={prevHref} variant="outline">Previous</Button>
		{:else}
			<Button variant="outline" disabled>Previous</Button>
		{/if}
		{#if nextHref}
			<Button href={nextHref} variant="outline">Next</Button>
		{:else}
			<Button variant="outline" disabled>Next</Button>
		{/if}
	</div>
</main>
