package eu.raidersheaven.rhsignitem.commands;

import com.mojang.brigadier.tree.LiteralCommandNode;
import eu.raidersheaven.rhsignitem.Main;
import eu.raidersheaven.rhsignitem.commands.rename.Rename;
import eu.raidersheaven.rhsignitem.commands.sign.*;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.Commands;

public class CommandNode {

    /**
     * /sign command
     * @param plugin
     * @return
     */
    public static LiteralCommandNode<CommandSourceStack> createSign(Main plugin) {
        return Commands.literal("sign")
                .requires(stack -> stack.getSender().hasPermission("RHSignItem.*") || stack.getSender().hasPermission("RHSignItem.command.*") || stack.getSender().hasPermission("RHSignItem.command.sign"))
                .executes(Sign::fastSignItem)
                .then(Sign.register(plugin))
                .then(Help.register(plugin))
                .then(Unlock.register(plugin))
                .then(Lock.register(plugin))
                .then(Delete.register(plugin))
                .then(Version.register(plugin))
                .build();
    }


    /**
     * /rename command
     * @param plugin
     * @return
     */
    public static LiteralCommandNode<CommandSourceStack> createRename(Main plugin) {
        return Commands.literal("rename")
                .requires(stack -> stack.getSender().hasPermission("RHSignItem.*") || stack.getSender().hasPermission("RHSignItem.command.*") || stack.getSender().hasPermission("RHSignItem.command.rename"))
                .executes(Help::displayConfigList)
                .then(Rename.register(plugin))
                .build();
    }
}
