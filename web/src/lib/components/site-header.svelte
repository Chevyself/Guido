<script lang="ts">
	import { resolve } from '$app/paths';
	import { Button } from '$lib/components/ui/button';
</script>

<header class="border-b border-border/60 bg-background/80 backdrop-blur-sm">
	<div class="mx-auto flex h-14 max-w-5xl items-center justify-between px-4 md:px-8">
		<a href={resolve('/')} class="flex items-center gap-2 font-semibold tracking-tight">
			<span
				class="flex size-8 items-center justify-center rounded-lg bg-primary text-sm text-primary-foreground"
				aria-hidden="true"
			>
				G
			</span>
			Guido
		</a>
		<nav class="flex items-center gap-2">
			<Button href={resolve('/')} variant="ghost" size="sm">Home</Button>
			<Button href={resolve('/leaderboards')} variant="ghost" size="sm">Leaderboards</Button>
		</nav>
	</div>
</header>
