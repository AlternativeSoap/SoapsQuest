# FAQ

Common questions and troubleshooting for SoapsQuest.

---

## General

### The plugin isn't loading
- Make sure you're running **Paper 1.21** or newer. SoapsQuest doesn't support Spigot or older versions.
- Check your console for errors on startup. Look for `[SoapsQuest]` messages.
- Make sure the jar file is in the `plugins/` folder and the server was restarted (not just reloaded with `/reload`).

### How do I update the plugin?
Replace the old jar with the new one and restart the server. Your config files are preserved — the plugin only generates defaults for missing files.

### Does SoapsQuest support Spigot?
No, only Paper 1.21+ is supported.

---

## Quests

### Players can't see any quests
- Run `/sq list` as an operator to check if quests are loaded
- If the list is empty, check your `quests.yml` for YAML syntax errors
- Run `/sq reload` — if there are config errors, the plugin will tell you what's wrong

### The quest paper isn't tracking progress
- Make sure the quest paper is in the player's inventory (not in a chest)
- Check if the quest is the **active** one — only the first paper of each quest type tracks progress
- If the quest has conditions (like a world or level requirement), make sure they're met
- If `lock-to-player` is enabled, only the original owner can progress

### How do players claim rewards?
Right-click the quest paper when all objectives are complete. The paper lore updates to show "COMPLETE!" and clicking it gives the rewards.

### Can players have multiple quests at once?
Yes. Players can have as many different quest papers as they want. Each quest type tracks independently. If they have multiple papers of the *same* quest, only the first one is active — the rest are queued.

### What happens when a player drops a quest paper?
By default (`abandon-on-drop: true`), dropping a quest paper abandons it — progress is reset and the paper becomes unbound. You can change this in `config.yml`.

### What happens when a quest paper goes into a chest?
Same as dropping — if `abandon-on-container-store: true`, storing a quest paper in any container resets it.

### Can other players pick up quest papers?
Yes, unless `lock-to-player: true` is set on the quest. Locked quest papers can only be used by the original owner.

---

## Random Generator

### `/sq generate` says "disabled"
Enable it in `random-generator.yml`:
```yaml
random-generator:
  enabled: true
```

### Generated quests have weird names
Customize the name templates in `random-generator.yml` under `display.name-templates`. Each objective type has its own set of name templates.

### How do I remove generated quests?
Use `/sq remove <quest_id>` to delete them. Generated quests are stored in `generated.yml`, not `quests.yml`.

---

## Daily/Weekly Quests

### Daily quests aren't working
1. Make sure `enabled: true` in `daily.yml`
2. Add quest IDs to the `quests` list — they must match IDs in `quests.yml`
3. The reset time uses server timezone in 24-hour format

### Players didn't receive daily quests
Players who were offline during reset get their quests on next login. Make sure the quest IDs in the daily config actually exist.

---

## Rewards

### Money rewards aren't working
You need **Vault** and an economy plugin installed (EssentialsX, CMI, etc.). Just having Vault alone isn't enough — it needs an economy backend.

### Items aren't being given
Check if the player's inventory is full. If the inventory is full, some plugins handle overflow differently. Also verify the `material` name is a valid Minecraft item type.

### Command rewards aren't running
- Commands run from console, so don't include the `/` prefix
- Use `{player}` as the placeholder for the completing player's name
- Make sure the command works when you run it from console manually

---

## GUI

### The GUI looks broken
Run `/sq reload` to refresh the GUI configuration. If items are missing, check `gui.yml` for valid material names.

### Players can't open the browser
They need the `soapsquest.gui.browser` permission. By default, everyone has this — but if you're using a permission plugin that removes defaults, you'll need to add it explicitly.

### The editor button doesn't show
The editor button only appears for players with `soapsquest.gui.editor` permission.

---

## Performance

### Is the plugin laggy?
SoapsQuest uses async processing and batch saves by default. For large servers:
- Keep `async-processing-enabled: true`
- Keep `batch-save-enabled: true`
- Increase `autosave-interval` if saves are noticeable
- The plugin is designed to handle hundreds of concurrent quests without server lag

### Data cleanup
The plugin automatically cleans up data from inactive players (default: 90 days offline). Adjust in `config.yml`:
```yaml
data-cleanup:
  enabled: true
  remove-inactive-after-days: 90
```

---

## PlaceholderAPI

### Placeholders aren't working
1. Make sure PlaceholderAPI is installed and loaded
2. SoapsQuest registers its expansion automatically — no `/papi ecloud` command needed
3. The identifier is `soapsquest` — use `%soapsquest_player_quests%` etc.
4. Try `/papi parse me %soapsquest_player_quests%` to test

---

## Still need help?

- **Discord:** [discord.gg/mawAzwFq](https://discord.gg/mawAzwFq)
- **Website:** [SoapsUniverse.com](https://www.SoapsUniverse.com)
