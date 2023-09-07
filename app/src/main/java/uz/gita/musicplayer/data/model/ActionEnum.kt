package uz.gita.musicplayer.data.model

enum class ActionEnum(val amount: Int) {
    MANAGE(6), PREV(1),
    NEXT(2), PAUSE(4), SEEK(7)
}