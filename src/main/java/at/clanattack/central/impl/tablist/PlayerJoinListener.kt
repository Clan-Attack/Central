package at.clanattack.central.impl.tablist

import at.clanattack.bootstrap.call.Call
import at.clanattack.bootstrap.call.SystemState
import at.clanattack.top.message.getMessage
import at.clanattack.top.message.getStringMessage
import at.clanattack.top.player.onlineTime
import at.clanattack.top.utility.formatDateUtil
import at.clanattack.top.utility.timerAsync
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit

class PlayerJoinListener {

    @Call(SystemState.ENABLED)
    fun enabled() {
        timerAsync(5) {
            val currentPlayers = Bukkit.getOnlinePlayers().size
            val maxPlayers = Bukkit.getMaxPlayers()

            Bukkit.getOnlinePlayers().forEach {
                val placeholders = arrayOf(
                    "currentPlayers=>$currentPlayers",
                    "maxPlayers=>$maxPlayers",
                    "onlineTime=>${formatDateUtil.formatTimeDiff(0, it.onlineTime)}",
                    "x=>${it.location.blockX}",
                    "y=>${it.location.blockY}",
                    "z=>${it.location.blockZ}",
                    "direction=>${getStringMessage("central.tablist.facing.${it.facing.name.lowercase()}")}",
                    "name=>${it.name}",
                    "ping=>${it.ping}",
                    "pingColor=>&${getPingColor(it.ping).asHexString()}"
                )

                it.sendPlayerListHeaderAndFooter(
                    getMessage("central.tablist.header", *placeholders),
                    getMessage("central.tablist.footer", *placeholders)
                )
            }

        }
    }

    private fun getPingColor(ping: Int) = when {
        ping <= 20 -> NamedTextColor.DARK_GREEN
        ping <= 30 -> NamedTextColor.GREEN
        ping <= 50 -> NamedTextColor.YELLOW
        ping <= 100 -> NamedTextColor.RED
        else -> NamedTextColor.DARK_RED
    }

}