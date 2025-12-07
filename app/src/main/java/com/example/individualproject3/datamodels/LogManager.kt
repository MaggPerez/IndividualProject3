package com.example.individualproject3.datamodels

import android.content.Context
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
class LogManager(private val context: Context) {
    private val logFileName = "game_progress_log.txt"

    /**
     * gets the log file from the internal storage
     */
    private fun getLogFile(): File {
        return File(context.filesDir, logFileName)
    }


    /**
     * writes a log entry
     *
     */
    fun logGameSession(
        username: String,
        level: Int,
        gameNumber: Int,
        score: Int,
        success: Boolean,
        attempts: Int
    ) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            .format(Date())

        val logEntry = buildString {
            append("[$timestamp] ")
            append("User: $username | ")
            append("Level: $level | ")
            append("Game: $gameNumber | ")
            append("Score: $score | ")
            append("Status: ${if (success) "SUCCESS" else "FAILED"} | ")
            append("Attempts: $attempts")
            appendLine()
        }

        writeToFile(logEntry)
    }


    /**
     * function that logs user registration
     */
    fun logUserRegistration(username: String, userType: String) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            .format(Date())

        val logEntry = "[$timestamp] NEW USER REGISTERED - Username: $username, Type: $userType\n"
        writeToFile(logEntry)
    }


    /**
     * logs user login
     */
    fun logUserLogin(username: String){
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            .format(Date())

        val logEntry = "[$timestamp] USER LOGIN - Username: $username\n"
        writeToFile(logEntry)
    }


    /**
     * logs a general message with timestamp
     */
    fun logMessage(message: String) {
        val timestamp = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            .format(Date())

        val logEntry = "[$timestamp] $message\n"
        writeToFile(logEntry)
    }


    /**
     * function that writes to log file using "append"
     */
    private fun writeToFile(logEntry: String) {
        try {
            FileWriter(getLogFile(), true).use { writer ->
                writer.append(logEntry)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    /**
     * function that reads all logs
     */
    fun readLogs(): String{
        return try {
            val file = getLogFile()
            if (file.exists()) {
                file.readText()
            } else {
                "No logs available"
            }
        } catch (e: Exception) {
            "Error reading logs: ${e.message}"
        }
    }


    /**
     * function that gets logs for a specific user
     */
    fun getLogsForUser(username: String): List<String>{
        return try {
            val file = getLogFile()
            if (file.exists()) {
                file.readLines().filter { it.contains("User: $username") }
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }



    /**
     * function that clears all logs
     */
    fun clearLogs(){
        try {
            getLogFile().writeText("")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}