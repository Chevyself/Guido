<script lang="ts">
	import { goto } from '$app/navigation';
	import { resolve } from '$app/paths';
	import BarChart3Icon from '@lucide/svelte/icons/bar-chart-3';
	import LinkIcon from '@lucide/svelte/icons/link';
	import SearchIcon from '@lucide/svelte/icons/search';
	import SwordsIcon from '@lucide/svelte/icons/swords';
	import TrophyIcon from '@lucide/svelte/icons/trophy';
	import { Badge } from '$lib/components/ui/badge';
	import { Button } from '$lib/components/ui/button';
	import * as Card from '$lib/components/ui/card';
	import { Input } from '$lib/components/ui/input';
	import { Separator } from '$lib/components/ui/separator';

	let nickname = $state('');

	const features = [
		{
			icon: TrophyIcon,
			title: 'Ladder rankings',
			description: 'Elo, wins, losses, and games played across every competitive ladder.'
		},
		{
			icon: SwordsIcon,
			title: 'Match stats',
			description: 'Kills, deaths, assists, and other context-specific stats from PGM matches.'
		},
		{
			icon: LinkIcon,
			title: 'Linked accounts',
			description: 'See whether a player is linked to Discord and their current online status.'
		}
	];

	const steps = [
		{ step: '1', text: 'Enter a Minecraft nickname in the search box.' },
		{ step: '2', text: 'Guido loads stats directly from the database.' },
		{ step: '3', text: 'View ladder tables and detailed stat breakdowns.' }
	];

	function search(event: SubmitEvent) {
		event.preventDefault();
		const trimmed = nickname.trim();
		if (!trimmed) return;
		goto(resolve(`/players/${encodeURIComponent(trimmed)}`));
	}
</script>

<svelte:head>
	<title>Guido — Minecraft player stats</title>
	<meta
		name="description"
		content="Look up Minecraft player stats from the Guido bot — ladder elo, wins, losses, and match stats."
	/>
</svelte:head>

<div class="flex flex-col">
	<!-- Hero -->
	<section class="relative overflow-hidden border-b border-border/60">
		<div
			class="pointer-events-none absolute inset-0 bg-[radial-gradient(ellipse_80%_60%_at_50%_-20%,var(--color-muted),transparent)]"
			aria-hidden="true"
		></div>
		<div
			class="pointer-events-none absolute inset-0 bg-[linear-gradient(to_bottom,transparent_60%,var(--color-background))]"
			aria-hidden="true"
		></div>

		<div class="relative mx-auto flex max-w-5xl flex-col items-center gap-8 px-4 py-16 text-center md:px-8 md:py-24">
			<Badge variant="secondary" class="gap-1.5">
				<BarChart3Icon class="size-3.5" />
				Player stats portal
			</Badge>

			<div class="max-w-2xl space-y-4">
				<h1 class="text-4xl font-semibold tracking-tight md:text-5xl">
					Minecraft stats,<br class="hidden sm:block" />
					powered by Guido
				</h1>
				<p class="text-lg text-muted-foreground">
					The same data behind the Discord <code class="rounded-md bg-muted px-1.5 py-0.5 text-sm"
						>stats</code
					> command — ladder standings, match history, and more in a clean web view.
				</p>
			</div>

			<Card.Root class="w-full max-w-lg text-left shadow-sm">
				<Card.Header class="pb-3">
					<Card.Title class="text-base">Find a player</Card.Title>
					<Card.Description>Search by Minecraft nickname</Card.Description>
				</Card.Header>
				<Card.Content>
					<form class="flex flex-col gap-3 sm:flex-row" onsubmit={search}>
						<div class="relative flex-1">
							<SearchIcon
								class="pointer-events-none absolute top-1/2 left-3 size-4 -translate-y-1/2 text-muted-foreground"
							/>
							<Input
								type="text"
								name="nickname"
								placeholder="e.g. Notch"
								class="pl-9"
								bind:value={nickname}
								autocomplete="username"
								required
							/>
						</div>
						<Button type="submit" size="lg" class="sm:shrink-0">
							View stats
						</Button>
					</form>
				</Card.Content>
				<Card.Footer class="justify-center border-t pt-4">
					<Button href={resolve('/leaderboards')} variant="outline" size="sm">
						<TrophyIcon class="size-4" />
						View ladder leaderboards
					</Button>
				</Card.Footer>
			</Card.Root>
		</div>
	</section>

	<!-- Features -->
	<section class="mx-auto w-full max-w-5xl px-4 py-16 md:px-8 md:py-20">
		<div class="mb-10 space-y-2 text-center">
			<h2 class="text-2xl font-semibold tracking-tight">What you can see</h2>
			<p class="text-muted-foreground">Everything the bot tracks, organized for the web.</p>
		</div>

		<div class="grid gap-4 sm:grid-cols-3">
			{#each features as feature (feature.title)}
				{#if feature.title === 'Ladder rankings'}
					<a href={resolve('/leaderboards')}>
						<Card.Root class="h-full border-border/60 transition-colors hover:bg-muted/40">
							<Card.Header>
								<div
									class="mb-1 flex size-10 items-center justify-center rounded-lg bg-muted text-foreground"
								>
									<feature.icon class="size-5" />
								</div>
								<Card.Title class="text-base">{feature.title}</Card.Title>
								<Card.Description>{feature.description}</Card.Description>
							</Card.Header>
						</Card.Root>
					</a>
				{:else}
					<Card.Root class="border-border/60">
						<Card.Header>
							<div
								class="mb-1 flex size-10 items-center justify-center rounded-lg bg-muted text-foreground"
							>
								<feature.icon class="size-5" />
							</div>
							<Card.Title class="text-base">{feature.title}</Card.Title>
							<Card.Description>{feature.description}</Card.Description>
						</Card.Header>
					</Card.Root>
				{/if}
			{/each}
		</div>
	</section>

	<Separator />

	<!-- How it works -->
	<section class="mx-auto w-full max-w-5xl px-4 py-16 md:px-8 md:py-20">
		<div class="mb-10 space-y-2 text-center">
			<h2 class="text-2xl font-semibold tracking-tight">How it works</h2>
			<p class="text-muted-foreground">No login required — just search and go.</p>
		</div>

		<ol class="mx-auto grid max-w-2xl gap-6">
			{#each steps as item (item.step)}
				<li class="flex items-start gap-4">
					<span
						class="flex size-8 shrink-0 items-center justify-center rounded-full bg-primary text-sm font-medium text-primary-foreground"
					>
						{item.step}
					</span>
					<p class="pt-1 text-muted-foreground">{item.text}</p>
				</li>
			{/each}
		</ol>
	</section>
</div>
