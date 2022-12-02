package at.clanattack.central.impl.start

import at.clanattack.bootstrap.ICore
import at.clanattack.bootstrap.provider.AbstractServiceProvider
import at.clanattack.bootstrap.provider.ServiceProvider
import at.clanattack.central.state.GameState
import at.clanattack.central.state.IStateServiceProvider
import at.clanattack.settings.ISettingServiceProvider
import at.clanattack.top.bootstrap.getServiceProvider
import at.clanattack.top.utility.timerAsync
import at.clanattack.utility.scope.ITask
import at.clanattack.xjkl.scope.asExpr

@ServiceProvider(IStateServiceProvider::class, [ISettingServiceProvider::class])
class StartServiceProvider(core: ICore) : AbstractServiceProvider(core), IStateServiceProvider {

    private val calls = mutableListOf<(GameState) -> Unit>()
    private val specificCalls = mutableMapOf<GameState, MutableList<() -> Unit>>()

    override val gameState: GameState
        get() = getServiceProvider<ISettingServiceProvider>().getSetting("central.state.current", GameState.PRE_GAME)

    override val change: Boolean
        get() = getServiceProvider<ISettingServiceProvider>().getSetting("central.state.change", -1L) != -1L

    override val changeTime: Long
        get() = getServiceProvider<ISettingServiceProvider>().getSetting("central.state.change", -1L)

    override fun cancelChange() = getServiceProvider<ISettingServiceProvider>().setSetting("central.state.change", -1L)

    override fun setChangeTime(changeTime: Long) =
        getServiceProvider<ISettingServiceProvider>().setSetting("central.state.change", changeTime)

    override fun setState(state: GameState) {
        this.cancelChange()
        getServiceProvider<ISettingServiceProvider>().setSetting("central.state.current", state)
    }

    override fun onChange(call: (GameState) -> Unit) = asExpr { this.calls.add(call) }

    override fun onChange(to: GameState, call: () -> Unit) {
        if (this.gameState == to) {
            call()
            return
        }

        specificCalls.putIfAbsent(to, mutableListOf())
        specificCalls[to]!!.add(call)
        this.checkListener()
    }

    private var running: Boolean = false

    private fun checkListener() {
        if (running) return

        val startState = this.gameState
        if (!change && calls.isEmpty() && specificCalls.getOrDefault(startState, emptyList()).isEmpty()) return

        running = true
        timerAsync(20) {
            val currentState = gameState
            if (startState != currentState) {
                handleStateChange(currentState, this)
                return@timerAsync
            }

            if (!change) return@timerAsync
            if (System.currentTimeMillis() > changeTime) {
                val after = startState.after ?: return@timerAsync asExpr { cancelChange() }

                setState(after)

                handleStateChange(after, this)
                return@timerAsync
            }
        }
    }

    private fun handleStateChange(newState: GameState, task: ITask) {
        specificCalls[newState]?.forEach { it() }
        calls.forEach { it(newState) }

        task.cancel()
        running = false
        checkListener()
    }

}
