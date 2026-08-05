# Prompt: Player stats view (first SvelteKit feature)

Use this prompt in Agent mode to implement the first Guido web feature: a **player stats page** in the SvelteKit app at `web/` (start with `npm run dev` from that directory).

---

## Goal

Build `/players/[nickname]` — a page that shows a Minecraft player's Guido stats, matching what the Discord `stats` command shows today but as a proper web UI using **shadcn-svelte** components.

Reference implementation on the bot side:

- `server_bot/src/main/java/me/googas/bot/core/commands/UserCommands.java` — `stats` command and `buildStats()`
- Stats are loaded via `runtime.getLoader().getStats().getForMinecraftLink(linkable, Stats.EMPTY_CONTEXT)`
- Player lookup: `runtime.getLoader().getMinecraftLinks().getByNickname(nickname)`
- Default stats context: `Stats.EMPTY_CONTEXT` = `"no-context"` (`core/src/main/java/me/googas/api/stats/Stats.java`)

Stat keys in Mongo are a flat `Map<String, Double>`, e.g.:

- `{ladderName}-elo`, `{ladderName}-wins`, `{ladderName}-loses`, `{ladderName}-played`
- PGM / custom keys: `kills`, `deaths`, `assists`, etc. (context-specific; see `bukkit_pgm/.../PGMStatsHandler.java`)

---

## Architecture (hybrid: Mongo reads + TCP for live ops)

```
Browser  →  SvelteKit server (+page.server.ts)
              ├─ Read path (stats, leaderboards)  →  MongoDB (read-only)
              └─ Live/write path (future)         →  TCP JSON socket  →  GuidoBot
```

- **Never** open raw TCP or Mongo from the browser.
- **Stats and leaderboards** use `$lib/mongo/` on the **server only** (`mongodb` driver).
- **TCP client** (`$lib/guido/client.ts`) is kept for future live features (queue, matchmaking, writes).
- Use `$env/static/private` for `MONGO_URI`, `MONGO_DATABASE`, `GUIDO_GUILD_ID` (reads) and `GUIDO_*` (TCP).
- Prefer `@sveltejs/adapter-node` for Node `net` and Mongo support in production.
- Web reads must **not** auto-insert empty stats docs (unlike Java `getForMinecraftLink`).

### Mongo collections (read-only)

| Collection | Purpose |
|------------|---------|
| `minecraft-links` | Player lookup by nickname (`$regex`, matches Java) |
| `stats` | `_id: { linkableId, context }`, `values: { key: number }` |
| `guilds` | Ladder list for leaderboard index (`GUIDO_GUILD_ID`) |

Query parity reference: `server_mongo/.../MongoStatsLoader.java`, `MongoMinecraftLinksLoader.java`.

### Guido TCP wire protocol (starbox / `me.googas.net`)

1. Plain TCP, UTF-8.
2. Send request JSON, then a line `---` (see `JsonMessenger.printLine`).
3. Request shape:

```json
{
  "id": "<uuid>",
  "method": "<receptor method>",
  "parameters": { }
}
```

4. Response shape:

```json
{
  "id": "<same uuid>",
  "object": <result or null>,
  "error": false
}
```

5. After connect, call `auth` with `{ "token": "<GUIDO_TOKEN>" }` (method `auth` in `Requests.Server`).

Existing receptors in `server_bot` include `mclinks/save-stats` (write) and optional `mclinks/get-stats*` (for non-web clients). **The web app reads Mongo directly** and does not require these receptors.

---

## Part 1 — Java: `get-stats` receptors (optional for web)

Add to `core/src/main/java/me/googas/api/Requests.java` under `MinecraftLinks` (or a new `Stats` nested class):

| Constant | Value |
|----------|-------|
| Method | `mclinks/get-stats` |
| Params | `uuid` (UUID), optional `context` (String, default `no-context`) |

`RequestBuilder` return type: a DTO the web can consume, e.g.:

```java
public record PlayerStatsResponse(
    UUID uuid,
    String nickname,
    String context,
    Map<String, Double> stats) {}
```

Add `@Receptor` on `LinkHandler` (or a small new `StatsHandler` with `hasReceptors() == true`):

1. Resolve `MinecraftLinkable` by `uuid` (or support lookup by `nickname` if you add that param — nickname is nicer for the URL).
2. If not found → throw `JsonExternalCommunicationException` with message like `stats.player-not-found` (see `assets/lang/en.properties`).
3. Return `PlayerStatsResponse` with `stats.getMap()` contents.

Register the handler on the socket server if you create a new handler class (pattern in `GuidoBot.setupSocketServer()`).

Optional follow-up receptors (not required for v1):

- `mclinks/get-stats-by-nickname` — avoids UUID in the web layer
- `stats/leaderboard` — mirror `LeaderboardCommands`

---

## Part 2 — SvelteKit: Mongo read layer

```
web/src/lib/mongo/
  client.ts       # MongoClient singleton
  env.ts          # MONGO_URI, MONGO_DATABASE, GUIDO_GUILD_ID
  stats.ts        # getPlayerStatsByNickname, getLeaderboardByLadder, getRankingByStat
  guild.ts        # getLadders
  types.ts        # document shapes
  errors.ts       # PlayerNotFoundError, LadderNotFoundError
```

