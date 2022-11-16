package at.clanattack.central.start

import at.clanattack.bootstrap.provider.IServiceProvider

interface IStartServiceProvider : IServiceProvider {

    val started: Boolean

    val startTime: Long

    fun setStartTime(startTime: Long)

}