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

package corbit.commons.io;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import corbit.commons.dict.TagDictionary;
import corbit.commons.util.Statics;
import corbit.commons.word.ArcLabel;
import corbit.commons.word.IndexWord;
import corbit.commons.word.ParsedSentence;
import corbit.commons.word.SentenceBuilder;

public class MaltReader extends ParseReader
{
	public MaltReader(String sFile, TagDictionary dict)
	{
		super(sFile, dict.generateTagSet(), new TreeSet<String>(Arrays.asList(dict.getArcLabels())));
	}

	@Override protected void iterate() throws InterruptedException
	{
		int iLine = 0;
		FileEnum fe = new FileEnum(m_sFile);

		try
		{
			while (fe.hasNext())
			{
				SentenceBuilder sb = new SentenceBuilder(m_posSet, m_labelSet);
				int index = 0;

				while (true)
				{
					String l = Statics.trimSpecial(fe.next());
					++iLine;
					if (l.length() == 0)
						break;

					Pattern re = Pattern.compile("(.*?)\t(.*?)\t(.*?)\t(.*?)");
					Matcher mc = re.matcher(l);
					
					if (!mc.matches() || mc.groupCount() < 4)
					{
						System.err.println(String.format("Error found at line %d. Skipping.", iLine));
						sb = null;
						break;
					}
					String sForm = mc.group(1); //no need to normalize for English //Normalizer.normalize(mc.group(1), Normalizer.Form.NFKC);
					String sPos = mc.group(2);
					int iHead = Integer.parseInt(mc.group(3)) - 1;
					ArcLabel label = ArcLabel.getLabel(mc.group(4));
					sb.addWord(index, sForm, sPos, iHead, label);
					index++;
				}

				if (sb != null)
					yieldReturn(sb.compile());
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			fe.shutdown();
		}
	}

	public static void maltToDep(String sFile, String sOutFile, TagDictionary dict) throws IOException
	{
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(sOutFile), "UTF-8"));
		MaltReader mr = new MaltReader(sFile, dict);
		try
		{
			while (mr.hasNext())
			{
				ParsedSentence s = mr.next();
				for (int i = 0; i < s.size(); ++i)
				{
					IndexWord dw = s.get(i);
					pw.print(String.format("%d:(%d)_(%s)_(%s)_(%s) ",
							dw.index, dw.head, dw.form, dw.tag, dw.arclabel));
				}
				pw.println();
			}
		} catch (Exception e)
		{
			e.printStackTrace();
		} finally
		{
			mr.shutdown();
		}
		pw.close();
	}
}
