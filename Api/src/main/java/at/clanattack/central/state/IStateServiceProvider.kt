package at.clanattack.central.state

import at.clanattack.bootstrap.provider.IServiceProvider

interface IStateServiceProvider : IServiceProvider {

    val gameState: GameState

    val change: Boolean

    val changeTime: Long

    val timeToChange: Long
        get() = if (changeTime == -1L) -1L else changeTime - System.currentTimeMillis()

    fun cancelChange()

    fun setChangeTime(changeTime: Long)

    fun setState(state: GameState)

    fun onChange(call: (GameState) -> Unit)

    fun onChange(to: GameState, call: () -> Unit)

}