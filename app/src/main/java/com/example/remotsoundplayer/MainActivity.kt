package com.example.remotsoundplayer

import android.app.Activity
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import fi.iki.elonen.NanoHTTPD
import java.net.NetworkInterface

class MainActivity : AppCompatActivity() {
    private val TAG: String = this::class.simpleName!!
    private val PORT = 12345
    private lateinit var server: App

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
    }

    override fun onStart() {
        super.onStart()
        server = App(PORT, this)
        val text = findViewById<TextView>(R.id.textview_first)

        for (intf in NetworkInterface.getNetworkInterfaces()) {
            for (addr in intf.inetAddresses) {
                if (!addr.isLoopbackAddress && !addr.isLinkLocalAddress) {
                    Log.i(TAG, "${intf.displayName} $addr")
                    text.text = "${text.text}\nhttp://$addr"
                }
            }
        }
    }

    override fun onStop() {
        super.onStop()
        server.closeAllConnections()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        return when (item.itemId) {
            R.id.action_settings -> true
            else -> super.onOptionsItemSelected(item)
        }
    }

    class App(val port: Int, val context: Activity) : NanoHTTPD(port) {
        override fun serve(session: IHTTPSession): Response {
            val media = MediaPlayer.create(context, R.raw.hello);
            context.volumeControlStream = AudioManager.STREAM_MUSIC
            media.start()
            return newFixedLengthResponse("accept")
        }

        init {
            start(SOCKET_READ_TIMEOUT, false)
        }
    }
}