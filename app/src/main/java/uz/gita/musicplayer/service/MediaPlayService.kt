package uz.gita.musicplayer.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Color
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.ServiceCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import uz.gita.musicplayer.MainActivity
import uz.gita.musicplayer.R
import uz.gita.musicplayer.data.model.CommandEnum
import uz.gita.musicplayer.data.model.CursorEnum
import uz.gita.musicplayer.data.model.MusicData
import uz.gita.musicplayer.utils.MyEventBus
import uz.gita.musicplayer.utils.getMusicDataByPosition
import uz.gita.musicplayer.utils.getMusicImage

class MediaPlayService: Service() {

    companion object {
        const val CHANNEL_ID = "KHAN MUSIC"
        const val CHANNEL_NAME = "Music347"
    }

    private var _musicPlayer: MediaPlayer? = null
    private val musicPlayer get() = _musicPlayer!!

    override fun onBind(intent: Intent?): IBinder? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())
    private var job: Job? = null

    override fun onCreate() {
        super.onCreate()
        createChannel()
    }

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_LOW
            val mChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance)

            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotification(musicData: MusicData) {
        musicData.albumArt = MyEventBus.storageCursor!!.getMusicImage()
        val myIntent = Intent(this, MainActivity::class.java).apply {
            Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val myPendingIntent = PendingIntent.getActivity(this, 1, myIntent, PendingIntent.FLAG_IMMUTABLE)

        val notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setCustomContentView(createRemoteView(musicData))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setContentIntent(myPendingIntent)
            .setOnlyAlertOnce(true)
            .setOngoing(true)

        startForeground(2, notificationBuilder.build())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createRemoteView(musicData: MusicData): RemoteViews {
        val view = RemoteViews(this.packageName, R.layout.remote_view)
        view.setTextViewText(R.id.textMusicName, musicData.title)
        view.setTextViewText(R.id.textArtistName, musicData.artist)
        if (musicData.albumArt != null)
            view.setImageViewBitmap(R.id.frombitmap, musicData.albumArt)
        else
            view.setImageViewResource(R.id.frombitmap, R.drawable.img)
        if (_musicPlayer != null && !musicPlayer.isPlaying) {
            view.setImageViewResource(R.id.buttonManage, R.drawable.img_play)
        } else {
            view.setImageViewResource(R.id.buttonManage, R.drawable.img_pause)
        }

        view.setOnClickPendingIntent(R.id.buttonPrev, createPendingIntent(CommandEnum.PREV))
        view.setOnClickPendingIntent(R.id.buttonManage, createPendingIntent(CommandEnum.MANAGE))
        view.setOnClickPendingIntent(R.id.buttonNext, createPendingIntent(CommandEnum.NEXT))
        view.setOnClickPendingIntent(R.id.buttonCancel, createPendingIntent(CommandEnum.CLOSE))
        return view
    }

    private fun createPendingIntent(commandEnum: CommandEnum): PendingIntent {
        val intent = Intent(this, MediaPlayService::class.java)
        intent.putExtra("COMMAND", commandEnum)
        return PendingIntent.getService(
            this,
            commandEnum.amount,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {

        if (MyEventBus.currentCursorEnum == CursorEnum.STORAGE &&
            (MyEventBus.storageCursor == null || MyEventBus.storagePos.value == -1)
        ) return START_NOT_STICKY
        else if (MyEventBus.currentCursorEnum == CursorEnum.SAVED &&
            (MyEventBus.roomCursor == null || MyEventBus.roomPos == -1)
        ) return START_NOT_STICKY

        val command = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            intent?.extras?.getSerializable("COMMAND", CommandEnum::class.java)
        } else {
            intent?.extras?.getSerializable("COMMAND") as CommandEnum
        }
        if (command != null) {
            doneCommand(command)
            if (command.name != CommandEnum.CLOSE.name && MyEventBus.currentCursorEnum == CursorEnum.SAVED) {
                createNotification(MyEventBus.roomCursor!!.getMusicDataByPosition(MyEventBus.roomPos))
            } else if (command.name != CommandEnum.CLOSE.name && MyEventBus.currentCursorEnum == CursorEnum.STORAGE) {
                createNotification(MyEventBus.storageCursor!!.getMusicDataByPosition(MyEventBus.storagePos.value))
            }
        }
        return START_NOT_STICKY
    }

    private fun doneCommand(commandEnum: CommandEnum) {
        when (commandEnum) {
            CommandEnum.MANAGE -> {
                if (musicPlayer.isPlaying) doneCommand(CommandEnum.PAUSE)
                else doneCommand(CommandEnum.SEEK)
            }

            CommandEnum.SEEK -> {
                scope.launch { MyEventBus.isPlaying.emit(true) }
                job = moveProgress().onEach { MyEventBus.currentTimeFlow.emit(it) }.launchIn(scope)
                musicPlayer.seekTo(MyEventBus.currentTime.value)
                musicPlayer.start()
            }

            CommandEnum.UPDATE_SEEKBAR -> {
                if (musicPlayer.isPlaying) {
                    job?.cancel()
                    musicPlayer.seekTo(MyEventBus.currentTime.value)
                    job = moveProgress().onEach { MyEventBus.currentTimeFlow.emit(it) }
                        .launchIn(scope)
                } else {
                    job?.cancel()
                    musicPlayer.seekTo(MyEventBus.currentTime.value)
                    job = moveProgress().onEach { MyEventBus.currentTimeFlow.emit(it) }
                        .launchIn(scope)
                }
            }

            CommandEnum.PREV -> {
                if (!MyEventBus.isRepeated) {
                    if (MyEventBus.currentCursorEnum == CursorEnum.SAVED) {
                        if (MyEventBus.roomPos - 1 == -1) {
                            MyEventBus.roomPos = MyEventBus.roomCursor!!.count - 1
                        } else if (MyEventBus.roomPos == MyEventBus.roomCursor!!.count) {
                            MyEventBus.currentCursorEnum = CursorEnum.STORAGE
                            scope.launch {
                                MyEventBus.storagePos.emit(0)
                            }
                        } else {
                            --MyEventBus.roomPos
                        }
                    } else {
                        if (MyEventBus.storagePos.value - 1 == -1) {
                            scope.launch {
                                MyEventBus.storagePos.emit(MyEventBus.storageCursor!!.count - 1)
                            }
                        } else {
                            scope.launch {
                                MyEventBus.storagePos.emit(MyEventBus.storagePos.value-1)
                            }
                        }
                    }
                }
                doneCommand(CommandEnum.PLAY)
            }

            CommandEnum.NEXT -> {
                if (!MyEventBus.isRepeated) {
                    if (MyEventBus.currentCursorEnum == CursorEnum.SAVED) {
                        if (MyEventBus.roomPos + 1 == MyEventBus.roomCursor!!.count) {
                            MyEventBus.roomPos = 0
                        } else if (MyEventBus.roomPos == MyEventBus.roomCursor!!.count) {
                            MyEventBus.currentCursorEnum = CursorEnum.STORAGE
                            scope.launch {
                                MyEventBus.storagePos.emit(0)
                            }
                        } else {
                            ++MyEventBus.roomPos
                        }
                    } else {
                        if (MyEventBus.storagePos.value + 1 == MyEventBus.storageCursor!!.count) {
                            scope.launch {
                                MyEventBus.storagePos.emit(0)
                            }
                        } else {
                            scope.launch {
                                MyEventBus.storagePos.emit(MyEventBus.storagePos.value+1)
                            }
                        }
                    }
                }
                doneCommand(CommandEnum.PLAY)
            }

            CommandEnum.PLAY -> {
                scope.launch { MyEventBus.isPlaying.emit(true) }
                job?.cancel()
                val data =
                    if (MyEventBus.currentCursorEnum == CursorEnum.SAVED) MyEventBus.roomCursor!!.getMusicDataByPosition(
                        MyEventBus.roomPos
                    ) else MyEventBus.storageCursor!!.getMusicDataByPosition(MyEventBus.storagePos.value)

                scope.launch { MyEventBus.currentMusicData.emit(data) }

                MyEventBus.currentTime.value = 0
                MyEventBus.totalTime = data.duration.toInt()
                _musicPlayer?.stop()
                _musicPlayer = MediaPlayer.create(this, Uri.parse(data.data))
                musicPlayer.seekTo(MyEventBus.currentTime.value)
                musicPlayer.setOnCompletionListener { doneCommand(CommandEnum.NEXT) }

                job = moveProgress().onEach { MyEventBus.currentTimeFlow.emit(it) }.launchIn(scope)

                musicPlayer.start()
            }

            CommandEnum.PAUSE -> {
                scope.launch { MyEventBus.isPlaying.emit(false) }
                job?.cancel()
                musicPlayer.pause()
                MyEventBus.currentTime.value = MyEventBus.currentTimeFlow.value
                musicPlayer.seekTo(MyEventBus.currentTime.value)
            }

            CommandEnum.CLOSE -> {
                scope.launch { MyEventBus.isPlaying.emit(false) }
                job?.cancel()
                musicPlayer.pause()
                ServiceCompat.stopForeground(this, ServiceCompat.STOP_FOREGROUND_REMOVE)
            }

            CommandEnum.IS_REPEATED -> {
                MyEventBus.isRepeated = !MyEventBus.isRepeated
                scope.launch {
                    MyEventBus.isRepeatedFlow.emit(!MyEventBus.isRepeatedFlow.value)
                }
            }
        }
    }

    private fun moveProgress(): Flow<Int> = flow {
        for (i in MyEventBus.currentTime.value until MyEventBus.totalTime step 1000) {
            emit(i)
            delay(1000)
        }
    }
}


fun getColorFromBitmap(musicPic: Bitmap): Int {
    var redBucket: Long = 0
    var greenBucket: Long = 0
    var blueBucket: Long = 0
    var pixelCount: Long = 0

    for (y in 0 until musicPic.height) {
        for (x in 0 until musicPic.width) {
            val c = musicPic.getPixel(x, y)

            pixelCount++
            redBucket += Color.red(c).toLong()
            greenBucket += Color.green(c).toLong()
            blueBucket += Color.blue(c).toLong()
            // does alpha matter?
        }
    }

    return  Color.rgb(
        (redBucket / pixelCount).toInt(),
        (greenBucket / pixelCount).toInt(),
        (blueBucket / pixelCount).toInt())
}
