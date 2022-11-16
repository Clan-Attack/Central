package at.clanattack.central.start

import at.clanattack.bootstrap.provider.ServiceProvider
import at.clanattack.settings.ISettingServiceProvider
import at.clanattack.top.settings.getSetting
import at.clanattack.top.settings.setSetting

@ServiceProvider(IStartServiceProvider::class, [ISettingServiceProvider::class])
class StartServiceProvider : IStartServiceProvider {

    override val started: Boolean
        get() = this.startTime < System.currentTimeMillis()

    override val startTime: Long
        get() = getSetting("central.start.time", System.currentTimeMillis())

    override fun setStartTime(startTime: Long) = setSetting("central.start.time", startTime)

}