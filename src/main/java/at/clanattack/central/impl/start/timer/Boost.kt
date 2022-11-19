package at.clanattack.central.impl.start.timer

import at.clanattack.bootstrap.call.Call
import at.clanattack.bootstrap.call.SystemState
import at.clanattack.central.state.GameState
import at.clanattack.central.top.state.gameState
import at.clanattack.central.top.state.onGameStateChange
import at.clanattack.top.utility.async
import at.clanattack.top.utility.timerAsync
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class Boost {

    private val distances = mutableMapOf<Pair<Int, Int>, Int>()
    private var running: Boolean = false

    @Call(SystemState.ENABLED)
    fun enabled() {
        if (gameState != GameState.IN_GAME) start()

        onGameStateChange(GameState.PRE_GAME) { start() }
        onGameStateChange(GameState.POST_GAME) { start() }
    }

    private fun start() {
        if (running) return

        running = true
        val timer = timerAsync(1) {
            Bukkit.getOnlinePlayers().forEach { player ->
                // Async to run every player in sync
                async {
                    if (calculatePlayer(player)) return@async

                    val world = player.location.world
                    val position = player.location.blockX to player.location.blockZ
                    val highestBlocks = mutableListOf<Int>()
                    for (x in -15..15) {
                        for (z in -15..15) highestBlocks.add(
                            world.getHighestBlockYAt(
                                position.first + x,
                                position.second + z
                            )
                        )
                    }

                    distances[position] = highestBlocks.average().toInt() + 100
                    calculatePlayer(player)
                }
            }
        }

        onGameStateChange {
            if (it == GameState.IN_GAME) {
                timer.cancel()
                distances.clear()
            }
        }
    }

    private fun calculatePlayer(player: Player): Boolean {
        val y = player.location.blockY
        val position = player.location.blockX to player.location.blockZ

        if (position in distances) {
            if (y <= distances[position]!!) {
                val velocity = player.velocity
                if (velocity.y < 2) velocity.y += 0.2
                player.velocity = velocity
            }

            return true
        }

        return false
    }

}