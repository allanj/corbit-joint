/*
 * Corbit, a text analyzer
 * 
 * Copyright (c) 2010-2012, Jun Hatori
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the names of the authors nor the names of its contributors
 *       may be used to endorse or promote products derived from this
 *       software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER
 * OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package corbit.commons.util;

import java.text.DateFormat;
import java.util.Date;

import corbit.commons.io.Console;

public class Stopwatch
{
	String m_sMessage;
	long lStart;
	long lElapsed;
	boolean bRunning = false;

	public Stopwatch(String s)
	{
		m_sMessage = s;
		Console.writeLine(String.format("[%s | 0.00:00:00.000] start: %s", DateFormat.getInstance().format(new Date()), s));
		reset();
		start();
	}

	public void reset()
	{
		lElapsed = 0;
		lStart = System.currentTimeMillis();
	}

	public void start()
	{
		if (bRunning)
			return;
		bRunning = true;
		lStart = System.currentTimeMillis();
	}

	public void pause()
	{
		if (!bRunning)
			return;
		bRunning = false;
		lElapsed += System.currentTimeMillis() - lStart;
	}

	public void lap()
	{
		pause();
		DateFormat df1 = DateFormat.getInstance();

		final long n[] = { 1000, 60, 60, 24 };
		long t[] = new long[n.length + 1];

		long l = lElapsed;
		for (int i = 0; i < n.length; ++i) {
			t[i] = l % n[i];
			l = l / n[i];
		}
		t[n.length] = l;

		Console.writeLine(String.format("[%s | %s] end: %s",
				df1.format(new Date()),
				String.format("%1d.%02d:%02d:%02d.%03d", t[4], t[3], t[2], t[1], t[0]),
				m_sMessage));
	}
}
