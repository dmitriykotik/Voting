package org.dmitriykotik.voting;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;
import org.dmitriykotik.voting.Vars.Election;

public class Voting_Listener implements Listener {
    private final Voting plugin;
    
    public Voting_Listener(Voting plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e) {
        if (!(e.getWhoClicked() instanceof Player)) return;
        Player player = (Player) e.getWhoClicked();
        String title = ChatColor.stripColor(e.getView().getTitle());
        int topSize = e.getView().getTopInventory().getSize();

        if (title.startsWith("Preview: ") || title.startsWith("Vote: ")) {
            if (e.getRawSlot() < topSize) {
                e.setCancelled(true);
            }
            if (title.startsWith("Preview: ")) {
                return;
            }

            ItemStack clicked = e.getCurrentItem();
            if (clicked == null || !clicked.hasItemMeta()) return;

            ItemMeta meta = clicked.getItemMeta();
            PersistentDataContainer container = meta.getPersistentDataContainer();
            if (!container.has(Vars.keyPollId, PersistentDataType.STRING)) return;

            String pollId = container.get(Vars.keyPollId, PersistentDataType.STRING);
            Election poll = Vars.elections.get(pollId);
            if (poll == null) {
                PollNotFound(player);
                player.closeInventory();
                return;
            }
            if (poll.votes.containsKey(player.getName())) {
                AlreadyVoted(player);
                player.closeInventory();
                return;
            }

            String option = ChatColor.stripColor(meta.getDisplayName());
            if (option.equalsIgnoreCase("Skip")) {
                poll.votes.put(player.getName(), "SKIP");
                player.sendMessage(Vars.Prefix + " " + Vars.PollSkip);
            } else if (option.toLowerCase().contains(player.getName().toLowerCase())) {
                player.sendMessage(Vars.Prefix + " " + Vars.PollSelfVote);
            } else if (poll.options.contains(option)) {
                poll.votes.put(player.getName(), option);
                player.sendMessage(Vars.Prefix + " " + Vars.PollVoted + ": " + option);
            } else {
                player.sendMessage(Vars.Prefix + " " + Vars.PollInvalidElement);
            }

            plugin.SaveElections();
            Vars.playerVote.remove(player.getName());
            player.closeInventory();
        }
    }

    @EventHandler
    public void onInventoryDrag(InventoryDragEvent e) {
        String title = ChatColor.stripColor(e.getView().getTitle());
        int topSize = e.getView().getTopInventory().getSize();
        if (title.startsWith("Preview: ") || title.startsWith("Vote: ")) {
            for (int slot : e.getRawSlots()) {
                if (slot < topSize) {
                    e.setCancelled(true);
                    break;
                }
            }
        }
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
