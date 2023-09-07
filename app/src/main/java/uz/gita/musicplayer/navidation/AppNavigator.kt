package uz.gita.musicplayer.navidation

interface AppNavigator {
    suspend fun navigateTo(screen: MyScreen)
    suspend fun back()
}