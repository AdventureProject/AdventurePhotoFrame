package com.darkrockstudios.iot.adventurephotoframe

import android.content.Context
import android.util.Log
import org.apache.commons.lang3.exception.ExceptionUtils
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.*

/**
 * Created by adamw on 2/21/2017.
 */

object ErrorHandler
{
	private val FILE_NAME = "errors"
	private val SEPARATOR = System.lineSeparator() + "==========================" + System.lineSeparator()

	private fun constructErrorLog(thread: Thread, error: Throwable): String
	{
		var errorStr = SEPARATOR + Date().toString() + System.lineSeparator()
		errorStr += "Thread: " + thread.name + System.lineSeparator()
		errorStr += ExceptionUtils.getStackTrace(error) + System.lineSeparator()
		errorStr += SEPARATOR

		return errorStr
	}

	fun writeErrorToFile(thread: Thread, error: Throwable, context: Context)
	{
		try
		{
			val errorStr = constructErrorLog(thread, error)

			val outputStreamWriter = OutputStreamWriter(context.openFileOutput(FILE_NAME, Context.MODE_APPEND))
			outputStreamWriter.write(errorStr)
			outputStreamWriter.close()
		}
		catch (e: IOException)
		{
			Log.e("Exception", "File write failed: " + e.toString())
		}

	}

	fun clearErrorFile(context: Context): Boolean
	{
		return context.deleteFile(FILE_NAME)
	}

	fun readErrorFile(context: Context): String
	{
		try
		{
			val bufferSize = 1024
			val buffer = CharArray(bufferSize)
			val out = StringBuilder()
			val `in` = InputStreamReader(context.openFileInput(FILE_NAME))
			while (true)
			{
				val rsz = `in`.read(buffer, 0, buffer.size)
				if (rsz < 0)
				{
					break
				}
				out.append(buffer, 0, rsz)
			}
			return out.toString()
		}
		catch (e: IOException)
		{
			e.printStackTrace()
		}

		return ""
	}
}
