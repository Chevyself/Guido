<script lang="ts">
	import './layout.css';
	import favicon from '$lib/assets/favicon.svg';
	import SiteFooter from '$lib/components/site-footer.svelte';
	import SiteHeader from '$lib/components/site-header.svelte';

	let { children } = $props();
</script>

<svelte:head><link rel="icon" href={favicon} /></svelte:head>

<div class="flex min-h-svh flex-col">
	<SiteHeader />
	<div class="flex-1">
		{@render children()}
	</div>
	<SiteFooter />
</div>
