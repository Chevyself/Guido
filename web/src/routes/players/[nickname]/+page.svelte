<script lang="ts">
	import * as Avatar from '$lib/components/ui/avatar';
	import { Badge } from '$lib/components/ui/badge';
	import * as Card from '$lib/components/ui/card';
	import { Separator } from '$lib/components/ui/separator';
	import * as Table from '$lib/components/ui/table';
	import { winLossRatio } from '$lib/stats/parse';
	import type { PageData } from './$types';

	let { data }: { data: PageData } = $props();

	const headUrl = $derived(
		`https://minotar.net/helm/${encodeURIComponent(data.player.nickname)}/100.png`
	);
	const hasLadders = $derived(data.ladders.length > 0);
	const hasOther = $derived(data.other.length > 0);
	const isEmpty = $derived(!hasLadders && !hasOther);
</script>

<svelte:head>
	<title>{data.player.nickname} · Guido Stats</title>
</svelte:head>

<main class="mx-auto flex w-full max-w-4xl flex-col gap-6 p-4 md:p-8">
	<Card.Root>
		<Card.Header class="flex flex-row items-center gap-4">
			<Avatar.Root class="size-20">
				<Avatar.Image src={headUrl} alt="{data.player.nickname}'s Minecraft head" />
				<Avatar.Fallback>{data.player.nickname.slice(0, 2).toUpperCase()}</Avatar.Fallback>
			</Avatar.Root>
			<div class="flex flex-col gap-2">
				<Card.Title class="text-2xl">{data.player.nickname}</Card.Title>
				<div class="flex flex-wrap gap-2">
					{#if data.player.linked}
						<Badge variant="secondary">Linked</Badge>
					{:else}
						<Badge variant="outline">Not linked</Badge>
					{/if}
					{#if data.player.online}
						<Badge>Online</Badge>
					{:else}
						<Badge variant="outline">Offline</Badge>
					{/if}
					<Badge variant="outline">Context: {data.player.context}</Badge>
				</div>
			</div>
		</Card.Header>
	</Card.Root>

	{#if isEmpty}
		<Card.Root>
			<Card.Content class="py-10 text-center text-muted-foreground">
				No stats recorded for this player yet.
			</Card.Content>
		</Card.Root>
	{:else}
		{#if hasLadders}
			<section class="space-y-3">
				<h2 class="text-lg font-semibold">Ladder stats</h2>
				<Card.Root>
					<Card.Content class="p-0">
						<div class="overflow-x-auto">
							<Table.Root>
								<Table.Header>
									<Table.Row>
										<Table.Head>Ladder</Table.Head>
										<Table.Head class="text-right">Elo</Table.Head>
										<Table.Head class="text-right">Wins</Table.Head>
										<Table.Head class="text-right">Losses</Table.Head>
										<Table.Head class="text-right">Played</Table.Head>
										<Table.Head class="text-right">W/L</Table.Head>
									</Table.Row>
								</Table.Header>
								<Table.Body>
									{#each data.ladders as row (row.ladder)}
										<Table.Row>
											<Table.Cell class="font-medium">{row.ladder}</Table.Cell>
											<Table.Cell class="text-right">{row.elo}</Table.Cell>
											<Table.Cell class="text-right">{row.wins}</Table.Cell>
											<Table.Cell class="text-right">{row.losses}</Table.Cell>
											<Table.Cell class="text-right">{row.played}</Table.Cell>
											<Table.Cell class="text-right">
												{winLossRatio(row.wins, row.losses)}
											</Table.Cell>
										</Table.Row>
									{/each}
								</Table.Body>
							</Table.Root>
						</div>
					</Card.Content>
				</Card.Root>
			</section>
		{/if}

		{#if hasOther}
			<Separator />
			<section class="space-y-3">
				<h2 class="text-lg font-semibold">Other stats</h2>
				<Card.Root>
					<Card.Content class="p-0">
						<div class="overflow-x-auto">
							<Table.Root>
								<Table.Header>
									<Table.Row>
										<Table.Head>Stat</Table.Head>
										<Table.Head class="text-right">Value</Table.Head>
									</Table.Row>
								</Table.Header>
								<Table.Body>
									{#each data.other as stat (stat.key)}
										<Table.Row>
											<Table.Cell class="font-medium">{stat.key}</Table.Cell>
											<Table.Cell class="text-right">{stat.value}</Table.Cell>
										</Table.Row>
									{/each}
								</Table.Body>
							</Table.Root>
						</div>
					</Card.Content>
				</Card.Root>
			</section>
		{/if}
	{/if}
</main>
