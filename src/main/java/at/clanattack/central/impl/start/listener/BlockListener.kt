package at.clanattack.central.impl.start.listener

import at.clanattack.central.state.GameState
import at.clanattack.central.top.state.gameState
import at.clanattack.utility.listener.ListenerPriority
import at.clanattack.utility.listener.ListenerTrigger
import at.clanattack.utility.listener.Priority
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockFromToEvent
import org.bukkit.event.block.BlockPhysicsEvent

class BlockListener {

    @Priority(ListenerPriority.HIGHEST)
    @ListenerTrigger(BlockPhysicsEvent::class)
    fun blockPhysics(event: BlockPhysicsEvent) {
        if (gameState != GameState.IN_GAME) event.isCancelled = true
    }

    @Priority(ListenerPriority.HIGHEST)
    @ListenerTrigger(BlockFromToEvent::class)
    fun blockFromTo(event: BlockFromToEvent) {
        if (gameState != GameState.IN_GAME) event.isCancelled = true
    }

}