package org.dmitriykotik.voting;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public class Vote_TabCompleter implements TabCompleter{
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (!sender.hasPermission("voting.vote")) return Collections.emptyList();
        if (args.length <= 1) {
            return Vars.elections.values().stream()
                .filter(e -> e.published)
                .map(e -> e.id)
                .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }

}
