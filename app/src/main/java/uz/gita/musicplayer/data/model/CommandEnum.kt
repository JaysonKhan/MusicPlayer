package uz.gita.musicplayer.data.model

enum class CommandEnum(val amount: Int) {
    PREV(1),
    NEXT(2),
    PLAY(3),
    PAUSE(4),
    CLOSE(5),
    MANAGE(6),
    SEEK(7),
    UPDATE_SEEKBAR(8),
    IS_REPEATED(8)
}

