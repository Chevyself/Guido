<script lang="ts">
	import { resolve } from '$app/paths';
	import { Button } from '$lib/components/ui/button';
	import * as Card from '$lib/components/ui/card';
	import * as Table from '$lib/components/ui/table';
	import type { PageData } from './$types';

	let { data }: { data: PageData } = $props();

	const prevHref = $derived(
		data.page > 0
			? `${resolve(`/rankings/${encodeURIComponent(data.stat)}`)}?page=${data.page - 1}`
			: null
	);
	const nextHref = $derived(
		data.hasNext
			? `${resolve(`/rankings/${encodeURIComponent(data.stat)}`)}?page=${data.page + 1}`
			: null
	);
</script>

<svelte:head>
	<title>{data.stat} ranking · Guido</title>
</svelte:head>

<main class="mx-auto flex w-full max-w-4xl flex-col gap-6 p-4 md:p-8">
	<div class="space-y-2">
		<h1 class="text-3xl font-semibold tracking-tight">{data.stat}</h1>
		<p class="text-muted-foreground">Stat ranking · page {data.page + 1}</p>
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
								<Table.Head class="text-right">Value</Table.Head>
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
