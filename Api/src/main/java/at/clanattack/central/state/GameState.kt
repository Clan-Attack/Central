package at.clanattack.central.state

enum class GameState(val after: GameState?, val short: String) {

    POST_GAME(null, "post"),
    IN_GAME(POST_GAME, "in"),
    PRE_GAME(IN_GAME, "pre")

}