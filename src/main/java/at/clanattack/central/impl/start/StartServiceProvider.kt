package at.clanattack.central.impl.start

import at.clanattack.bootstrap.ICore
import at.clanattack.bootstrap.provider.AbstractServiceProvider
import at.clanattack.bootstrap.provider.ServiceProvider
import at.clanattack.central.start.IStartServiceProvider
import at.clanattack.player.actionbar.ActionbarPriority
import at.clanattack.settings.ISettingServiceProvider
import at.clanattack.top.player.sendActionbar
import at.clanattack.top.settings.getSetting
import at.clanattack.top.settings.setSetting
import at.clanattack.top.utility.async
import at.clanattack.top.utility.formatDateUtil
import at.clanattack.top.utility.timerAsync
import org.bukkit.Bukkit
import org.bukkit.entity.Player

@ServiceProvider(IStartServiceProvider::class, [ISettingServiceProvider::class])
class StartServiceProvider(core: ICore) : AbstractServiceProvider(core), IStartServiceProvider {

    private val distance = mutableMapOf<Pair<Int, Int>, Int>()

    override val started: Boolean
        get() = this.startTime < System.currentTimeMillis()

    override val startTime: Long
        get() = getSetting("central.start.time", System.currentTimeMillis())

    override fun setStartTime(startTime: Long) {
        setSetting("central.start.time", startTime)
        startTimer()
    }

    override fun load() {
        startTimer()
    }

    private fun startTimer() {
        if (this.started) return

        timerAsync(5) {
            if (started) {
                this.cancel()
                return@timerAsync
            }

            Bukkit.getOnlinePlayers().forEach {
                it.sendActionbar(
                    ActionbarPriority.NORMAL,
                    2000,
                    "central.start.actionbar",
                    "time=>${formatDateUtil.formatTimeDiff(startTime)}"
                )
            }
        }

        timerAsync(1) {
            if (started) {
                distance.clear()
                this.cancel()
                return@timerAsync
            }

            Bukkit.getOnlinePlayers().forEach { player ->
                // Async to run every player synchronized
                async {
                    if(calculatePlayer(player)) return@async

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

                    distance[position] = highestBlocks.average().toInt() + 50
                    calculatePlayer(player)
                }
            }

        }
    }

    private fun calculatePlayer(player: Player): Boolean {
        val y = player.location.blockY
        val position = player.location.blockX to player.location.blockZ

        if (position in distance) {
            if (y <= distance[position]!!) {
                val velocity = player.velocity
                if (velocity.y < 5) velocity.y += 0.5
                player.velocity = velocity
            }

            return true
        }

        return false
    }

}
