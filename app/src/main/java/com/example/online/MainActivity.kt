package com.example.online

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.features.websocket.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.channels.BroadcastChannel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.w3c.dom.Text

class MainActivity : AppCompatActivity() {
    val client = HttpClient(CIO){
        install(WebSockets)
    }
    private val channel = BroadcastChannel<String>(1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val textView = findViewById<TextView>(R.id.textView)
                MainScope().launch(Dispatchers.IO) {
            client.webSocket(host = "192.168.111.50" , port = 8080){
                launch { channel.consumeEach { message ->
                    outgoing.send(Frame.Text(message))
                }
                }
                for (frame in incoming){
                    if(frame is Frame.Text){
                        withContext(Dispatchers.Main){
                            textView.text = frame.readText()
                        }
                    }
                }
            }
        }
    }
}