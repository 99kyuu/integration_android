package com.virtusize.libsource

import android.content.SharedPreferences
import com.virtusize.libsource.Constants.BID_KEY
import java.util.*
import kotlin.random.Random

class BrowserIdentifier(private val sharedPrefs: SharedPreferences) {

    fun getBid(): String {
        return if(sharedPrefs.contains(BID_KEY)) {
                    sharedPrefs.getString(BID_KEY, null) ?: generateAndStoreBid()
                }
                else generateAndStoreBid()
    }

    private fun generateAndStoreBid(): String {
        val bid = generateBid()
        val editor = sharedPrefs.edit()
        editor.putString(BID_KEY, bid)
        editor.apply()
        return bid
    }

    private fun generateBid(): String {
        // Format: cJhf5nGjDA0fgUXLAIz5Ls5.pfa1lp
        val randomPart = StringBuilder("")
        val chars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz"
        for (i in 0..21) {
            val randomNum = Random.nextInt(until = chars.length)
            randomPart.append(chars[randomNum])
        }
        val timeUntil1970InMs = Calendar.getInstance().timeInMillis

        randomPart.append('.')
        randomPart.append(timeUntil1970InMs.toString(36).toLowerCase())

        return randomPart.toString()
    }
}