package at.clanattack.central.impl.start.listener

import at.clanattack.central.state.GameState
import at.clanattack.central.top.state.gameState
import at.clanattack.utility.listener.ListenerPriority
import at.clanattack.utility.listener.ListenerTrigger
import at.clanattack.utility.listener.Priority
import org.bukkit.attribute.Attribute
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent

class Player {

    @Priority(ListenerPriority.HIGHEST)
    @ListenerTrigger(PlayerInteractEvent::class)
    fun playerInteract(event: PlayerInteractEvent) {
        if (gameState != GameState.IN_GAME) event.isCancelled = true
    }

    @ListenerTrigger(PlayerJoinEvent::class)
    fun playerJoin(event: PlayerJoinEvent) {
        if (gameState == GameState.IN_GAME) return

        event.player.allowFlight = true
        event.player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.baseValue = 20.0
        event.player.health = event.player.getAttribute(Attribute.GENERIC_MAX_HEALTH)?.value ?: 20.0

        event.player.foodLevel = 20
        event.player.level = 0
        event.player.exp = 0F
    }

}