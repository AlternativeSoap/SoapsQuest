# Frequently Asked Questions

If you run into an issue or have a question, check below before asking for help. Most common problems are answered here.

Need more help? Join the Discord or visit the website:
- **Discord:** [discord.gg/SoapsUniverse](https://discord.gg/SoapsUniverse)
- **Website:** [SoapsUniverse.com](https://SoapsUniverse.com)

---

## General

**The plugin is not loading. What is wrong?**

Make sure your server is running Paper 1.21 or newer. SoapsQuest does not support Spigot or older Paper versions. Check your server console for any error messages when starting up.

**I updated the plugin and things are broken.**

After updating, reload the plugin with `/sq reload`. If the issue continues, check the changelog for any breaking changes. If you still have problems, ask in the Discord.

**Does SoapsQuest work on Spigot?**

No. SoapsQuest requires Paper 1.21 or newer. Spigot is not supported.

---

## Quests

**Players cannot see any quests in the quest browser.**

Make sure you have at least one quest defined in `quests.yml` that does not have conditions blocking that player. Also check that the player has the `soapsquest.quest` permission.

**The quest paper is not tracking progress.**

The player must be holding the quest paper in their hand or have it in their inventory while completing objectives. If progress is still not tracking, check that the objective type and target match what the player is doing. For example, a `kill_mob` objective needs the exact mob type in lowercase.

**The player completed the quest but cannot claim the reward.**

The player needs to right-click the quest paper to open the reward claim screen. Make sure the quest paper is still in their inventory and has not been thrown away. If the paper shows the quest as complete, the reward should be claimable.

**Can players have more than one quest at a time?**

Yes. By default players can hold as many quest papers as they want. You can limit this using the `active-limit` condition on your quests. See the Conditions page for details.

**Players are dropping quest papers and losing them.**

This is intentional. Quest papers are physical items. You can make the paper harder to lose by adding lore or a custom name so players know what it is. You can also enable settings in `config.yml` to prevent papers from being placed in chests or picked up by other players.

**Another player picked up someone else's quest paper.**

Enable the `prevent-other-pickup` option in `config.yml` to stop other players from picking up quest papers that do not belong to them.

**Can quest papers be placed in chests?**

By default, yes. You can disable this using the `prevent-chest-storage` option in `config.yml` if you do not want players storing quest papers.

---

## Random Quest Generator

**The random quest generator is not working.**

The Random Quest Generator is a premium feature. Make sure you have the premium version installed. Check that `enabled: true` is set in `random-generator.yml`.

**Generated quests have weird names.**

Check the `name-templates` section in `random-generator.yml`. Make sure the placeholders are spelled correctly and that your template list is not empty.

**How do I remove old generated quests from players?**

Generated quests automatically expire when the player completes or abandons them. There is no bulk removal command. If needed, you can reset player data from the database using the admin commands.

---

## Daily and Weekly Quests

**Daily and weekly quests are not working.**

Daily and weekly quests are a premium feature. Make sure you have the premium version installed. Check that `enabled: true` is set in the `daily.yml` file under both the `daily` and `weekly` sections.

**Players did not receive their daily quest.**

Make sure the player was online at the time the daily quest was assigned, or that your notification mode is set to `on-join` so they receive it when they log in. Check `daily.yml` to confirm the assign-time and notification settings are correct.

---

## Rewards

**Money rewards are not working.**

Money rewards require Vault and a compatible economy plugin (like EssentialsX) to be installed. Without Vault, the money reward type will not function. Check that Vault is installed and that your economy plugin is set up correctly.

**Items are not being given to the player.**

Check the item definition in your quest reward. The `material` field must be a valid Minecraft item name in uppercase, for example `DIAMOND` or `IRON_SWORD`. If the item has custom NBT, make sure the format is correct.

**Commands in rewards are not running.**

Make sure the command does not start with a `/`. For example, use `give %player% diamond 1` not `/give %player% diamond 1`. The `%player%` placeholder is replaced with the player's username when the command runs.

---

## GUI

**The quest browser GUI is broken or shows the wrong items.**

Try running `/sq reload` to reload the plugin. If items in the GUI look wrong, check your `gui.yml` file for any formatting errors. Remove any extra spaces or tabs that might be causing YAML parsing problems.

**Players cannot open the quest browser.**

Make sure the player has the `soapsquest.quest` permission. Check your permissions plugin to confirm it is set up correctly.

**The quest editor button is not showing in the GUI.**

The quest editor is a premium feature. If you have the premium version, make sure the player or group has the `soapsquest.admin` permission.

---

## Performance

**The plugin is causing lag or slowdowns.**

Check your `config.yml` for the performance settings. You can lower the progress update frequency and disable some visual features to improve performance. If you have a large number of quests or players, consider increasing your server resources.

**Can I clean up old player data?**

Currently, player data is stored per UUID. Old data from players who have not joined in a long time does not automatically get removed. A cleanup feature may be added in a future update.

---

## PlaceholderAPI

**Placeholders are not working.**

Make sure PlaceholderAPI is installed on your server. Then run `/papi parse <yourname> %soapsquest_player_quests%` to see if it returns a value. If it returns the placeholder text unchanged, PlaceholderAPI may not be detecting SoapsQuest. Try reloading both plugins.

**What placeholders are available?**

See the [Placeholders](Placeholders.md) page for the full list.

---

## Still Need Help?

If your question is not answered here, join the Discord community. Please include the following when asking for help:

- Your server version (Paper 1.21.x)
- Your SoapsQuest version
- Any error messages from your console
- What you were doing when the problem happened

**Discord:** [discord.gg/SoapsUniverse](https://discord.gg/SoapsUniverse)
**Website:** [SoapsUniverse.com](https://SoapsUniverse.com)
