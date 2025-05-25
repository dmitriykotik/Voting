package org.dmitriykotik.voting;

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

public class Vote_Commands implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String args[]) {
        if (!sender.hasPermission("voting.vote")) return AccessDenied(sender);
        if (!(sender instanceof Player) || args.length < 1) return sendHelp(sender);
        String id = args[0].toLowerCase();
        if (id == "help") return sendHelp(sender);
        Vars.Election e = new Vars.Election();
        e = Vars.elections.get(id);
        if (e == null || !e.published) return PollNotFound(sender);
        Player sndr = (Player)sender;
        if (e.votes.containsKey(sndr.getName())) return AlreadyVoted(sender);

        int optionCount = e.options.size();
        int baseRows = (optionCount + 1 + 8) / 9;
        int rows = Math.min(baseRows + 1, 6);
        int size = rows * 9;
        Inventory gui = Bukkit.createInventory(null, size, "Vote: " + e.name);

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
        
        return true;
    }

    private boolean sendHelp(CommandSender sender) {
        if (!sender.hasPermission("voting.help") || !sender.hasPermission("voting.vote"))
        {
            sender.sendMessage(Vars.Prefix + " " + Vars.AccessDeniedHelp);
            return true;
        }

        sender.sendMessage(" §7-+- §aVoting | vote §7-+-");
        sender.sendMessage(" §7- §avote help §f- §7" + Vars.HelpHelp);
        if (sender.hasPermission("voting.vote")) sender.sendMessage(" §7- §avote <id> §f- §7" + Vars.HelpVote);
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
