package org.dmitriykotik.voting;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

public class Poll_TabCompleter implements TabCompleter{
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        Map<String, String> commandPermissions = new HashMap<>();
        commandPermissions.put("create", "voting.create");
        commandPermissions.put("add", "voting.add");
        commandPermissions.put("remove", "voting.remove");
        commandPermissions.put("preview", "voting.preview");
        commandPermissions.put("result", "voting.result");
        commandPermissions.put("clear", "voting.clear");
        commandPermissions.put("publish", "voting.publish");
        commandPermissions.put("vote", "voting.vote");
        commandPermissions.put("help", "voting.help");
        commandPermissions.put("delete", "voting.delete");
        commandPermissions.put("reload", "voting.reload");

        List<String> subs = new ArrayList<>();
        for (String cmd : commandPermissions.keySet()) {
            if (sender.hasPermission(commandPermissions.get(cmd))) {
                subs.add(cmd);
            }
        }

        if (args.length == 1) {
            return subs.stream()
                .filter(sub -> sub.startsWith(args[0].toLowerCase()))
                .collect(Collectors.toList());
        } else if (args.length == 2) {
            String sub = args[0].toLowerCase();
            if (!commandPermissions.containsKey(sub) || !sender.hasPermission(commandPermissions.get(sub))) {
                return Collections.emptyList();
            }

            List<String> ids = new ArrayList<>();

            if (Arrays.asList("add", "remove", "clear", "publish", "result", "vote", "preview", "delete").contains(sub)) {
                if (sender instanceof Player) {
                    String playerName = sender.getName();
                    for (Vars.Election e : Vars.elections.values()) {
                        if (e.owner.equals(playerName)) {
                            if ((sub.equals("vote") || sub.equals("preview")) && !e.published) continue;
                            ids.add(e.id);
                        }
                    }
                }
            }
            return ids;
        }

        return Collections.emptyList();
    }

}
