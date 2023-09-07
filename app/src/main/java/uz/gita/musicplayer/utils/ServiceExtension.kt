package uz.gita.musicplayer.utils

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import uz.gita.musicplayer.data.model.CommandEnum
import uz.gita.musicplayer.service.MediaPlayService

fun startMusicService(context: Context, commandEnum: CommandEnum) {
    val intent = Intent(context, MediaPlayService::class.java)
    intent.putExtra("COMMAND", commandEnum)
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        context.startForegroundService(intent)
    } else context.startService(intent)
}
fun getBitmap(byteArray: ByteArray?): Bitmap? {
    if (byteArray != null) {
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
    return null
}