package at.clanattack.central.impl.start.listener

import at.clanattack.central.state.GameState
import at.clanattack.central.top.state.gameState
import at.clanattack.utility.listener.ListenerPriority
import at.clanattack.utility.listener.ListenerTrigger
import at.clanattack.utility.listener.Priority
import org.bukkit.event.entity.ExplosionPrimeEvent

class Environment {

    @Priority(ListenerPriority.HIGHEST)
    @ListenerTrigger(ExplosionPrimeEvent::class)
    fun explosionPrime(event: ExplosionPrimeEvent) {
        if (gameState != GameState.IN_GAME) event.isCancelled = true
    }

}