package at.clanattack.central.state

enum class GameState(val after: GameState?) {

    POST_GAME(null),
    IN_GAME(POST_GAME),
    PRE_GAME(IN_GAME)

}