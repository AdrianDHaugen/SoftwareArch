package io.github.some_example_name.models

class Battle {
   private var _playerA = Player()
    var playerA: Player
        get() = _playerA
        set(value) { _playerA = value }

    private var _playerB = Player()
    var playerB: Player
        get() = _playerB
        set(value) { _playerB = value }
}
