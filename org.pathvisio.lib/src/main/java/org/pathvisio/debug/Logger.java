/*******************************************************************************
 * PathVisio, a tool for data visualization and analysis using biological pathways
 * Copyright 2006-2019 BiGCaT Bioinformatics
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License.  You may obtain a copy
 * of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  See the
 * License for the specific language governing permissions and limitations under
 * the License.
 ******************************************************************************/
package org.pathvisio.debug;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

/**
 * Logs output to a stream, with the option to filter for types of messages
 * 
 * log levels: 1: Trace 2: debug 3: info 4: warn 5: error 6: fatal
 * 
 * @author unknown
 */
public class Logger {
	private boolean debugEnabled = true;
	private boolean traceEnabled = false;
	private boolean infoEnabled = true;
	private boolean warnEnabled = true;
	private boolean errorEnabled = true;
	private boolean fatalEnabled = true;

	private PrintStream s = System.err;

	StopWatch logTimer;

	/**
	 * Constructor for Logger 
	 */
	public Logger() {
		logTimer = new StopWatch();
		logTimer.start();
	}
	
	/**
	 * Returns print stream.
	 * 
	 * @return s the stream.
	 */
	public PrintStream getStream() {
		return s;
	}

	/**
	 * Sets stream as given print stream.
	 * 
	 * @param aStream
	 */
	public void setStream(PrintStream aStream) {
		s = aStream;
	}

	/**
	 * if dest is "STDERR" or "STDOUT", the standard error / output are used.
	 * Otherwise, dest is interpreted as a filename
	 * 
	 * @param dest 
	 */
	public void setDest(String dest) {
		if (dest != null) {
			if (dest.equals("STDERR")) {
				s = System.err;
			} else if (dest.equals("STDOUT")) {
				s = System.out;
			} else {
				try {
					s = new PrintStream(new File(dest));
				} catch (FileNotFoundException e) {
					s = System.err;
					error("Could not open log file " + dest + " for writing", e);
				}
			}
		}
	}

	/**
	 * get/set log level to a certain level. The higher the level, the move output.
	 * Messages above this level are discarded.
	 * 
	 * @param debug
	 * @param trace
	 * @param info
	 * @param warn
	 * @param error
	 * @param fatal
	 */
	public void setLogLevel(boolean debug, boolean trace, boolean info, boolean warn, boolean error, boolean fatal) {
		debugEnabled = debug;
		traceEnabled = trace;
		infoEnabled = info;
		warnEnabled = warn;
		errorEnabled = error;
		fatalEnabled = fatal;
	}

	private static final String FORMAT_STRING = "[%10.3f] ";

	/**
	 * @param msg
	 */
	public void trace(String msg) {
		System.out.println(msg);
		if (traceEnabled) {
			s.printf(FORMAT_STRING, logTimer.look() / 1000.0f);
			s.println("Trace: " + msg);
		}
	}

	/**
	 * @param msg
	 */
	public void debug(String msg) {
		if (debugEnabled) {
			s.printf(FORMAT_STRING, logTimer.look() / 1000.0f);
			s.println("Debug: " + msg);
		}
	}

	/**
	 * @param msg
	 */
	public void info(String msg) {
		if (infoEnabled) {
			s.printf(FORMAT_STRING, logTimer.look() / 1000.0f);
			s.println("Info:  " + msg);
		}
	}

	/**
	 * @param msg
	 */
	public void warn(String msg) {
		if (warnEnabled)
			s.println("Warn:  " + msg);
	}

	/**
	 * @param msg
	 * @param e
	 */
	public void warn(String msg, Throwable e) {
		if (warnEnabled) {
			s.println("Warn:  " + msg + "\n\t" + e.getMessage());
		}
		if (debugEnabled) {
			e.printStackTrace(s);
		}
	}

	/**
	 * @param msg
	 */
	public void error(String msg) {
		if (errorEnabled)
			s.println("Error: " + msg);
	}

	/**
	 * @param msg
	 * @param e
	 */
	public void error(String msg, Throwable e) {
		if (errorEnabled) {
			error(msg + "\n\t" + e.toString() + (e != null ? ": " + e.getMessage() : ""));
		}
		if (debugEnabled) {
			e.printStackTrace(s);
		}
	}

	/**
	 * @param msg
	 */
	public void fatal(String msg) {
		if (fatalEnabled)
			s.println("Fatal: " + msg);
	}

	/**
	 * Global application logger
	 */
	public static Logger log = new Logger();
}
