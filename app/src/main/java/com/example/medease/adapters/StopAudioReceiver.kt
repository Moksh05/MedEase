import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import com.example.medease.R

class StopAudioReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        // Stop audio playback
        val mp = MediaPlayer.create(context, R.raw.alarm_audio)
        if (mp.isPlaying) {
            mp.stop()
            mp.release()
        }
    }
}
