<script lang="ts">
	import { page } from '$app/state';
	import * as Alert from '$lib/components/ui/alert';
</script>

{#if page.error}
	<main class="mx-auto flex w-full max-w-lg flex-col gap-4 p-4 md:p-8">
		<Alert.Root variant="destructive">
			<Alert.Title>{page.status ?? 'Error'}</Alert.Title>
			<Alert.Description>{page.error.message}</Alert.Description>
		</Alert.Root>
		<a href="/" class="text-sm text-muted-foreground hover:text-foreground">← Back home</a>
	</main>
{/if}
