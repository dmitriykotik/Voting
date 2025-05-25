package org.dmitriykotik.voting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.dmitriykotik.voting.Vars.Election;

public class Poll_Commands implements CommandExecutor {
    private final Voting plugin;
    
    public Poll_Commands(Voting plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String args[]) {
        if (args.length < 1) return sendHelp(sender);
        String sub = args[0].toLowerCase();
        switch (sub) {
            case "help":
                return sendHelp(sender);
            case "create":
                if (!sender.hasPermission("voting.create")) return AccessDenied(sender);
                if (!(sender instanceof Player) || args.length < 3) return sendHelp(sender);
                String id = args[1].toLowerCase();
                String name = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
                Election e = new Election();
                e.id = id; e.name = name; e.owner = sender.getName();
                Vars.elections.put(id, e);
                sender.sendMessage(Vars.Prefix + " " + Vars.Poll + " '" + name + "' " + Vars.PollCreatedWithId + ": " + id);
                plugin.SaveElections();
                break;
            case "add":
                if (!sender.hasPermission("voting.add")) return AccessDenied(sender);
                if (!(sender instanceof Player) || args.length < 2) return sendHelp(sender);
                id = args[1].toLowerCase();
                e = Vars.elections.get(id);
                if (e == null) return PollNotFound(sender);

                e.options.add(args[2]); 
                sender.sendMessage(Vars.Prefix + " " + Vars.PollElementAdd);
                plugin.SaveElections();
                break;
            case "remove":
                if (!sender.hasPermission("voting.remove")) return AccessDenied(sender);
                if (!(sender instanceof Player) || args.length < 2) return sendHelp(sender);
                id = args[1].toLowerCase();
                e = Vars.elections.get(id);
                if (e == null) return PollNotFound(sender);

                e.options.remove(args[2]);
                sender.sendMessage(Vars.Prefix + " " + Vars.PollElementRemove);
                plugin.SaveElections();
                break;
            case "clear":
                if (!sender.hasPermission("voting.clear")) return AccessDenied(sender);
                if (!(sender instanceof Player) || args.length < 2) return sendHelp(sender);
                id = args[1].toLowerCase();
                e = Vars.elections.get(id);
                if (e == null) return PollNotFound(sender);

                e.options.clear(); 
                e.votes.clear();
                sender.sendMessage(Vars.Prefix + " " + Vars.PollElementClear);
                plugin.SaveElections();
                break;
            case "preview":
                if (!sender.hasPermission("voting.preview")) return AccessDenied(sender);
                if (!(sender instanceof Player) || args.length < 2) return sendHelp(sender);
                id = args[1].toLowerCase();
                e = Vars.elections.get(id);
                if (e == null) return PollNotFound(sender);
                Player sndr = (Player)sender;

                int optionCount = e.options.size();
                int baseRows   = (optionCount + 1 + 8) / 9;            // сколько рядов нужно для опций (+1 для кнопки Skip)
                int rows       = Math.min(baseRows + 1, 6);           // +1 — ваш дополнительный ряд, максимум 6 (54 слота)
                int size       = rows * 9;

                Inventory gui = Bukkit.createInventory(null, size, ChatColor.AQUA + "Preview: " + e.name);
                for (String opt : e.options) {
                    ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
                    SkullMeta m = (SkullMeta) skull.getItemMeta();
                    m.setOwningPlayer(Bukkit.getOfflinePlayer(opt));
                    m.setDisplayName(ChatColor.YELLOW + opt);
                    skull.setItemMeta(m);
                    gui.addItem(skull);
                }
                sndr.openInventory(gui);
                break;
            case "publish":
                if (!sender.hasPermission("voting.publish")) return AccessDenied(sender);
                if (!(sender instanceof Player) || args.length < 2) return sendHelp(sender);
                id = args[1].toLowerCase();
                e = Vars.elections.get(id);
                if (e == null) return PollNotFound(sender);

                e.published = true;
                sender.sendMessage(Vars.Prefix + " " + Vars.PollPublish);
                plugin.SaveElections();
                break;
            case "vote":
                if (!sender.hasPermission("voting.vote")) return AccessDenied(sender);
                if (!(sender instanceof Player) || args.length < 2) return sendHelp(sender);
                id = args[1].toLowerCase();
                e = Vars.elections.get(id);
                if (e == null || !e.published) return PollNotFound(sender);
                sndr = (Player)sender;
                if (e.votes.containsKey(sndr.getName())) return AlreadyVoted(sender);

                optionCount = e.options.size();
                baseRows = (optionCount + 1 + 8) / 9;
                rows = Math.min(baseRows + 1, 6);
                size = rows * 9;
                gui = Bukkit.createInventory(null, size, "Vote: " + e.name);

                for (String opt : e.options) {
                    if (opt.toLowerCase().contains(sndr.getName().toLowerCase())) continue;
                    ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
                    SkullMeta m = (SkullMeta) skull.getItemMeta();
                    m.setOwningPlayer(Bukkit.getOfflinePlayer(opt));
                    m.setDisplayName(ChatColor.YELLOW + opt);
                    PersistentDataContainer container = m.getPersistentDataContainer();
                    container.set(Vars.keyPollId, PersistentDataType.STRING, e.id);
                    skull.setItemMeta(m);
                    gui.addItem(skull);
                }
                ItemStack barrier = new ItemStack(Material.BARRIER);
                ItemMeta bm = barrier.getItemMeta();
                bm.setDisplayName(ChatColor.RED + "Skip");
                bm.getPersistentDataContainer().set(Vars.keyPollId, PersistentDataType.STRING, e.id);
                barrier.setItemMeta(bm);
                gui.setItem(gui.getSize() - 1, barrier);

                sndr.openInventory(gui);
                Vars.playerVote.put(sndr.getName(), id);
                break;
            case "result":
                if (!sender.hasPermission("voting.result")) return AccessDenied(sender);
                if (!(sender instanceof Player) || args.length < 2) return sendHelp(sender);
                id = args[1].toLowerCase();
                e = Vars.elections.get(id);
                if (e == null) return PollNotFound(sender);

                Map<String,Integer> counts = new LinkedHashMap<>();
                for (String opt : e.options) {
                    counts.put(opt, 0);
                }
                for (String vote : e.votes.values()) {
                    if (counts.containsKey(vote)) {
                        counts.put(vote, counts.get(vote) + 1);
                    }
                }
                sender.sendMessage(Vars.Prefix + " " + Vars.PollResults + " '" + e.name + "':");
                for (Map.Entry<String,Integer> entry : counts.entrySet()) {
                    sender.sendMessage(Vars.Prefix + " §e" + entry.getKey() + "§f: §a" + entry.getValue());
                }
                int max = counts.values().stream().max(Integer::compareTo).orElse(0);
                List<String> winners = new ArrayList<>();
                for (Map.Entry<String,Integer> entry : counts.entrySet()) {
                    if (entry.getValue() == max) winners.add(entry.getKey());
                }
                sender.sendMessage(Vars.Prefix + " " + Vars.PollWinners + ": " + String.join(", ", winners));
                plugin.SaveResults(id, counts);
                Vars.elections.remove(id);
                plugin.SaveElections();
                break;
            case "delete":
                if (!sender.hasPermission("voting.delete")) return AccessDenied(sender);
                if (!(sender instanceof Player) || args.length < 2) return sendHelp(sender);
                id = args[1].toLowerCase();
                e = Vars.elections.get(id);
                if (e == null) return PollNotFound(sender);

                Vars.elections.remove(id);
                sender.sendMessage(Vars.Prefix + " " + Vars.PollDelete);
                plugin.SaveElections();
                break;
            case "reload":
                if (!sender.hasPermission("voting.reload")) return AccessDenied(sender);
                sender.sendMessage(Vars.ReloadStart);
                plugin.Reload();
                sender.sendMessage(Vars.ReloadDone);
                break;
            default:
                return sendHelp(sender);
        }
        return true;
    }

