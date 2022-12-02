package at.clanattack.central.impl.motd

import at.clanattack.bootstrap.ICore
import at.clanattack.central.top.state.gameState
import at.clanattack.central.top.state.timeToGameStateChange
import at.clanattack.top.message.getMessage
import at.clanattack.top.utility.formatDateUtil
import at.clanattack.utility.listener.ListenerTrigger
import com.destroystokyo.paper.event.server.PaperServerListPingEvent
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.JoinConfiguration
import org.bukkit.Bukkit
import org.bukkit.util.CachedServerIcon
import java.io.File

class ServerListPingListener(core: ICore) {

    private val icon: CachedServerIcon

    init {
        val file = File(core.dataFolder, "icon.png")
        if (!file.exists()) throw IllegalStateException("Server icon is not set.")

        icon = Bukkit.loadServerIcon(file)
    }

    @ListenerTrigger(PaperServerListPingEvent::class)
    fun serverListPing(event: PaperServerListPingEvent) {
        val replacements = arrayOf(
            "onlinePlayer=>${event.numPlayers}",
            "maxPlayer=>${event.maxPlayers}",
            "freeSlot=>${event.maxPlayers - event.numPlayers}",
            "time=>${formatDateUtil.formatTimeDiff(timeToGameStateChange)}"
        )

        event.motd(
            Component.join(
                JoinConfiguration.newlines(),
                getMessage("central.ping.motd.${gameState.short}.1", *replacements),
                getMessage("central.ping.motd.${gameState.short}.2", *replacements)
            )
        )

        event.serverIcon = this.icon
    }

}