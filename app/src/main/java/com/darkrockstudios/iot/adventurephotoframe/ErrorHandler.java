package com.darkrockstudios.iot.adventurephotoframe;

import android.content.Context;
import android.util.Log;

import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.util.Date;

/**
 * Created by adamw on 2/21/2017.
 */

public class ErrorHandler
{
	private static final String FILE_NAME = "errors";
	private static final String SEPARATOR = System.lineSeparator() + "==========================" + System.lineSeparator();

	private static String constructErrorLog( Thread thread, Throwable error )
	{
		String errorStr = SEPARATOR + new Date().toString() + System.lineSeparator();
		errorStr += "Thread: " + thread.getName() + System.lineSeparator();
		errorStr += ExceptionUtils.getStackTrace( error ) + System.lineSeparator();
		errorStr += SEPARATOR;

		return errorStr;
	}

	public static void writeErrorToFile( Thread thread, Throwable error, Context context )
	{
		try
		{
			String errorStr = constructErrorLog( thread, error );

			OutputStreamWriter outputStreamWriter =
					new OutputStreamWriter( context.openFileOutput( FILE_NAME, Context.MODE_APPEND ) );
			outputStreamWriter.write( errorStr );
			outputStreamWriter.close();
		}
		catch( IOException e )
		{
			Log.e( "Exception", "File write failed: " + e.toString() );
		}
	}

	public static boolean clearErrorFile( Context context )
	{
		return context.deleteFile( FILE_NAME );
	}

	public static String readErrorFile( Context context )
	{
		try
		{
			final int bufferSize = 1024;
			final char[] buffer = new char[ bufferSize ];
			final StringBuilder out = new StringBuilder();
			Reader in = new InputStreamReader( context.openFileInput( FILE_NAME ) );
			while( true )
			{
				int rsz = in.read( buffer, 0, buffer.length );
				if( rsz < 0 )
				{
					break;
				}
				out.append( buffer, 0, rsz );
			}
			return out.toString();
		}
		catch( IOException e )
		{
			e.printStackTrace();
		}

		return "";
	}
}
