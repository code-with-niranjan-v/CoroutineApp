package com.example.cororutineapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.coroutines.channels.produce

class MainActivity : AppCompatActivity() {
    private val  tag = "Testing1"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        example1()
        flowExample()
        channelExample()

    }

    private fun example1(){
        GlobalScope.launch(Dispatchers.Main) {
            Log.e(tag,"This is executed before the first delay")
            stallForTime()
            Log.e(tag,"This is executed after the first delay")
        }
        GlobalScope.launch(Dispatchers.Main) {
            Log.e(tag,"This is executed before the second delay")
            stallForTime()
            Log.e(tag,"This is executed after the second delay")
        }
        Log.e(tag,"This is executed immediately")
    }

    private suspend fun stallForTime() {
        withContext(Dispatchers.Default) {
            delay(2000L)
        }
    }

    private fun flowExample(){
        GlobalScope.launch(Dispatchers.Main){
            generateNumber().collect{
                delay(1000)
                Log.e(tag,it.toString())
            }
        }

    }

    private fun generateNumber(): Flow<Int> = flow {
        for (i in 0..5){
            delay(1000)
            emit(i)
        }
    }
    // Cold FLow -> it only sends elements in the stream if there are any consumer paying attention to it.
    private fun channelExample(){
        GlobalScope.launch(Dispatchers.Main){
            generateNumberUsingChannelsInOffer().consumeEach{
                delay(1000)
                Log.e(tag,it.toString())
            }
        }
    }
    //Cold Flow
    private fun CoroutineScope.generateNumberUsingChannels() = produce{
        for (i in 0..5){
            delay(1000)
            send(i)
        }
    }

    //Hot Flow -> it always send elements in the stream it does not matter whether any consumer paying attention to it.
    private fun CoroutineScope.generateNumberUsingChannelsInOffer() = produce{
        for (i in 0..5){
            delay(1000)
            this.trySend(i).isSuccess
        }
    }

}

