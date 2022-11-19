package at.clanattack.central.impl.start.timer

import at.clanattack.bootstrap.call.Call
import at.clanattack.bootstrap.call.SystemState
import at.clanattack.central.state.GameState
import at.clanattack.central.top.state.gameState
import at.clanattack.central.top.state.gameStateChangeTime
import at.clanattack.central.top.state.onGameStateChange
import at.clanattack.player.actionbar.ActionbarPriority
import at.clanattack.top.player.sendActionbar
import at.clanattack.top.utility.formatDateUtil
import at.clanattack.top.utility.timerAsync
import org.bukkit.Bukkit

class ActionBar {

    @Call(SystemState.ENABLED)
    fun enabled() {
        if (gameState == GameState.PRE_GAME) start()
        onGameStateChange(GameState.PRE_GAME) { start() }
    }

    private var running: Boolean = false
    private fun start() {
        if (running) return

        running = true
        val timer = timerAsync(5) {
            Bukkit.getOnlinePlayers().forEach {
                it.sendActionbar(
                    ActionbarPriority.NORMAL,
                    2,
                    "central.state.actionbar.start",
                    "time=>${formatDateUtil.formatTimeDiff(gameStateChangeTime)}"
                )
            }
        }

        onGameStateChange {
            if (it != GameState.PRE_GAME) timer.cancel()
        }
    }

}