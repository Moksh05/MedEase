package com.example.medease

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.view.SurfaceView
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.agora.base.internal.ContextUtils
import io.agora.rtc2.ChannelMediaOptions
import io.agora.rtc2.Constants
import io.agora.rtc2.IRtcEngineEventHandler
import io.agora.rtc2.RtcEngine
import io.agora.rtc2.RtcEngineConfig
import io.agora.rtc2.video.VideoCanvas

class VideoChatActivity : AppCompatActivity() {

    fun showMessage(message: String?) {
        runOnUiThread {
            Toast.makeText(
                ContextUtils.getApplicationContext(),
                message,
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    // Fill the App ID of your project generated on Agora Console.
    // Fill the App ID of your project generated on Agora Console.
    val appId = "873c51309e14457582e9af3da140a064"
var calljoined = false
var video = true
    var isMuted = false
    // Fill the channel name.
// Fill the channel name.
    val channelName = "MedEase"

    // Fill the temp token generated on Agora Console.
// Fill the temp token generated on Agora Console.
    val token = "007eJxTYLj2Muz9J8lbWafbJz35FT3xcqzn7rC5ea03pZw2+T2/ZP5agcHC3DjZ1NDYwDLV0MTE1NzUwijVMjHNOCXR0MQg0cDMJKyhJrUhkJFBL/s4MyMDBIL47Ay+qSmuicWpDAwAto8ixg=="
    //that identifies the local user.
// An integer that identifies the local user.
    val uid = 0
    var isJoined = false

    private lateinit var agoraEngine: RtcEngine

    //SurfaceView to render local video in a Container.
//SurfaceView to render local video in a Container.
    lateinit var localSurfaceView: SurfaceView

    //SurfaceView to render Remote video in a Container.
//SurfaceView to render Remote video in a Container.
    lateinit var remoteSurfaceView: SurfaceView

    private val PERMISSION_REQ_ID = 22
    private val REQUESTED_PERMISSIONS = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.RECORD_AUDIO

    )

    private fun checkSelfPermission(): Boolean {
        val permissionsGranted = REQUESTED_PERMISSIONS.all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
        return permissionsGranted
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_chat)

        if (!checkSelfPermission()) {
            ActivityCompat.requestPermissions(this, REQUESTED_PERMISSIONS, PERMISSION_REQ_ID);
        }

        findViewById<Button>(R.id.end_call_button).setText("Join")
        setupVideoSDKEngine()


//        findViewById<Button>(R.id.video_button).setOnClickListener { view ->
//            video = !video // Toggle the video state
//
//            // Enable or disable local video based on the state
//            agoraEngine.enableLocalVideo(video)
//
//            if (!video) {
//                findViewById<FrameLayout>(R.id.local_video_view).visibility = View.GONE
//                localSurfaceView.visibility = View.GONE
//                findViewById<Button>(R.id.video_button).setText("Turn On")
//            } else {
//                // If video is turned on, show the localSurfaceView
//                findViewById<FrameLayout>(R.id.local_video_view).visibility = View.VISIBLE
//                localSurfaceView.visibility = View.VISIBLE
//                findViewById<Button>(R.id.video_button).setText("Turn off")
//
//            }
//        }

        findViewById<Button>(R.id.end_call_button).setOnClickListener{ view->
            if (calljoined){
                calljoined = false
                leaveChannel(view)

                startActivity((Intent(this,Home::class.java)))
            }else {
                calljoined = true
                findViewById<Button>(R.id.end_call_button).setText("End")
                joinChannel(view)
            }
        }





    }

    private fun setupVideoSDKEngine() {
        try {
            val config = RtcEngineConfig()
            config.mContext = baseContext
            config.mAppId = appId
            config.mEventHandler = mRtcEventHandler
            agoraEngine = RtcEngine.create(config)
            // By default, the video module is disabled, call enableVideo to enable it.
            agoraEngine.enableVideo()
            setupLocalVideo()
        } catch (e: Exception) {
            showMessage(e.toString())
        }
    }


    private val mRtcEventHandler: IRtcEngineEventHandler = object : IRtcEngineEventHandler() {
        // Listen for the remote host joining the channel to get the uid of the host.
        override fun onUserJoined(uid: Int, elapsed: Int) {
            showMessage("Remote user joined $uid")

            // Set the remote video view
            runOnUiThread { setupRemoteVideo(uid) }
        }

        override fun onJoinChannelSuccess(channel: String, uid: Int, elapsed: Int) {
            isJoined = true
            showMessage("Joined Channel $channel")
        }

        override fun onUserOffline(uid: Int, reason: Int) {
            showMessage("Remote user offline $uid $reason")
            runOnUiThread { remoteSurfaceView.visibility = View.GONE }
        }
    }

    private fun setupRemoteVideo(uid: Int) {
        val container = findViewById<FrameLayout>(R.id.remote_video_view)
        remoteSurfaceView = SurfaceView(baseContext)
        remoteSurfaceView.setZOrderMediaOverlay(true)
        container.addView(remoteSurfaceView)
        agoraEngine.setupRemoteVideo(
            VideoCanvas(
                remoteSurfaceView,
                VideoCanvas.RENDER_MODE_FIT,
                uid
            )
        )
        // Display RemoteSurfaceView.
        remoteSurfaceView.visibility = View.VISIBLE
    }


    private fun setupLocalVideo() {
        val container = findViewById<FrameLayout>(R.id.local_video_view)
        // Create a SurfaceView object and add it as a child to the FrameLayout.
        localSurfaceView = SurfaceView(baseContext)
        container.addView(localSurfaceView)
        // Call setupLocalVideo with a VideoCanvas having uid set to 0.
        agoraEngine.setupLocalVideo(
            VideoCanvas(
                localSurfaceView,
                VideoCanvas.RENDER_MODE_HIDDEN,
                0
            )
        )
    }

    fun joinChannel(view: View?) {

        val options = ChannelMediaOptions()

        // For a Video call, set the channel profile as COMMUNICATION.
        options.channelProfile = Constants.CHANNEL_PROFILE_COMMUNICATION
        // Set the client role as BROADCASTER or AUDIENCE according to the scenario.
        options.clientRoleType = Constants.CLIENT_ROLE_BROADCASTER
        // Display LocalSurfaceView.
        setupLocalVideo()
        localSurfaceView.visibility = View.VISIBLE
        // Start local preview.
        agoraEngine.startPreview()
        // Join the channel with a temp token.
        // You need to specify the user ID yourself, and ensure that it is unique in the channel.
        agoraEngine.joinChannel(token, channelName, uid, options)

    }

    fun leaveChannel(view: View?) {
        if (!isJoined) {

        } else {
            agoraEngine.leaveChannel()
            showMessage("You left the channel")
            // Stop remote video rendering.
            if (remoteSurfaceView != null) remoteSurfaceView.visibility = View.GONE
            // Stop local video rendering.
            if (localSurfaceView != null) localSurfaceView.visibility = View.GONE
            isJoined = false

            startActivity(Intent(this,Home::class.java))
            finish()
        }
    }


//    fun onMuteButtonClicked(view: View) {
//        isMuted = !isMuted
//        if (!isMuted) {
//            agoraEngine.muteLocalAudioStream(false) // Unmute audio
//            view.findViewById<Button>(R.id.mute_button).backgroundTintList = ColorStateList.valueOf(
//                Color.parseColor("#FA0000"))
//            view.findViewById<Button>(R.id.mute_button).setText("Mute")
//        } else {
//            agoraEngine.muteLocalAudioStream(true) // Mute audio
//            view.findViewById<Button>(R.id.mute_button).backgroundTintList = ColorStateList.valueOf(
//                Color.RED)
//            view.findViewById<Button>(R.id.mute_button).setText("UnMute")
//        }
//    }


    override fun onDestroy() {
        super.onDestroy()
        agoraEngine.stopPreview()
        agoraEngine.leaveChannel()

        // Destroy the engine in a sub-thread to avoid congestion
        if (this::agoraEngine.isInitialized) {
            Thread {
                RtcEngine.destroy()
            }.start()
        }
    }

}