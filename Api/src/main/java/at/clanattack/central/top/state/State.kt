package at.clanattack.central.top.state

import at.clanattack.central.state.GameState
import at.clanattack.central.state.IStateServiceProvider
import at.clanattack.top.bootstrap.getServiceProvider


private val serviceProvider: IStateServiceProvider
    get() = getServiceProvider()

val gameState: GameState
    get() = serviceProvider.gameState

val changeGameState: Boolean
    get() = serviceProvider.change

val gameStateChangeTime: Long
    get() = serviceProvider.changeTime

val timeToGameStateChange: Long
    get() = serviceProvider.timeToChange

fun cancelGameStateChange() = serviceProvider.cancelChange()

fun setGameStateChangeTime(changeTime: Long) = serviceProvider.setChangeTime(changeTime)

fun setGameState(state: GameState) = serviceProvider.setState(state)

fun onGameStateChange(call: (GameState) -> Unit) = serviceProvider.onChange(call)

fun onGameStateChange(to: GameState, call: () -> Unit) = serviceProvider.onChange(to, call)