## Part 2b — SvelteKit: Guido TCP client (future live ops)

Create:

```
web/src/lib/guido/
  client.ts       # singleton connection, auth, request()
  types.ts        # PlayerStatsResponse, GuidoError
  env.ts          # read private env with defaults for local dev
```

`client.ts` responsibilities:

- Lazy-connect singleton `net.Socket`
- Authenticate once on first use
- `request<T>(method, parameters): Promise<T>` with UUID correlation and `---` framing
- Handle `error: true` responses and auth failures
- Timeouts aligned with bot `maxWait` (see `config.yml` / bot config, default ~3366)

Add `web/.env.example`:

```env
MONGO_URI=mongodb://localhost:27017
MONGO_DATABASE=guido
GUIDO_GUILD_ID=1511402659767128291

GUIDO_HOST=localhost
GUIDO_PORT=3366
GUIDO_TOKEN=your-token-here
```

Use a **read-only** Mongo user in production.

---

## Part 3 — Route: player stats page

### URL

`/players/[nickname]` — e.g. `/players/Notch`

### Server load

`web/src/routes/players/[nickname]/+page.server.ts`

- Read `params.nickname`
- Call `$lib/mongo/stats.getPlayerStatsByNickname(nickname)`
- On not found: `error(404, 'Player not found')`
- Return typed data for the page

### Leaderboard routes

| Route | Mirrors Discord |
|-------|-----------------|
| `/leaderboards` | — (ladder index) |
| `/leaderboards/[ladder]` | `lb` |
| `/rankings/[stat]` | `table` |

### UI (`+page.svelte`)

Use **shadcn-svelte** (install components via CLI as needed):

```bash
cd web
npx shadcn-svelte@latest add card table badge avatar separator skeleton alert input button
```

Layout:

1. **Header card**
   - Player head: `https://minotar.net/helm/{nickname}/100.png` (commented in `UserCommands.buildStats` — use it)
   - Nickname as title
   - Badges: linked status if you have that data

2. **Ladder stats section**
   - Parse keys ending in `-elo`, `-wins`, `-loses`, `-played`
   - Group by ladder name into rows
   - `Table` with columns: Ladder | Elo | Wins | Losses | Played | W/L ratio

3. **Other stats section**
   - Remaining keys (`kills`, `deaths`, etc.) in a second `Table` or stat grid (`Card` + `Badge`)

4. **States**
   - Loading: `Skeleton`
   - Not found: `Alert` destructive
   - Empty stats: friendly empty state

5. **Home / search**
   - Update `web/src/routes/+page.svelte` with a simple search form (nickname input + Button) that navigates to `/players/{nickname}`

### Styling

- Follow existing `web/src/routes/layout.css` and shadcn `neutral` / `luma` theme from `components.json`
- Mobile-friendly: tables scroll horizontally on small screens

---

## Part 4 — Types & stat parsing helper

`web/src/lib/stats/parse.ts`:

```ts
export type LadderRow = {
  ladder: string;
  elo: number;
  wins: number;
  losses: number;
  played: number;
};

export function parseStatsMap(map: Record<string, number>): {
  ladders: LadderRow[];
  other: { key: string; value: number }[];
};
```

Group logic:

- `{name}-elo` → ladder `{name}`
- Ignore unknown suffixes; dump ungrouped keys into `other`

---

## Acceptance criteria

- [ ] `npm run dev` in `web/` serves `/players/<nick>` with real data when Mongo is running
- [ ] `/leaderboards` and `/leaderboards/<ladder>` mirror Discord `lb`
- [ ] Page uses at least Card, Table, Badge, Avatar from shadcn-svelte
- [ ] No secrets in client bundle (`MONGO_*`, `GUIDO_*` only in server files)
- [ ] 404 when nickname unknown
- [ ] Stats pages work with **bot socket stopped** (Mongo-only)
- [ ] `npm run check` passes in `web/`

---

## Local test plan

1. Ensure Mongo is running with Guido data (`bot.properties` / `mongo_uri`, `database`).
2. Copy `MONGO_URI`, `MONGO_DATABASE`, `GUIDO_GUILD_ID` into `web/.env`.
3. `cd web && npm run dev`
4. Visit `/players/<nickname>` and verify tables match Discord `stats` output.
5. Visit `/leaderboards/<ladder>` and verify against Discord `lb`.
6. Confirm pages load with the bot socket **stopped**.

---

## Out of scope

- User login / Discord OAuth
- WebSocket live updates
- Editing stats from the web
- Global ladder computed elo (Java-only average)

---

## Copy-paste agent instruction (short)

> In repo `Guido`, implement the player stats feature described in `web/prompts/player-stats.md`. Read stats and leaderboards from MongoDB on the server; keep the Guido TCP client for future live ops. Build `/players/[nickname]`, `/leaderboards`, and leaderboard/ranking routes with shadcn-svelte UI.
