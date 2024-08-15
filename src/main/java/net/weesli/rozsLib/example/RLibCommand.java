package net.weesli.rozsLib.example;

import net.weesli.rozsLib.ColorManager.ColorBuilder;
import net.weesli.rozsLib.CommandBuilder;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;
import java.util.List;


/**
 * Example CommandBuilder usage
 * .. extends CommandBuilder
 */
public class RLibCommand extends CommandBuilder {

    public RLibCommand(Plugin plugin) {
        super(plugin);
    }


    @Override
    protected boolean Command(CommandSender sender, Command command, String s, String[] strings) {
        sender.sendMessage(ColorBuilder.convertColors("&bRunning RLib "+getPlugin().getDescription().getVersion()));
        return false;
    }

    @Override
    protected List<String> TabComplete(CommandSender sender, Command command, String s, String[] strings) {
        return List.of();
    }
}
