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
import at.clanattack.top.utility.formatDateUtil
import at.clanattack.top.utility.timerAsync
import at.clanattack.utility.scope.ITask
import org.bukkit.Bukkit

@ServiceProvider(IStartServiceProvider::class, [ISettingServiceProvider::class])
class StartServiceProvider(core: ICore) : AbstractServiceProvider(core), IStartServiceProvider {

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
        var timer: ITask? = null

        timer = timerAsync(5) {
            if (this.started) {
                timer?.cancel()
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
    }

}