    private boolean sendHelp(CommandSender sender) {
        if (!sender.hasPermission("voting.help"))
        {
            sender.sendMessage(Vars.Prefix + " " + Vars.AccessDeniedHelp);
            return true;
        }

        sender.sendMessage(" §7-+- §aVoting | poll §7-+-");
        sender.sendMessage(" §7- §apoll help §f- §7" + Vars.HelpHelp);
        if (sender.hasPermission("voting.create")) sender.sendMessage(" §7- §apoll create <id> <name_poll> §f- §7" + Vars.HelpCreate);
        if (sender.hasPermission("voting.add")) sender.sendMessage(" §7- §apoll add <id> <name_object> §f- §7" + Vars.HelpAdd);
        if (sender.hasPermission("voting.remove")) sender.sendMessage(" §7- §apoll remove <id> <name_object> §f- §7" + Vars.HelpRemove);
        if (sender.hasPermission("voting.clear")) sender.sendMessage(" §7- §apoll clear <id> §f- §7" + Vars.HelpClear);
        if (sender.hasPermission("voting.preview")) sender.sendMessage(" §7- §apoll preview <id> §f- §7" + Vars.HelpPreview);
        if (sender.hasPermission("voting.publish")) sender.sendMessage(" §7- §apoll publish <id> §f- §7" + Vars.HelpPublish);
        if (sender.hasPermission("voting.vote")) sender.sendMessage(" §7- §apoll vote <id> §f- §7" + Vars.HelpVote);
        if (sender.hasPermission("voting.result")) sender.sendMessage(" §7- §apoll result <id> §f- §7" + Vars.HelpResult);
        if (sender.hasPermission("voting.delete")) sender.sendMessage(" §7- §apoll delete <id> §f- §7" + Vars.HelpDelete);
        if (sender.hasPermission("voting.reload")) sender.sendMessage(" §7- §apoll reload <id> §f- §7" + Vars.HelpReload);
        return true;
    }

    private boolean AccessDenied(CommandSender sender) {
        sender.sendMessage(Vars.Prefix + " " + Vars.AccessDeniedHelp);
        return true;
    }

    private boolean PollNotFound(CommandSender sender) {
        sender.sendMessage(Vars.Prefix + " " + Vars.PollNotFound);
        return true;
    }

    private boolean AlreadyVoted(CommandSender sender) {
        sender.sendMessage(Vars.Prefix + " " + Vars.PollAlreadyVoted);
        return true;
    }
}
