package org.dmitriykotik.voting;

import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.logging.Logger;

public final class Voting extends JavaPlugin {

    @Override
    public void onEnable() {
        Logger log = this.getLogger();
        log.info("-=====- Voting -=====-");
        log.info("* Project: Voting");
        log.info("* Author: dmitriykotik");
        log.info("-====================-");
        log.info("");
        log.info(" -+- Initialization... -+-");
        log.info(" - Saving the configuration file...");
        this.saveDefaultConfig();
        log.info(" - File saved!");
        log.info(" - Initialization of variables...");
        Initialize();
        log.info(" - Done!");
        log.info(" - Adding commands to the Voting class...");
        this.getCommand("poll").setExecutor(new Poll_Commands(this));
        this.getCommand("poll").setTabCompleter(new Poll_TabCompleter());
        this.getCommand("vote").setExecutor(new Vote_Commands());
        this.getCommand("vote").setTabCompleter(new Vote_TabCompleter());
        getServer().getPluginManager().registerEvents(new Voting_Listener(this), this);
        log.info(" - Done!");
        log.info(" -+- DONE -+-");
        log.info(" -+- Plugin successfully enabled -+-");

    }

    @Override
    public void onDisable() {
        Logger log = this.getLogger();
        log.info("-=====- Voting -=====-");
        log.info("* Project: Voting");
        log.info("* Author: dmitriykotik");
        log.info("-====================-");
        log.info("");
        log.info(" -+- Plugin successfully disabled -+-");
    }

    private void Initialize() {
        reloadConfig();
        FileConfiguration cfg = getConfig();
        Vars.keyPollId = new NamespacedKey(this, "poll_id");
        // Properties
        Vars.Prefix = cfg.getString("properties.prefix");

        // Messages
        Vars.ReloadStart = cfg.getString("messages.reload_start");
        Vars.ReloadDone = cfg.getString("messages.reload_done");

        Vars.CommandNotFound = cfg.getString("messages.command_not_found");
        Vars.AccessDenied = cfg.getString("messages.access_denied");
        Vars.AccessDeniedHelp = cfg.getString("messages.access_denied_help");

        Vars.HelpHelp = cfg.getString("messages.help_help");
        Vars.HelpCreate = cfg.getString("messages.help_create");
        Vars.HelpAdd = cfg.getString("messages.help_add");
        Vars.HelpRemove = cfg.getString("messages.help_remove");
        Vars.HelpClear = cfg.getString("messages.help_clear");
        Vars.HelpPreview = cfg.getString("messages.help_preview");
        Vars.HelpPublish = cfg.getString("messages.help_publish");
        Vars.HelpVote = cfg.getString("messages.help_vote");
        Vars.HelpResult = cfg.getString("messages.help_result");

        Vars.HelpDelete = cfg.getString("messages.help_delete");
        Vars.HelpReload = cfg.getString("messages.help_reload");

        Vars.Poll = cfg.getString("messages.poll");
        Vars.PollCreatedWithId = cfg.getString("messages.poll_created_with_id");
        Vars.PollNotFound = cfg.getString("messages.poll_not_found");
        Vars.PollElementAdd = cfg.getString("messages.poll_element_add");
        Vars.PollElementRemove = cfg.getString("messages.poll_element_remove");
        Vars.PollElementClear = cfg.getString("messages.poll_element_clear");
        Vars.PollPublish = cfg.getString("messages.poll_publish");
        Vars.PollAlreadyVoted = cfg.getString("messages.poll_already_voted");
        Vars.PollResults = cfg.getString("messages.poll_results");
        Vars.PollWinners = cfg.getString("messages.poll_winners");
        Vars.PollDelete = cfg.getString("messages.poll_delete");

        Vars.PollSkip = cfg.getString("messages.poll_skip");
        Vars.PollSelfVote = cfg.getString("messages.poll_self_vote");
        Vars.PollVoted = cfg.getString("messages.poll_voted");
        Vars.PollInvalidElement = cfg.getString("messages.poll_invalid_element");

        LoadElections();
    }

    public void LoadElections() {
        FileConfiguration cfg = getConfig();
        Vars.elections.clear();

        if (!cfg.contains("elections")) return;

        ConfigurationSection sec = cfg.getConfigurationSection("elections");
        for (String id : sec.getKeys(false)) {
            ConfigurationSection eSec = sec.getConfigurationSection(id);
            Vars.Election e = new Vars.Election();
            e.id = id;
            e.name = eSec.getString("name", id);
            e.owner = eSec.getString("owner", "console");
            e.published = eSec.getBoolean("published", false);
            e.options.addAll(eSec.getStringList("options"));

            if (eSec.contains("votes")) {
                ConfigurationSection vSec = eSec.getConfigurationSection("votes");
                for (String player : vSec.getKeys(false)) {
                    e.votes.put(player, vSec.getString(player));
                }
            }
            Vars.elections.put(id, e);
        }
    }

    public void SaveElections() {
        FileConfiguration cfg = getConfig();

        cfg.set("elections", null);

        for (Map.Entry<String, Vars.Election> en : Vars.elections.entrySet()) {
            String id = en.getKey();
            Vars.Election e = en.getValue();
            String base = "elections." + id + ".";

            cfg.set(base + "name", e.name);
            cfg.set(base + "owner", e.owner);
            cfg.set(base + "published", e.published);
            cfg.set(base + "options", e.options);
            cfg.set(base + "votes", e.votes);
        }

        saveConfig();
    }

    public void SaveResults(String id, Map<String, Integer> results) {
        FileConfiguration cfg = getConfig();
        String path = "results." + id;
        if (cfg.contains(path)) {
            path += "_" + new Random().nextInt(10000);
        }
        cfg.createSection(path, results);
        saveConfig();
    }

    public void Reload() {
        Logger log = this.getLogger();
        log.info("-=====- Voting -=====-");
        log.info("* Project: Voting");
        log.info("* Author: dmitriykotik");
        log.info("-====================-");
        log.info("");
        log.info(" -+- Plugin successfully disabled -+-");

        log.info("-=====- Voting -=====-");
        log.info("* Project: Voting");
        log.info("* Author: dmitriykotik");
        log.info("-====================-");
        log.info(" - Saving the configuration file...");
        this.saveDefaultConfig();
        log.info(" - File saved!");
        log.info(" - Initialization of variables...");
        Initialize();
        log.info(" - Done!");
        log.info(" - Adding commands to the DoorLocker class...");
        log.info(" - Skip...");
        log.info(" - Done!");
        log.info(" -+- DONE -+-");
        log.info(" -+- Plugin successfully enabled -+-");
    }
}
