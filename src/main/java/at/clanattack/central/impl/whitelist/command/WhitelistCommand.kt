package at.clanattack.central.impl.whitelist.command

import at.clanattack.central.impl.whitelist.modle.WhitelistState
import at.clanattack.top.message.getMessage
import at.clanattack.top.player.getPlayer
import at.clanattack.utility.command.Command
import org.bukkit.command.CommandSender

class WhitelistCommand : Command("whitelist", permission = "ca.central.whitelist.command") {

    override fun execute(sender: CommandSender, args: Array<out String>) {
        if (args.size != 2)  {
            sender.sendMessage(getMessage("central.whitelist.command.syntax"))
            return
        }

        val player = getPlayer(args[0])
        if (player == null) {
            sender.sendMessage(getMessage("core.player.unknown", "name=>${args[0]}"))
            return
        }

        val allow = if (args[1].equals("allow", true)) true
        else if (args[1].equals("block", true)) false
        else null

        if (allow == null) {
            sender.sendMessage(getMessage("central.whitelist.command.state.unknown", "state=>${args[1]}"))
            return
        }

        player.setPlayerData("central.whitelist.state", if (allow) WhitelistState.ALLOWED else WhitelistState.BLOCKED)
        sender.sendMessage(getMessage("central.whitelist.command.${if (allow) "allowed" else "blocked"}",
            "name=>${player.name}"))

        player.bukkit?.kick(getMessage("central.whitelist.command.kick"))
    }

    override fun tabComplete(sender: CommandSender, args: Array<out String>, completions: MutableList<String>) {
        if (args.size != 2) return
        completions.addAll(arrayOf("allow", "block"))
    }

}