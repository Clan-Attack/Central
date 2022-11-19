package at.clanattack.central.impl.start.listener

import at.clanattack.central.state.GameState
import at.clanattack.central.top.state.gameState
import at.clanattack.utility.listener.ListenerPriority
import at.clanattack.utility.listener.ListenerTrigger
import at.clanattack.utility.listener.Priority
import org.bukkit.event.entity.EntityChangeBlockEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityTargetEvent

class EntityListener {

    @Priority(ListenerPriority.HIGHEST)
    @ListenerTrigger(EntityChangeBlockEvent::class)
    fun entityChangeBlock(event: EntityChangeBlockEvent) {
        if (gameState != GameState.IN_GAME) event.isCancelled = true
    }

    @Priority(ListenerPriority.HIGHEST)
    @ListenerTrigger(EntityDamageEvent::class)
    fun entityDamage(event: EntityDamageEvent) {
        if (gameState != GameState.IN_GAME) event.isCancelled = true
    }

    @Priority(ListenerPriority.HIGHEST)
    @ListenerTrigger(EntityTargetEvent::class)
    fun entityTarget(event: EntityTargetEvent) {
        if (gameState != GameState.IN_GAME) event.isCancelled = true
    }

}