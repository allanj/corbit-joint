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

package corbit.tagdep.io;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import corbit.commons.util.Generator;
import corbit.tagdep.SRParserEvaluator;
import corbit.tagdep.dict.TagDictionary;
import corbit.tagdep.word.DepTreeSentence;
import corbit.tagdep.word.DepWord;

public abstract class ParseReader extends Generator<DepTreeSentence>
{
	public static void evalPos(TagDictionary dict, String sFile, String sGoldFile) throws IOException
	{
		SRParserEvaluator eval = new SRParserEvaluator(dict, false, true);
		PlainReader pr = new PlainReader(sFile);
		MaltReader cr = new MaltReader(sGoldFile);
		try
		{
			while (pr.hasNext() && cr.hasNext())
				eval.evalSentence(pr.next(), cr.next());
			eval.evalTotal();
		}
		finally
		{
			pr.shutdown();
			cr.shutdown();
		}
	}

	/***
	 * CTB reader and dictionary are already disabled
	public static void ctbToPlain(String sFile, String sOutFile) throws IOException
	{
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(sOutFile), "UTF-8"));
		CTBReader cr = new CTBReader(sFile);
		try
		{
			while (cr.hasNext())
			{
				DepTreeSentence s = cr.next();
				for (int i = 0; i < s.size(); ++i)
					pw.print(s.get(i).form + "/" + s.get(i).pos + " ");
				pw.println();
			}
		}
		finally
		{
			cr.shutdown();
		}
		pw.close();
	}
	**/
	public static void maltToDep(String sFile, String sOutFile) throws IOException
	{
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(sOutFile), "UTF-8"));
		MaltReader mr = new MaltReader(sFile);
		try
		{
			while (mr.hasNext())
			{
				DepTreeSentence s = mr.next();
				for (int i = 0; i < s.size(); ++i)
				{
					DepWord dw = s.get(i);
					pw.print(String.format("{%d}:({%d})_({%s})_({%s}) ", dw.index, dw.head, dw.form, dw.pos));
				}
				pw.println();
			}
		}
		finally
		{
			mr.shutdown();
		}
		pw.close();
	}

	public static void ecrf2Malt(String sFile, String sOutFile) throws IOException{
		PrintWriter pw = new PrintWriter(new OutputStreamWriter(new FileOutputStream(sOutFile), "UTF-8"));
		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(sFile),"UTF-8"));
		
		String line = null;
		
		while((line = br.readLine())!=null){
			String[] vals = line.split("\\t");
			if(vals.length<2){
				pw.write("\n");
				continue;
			}
			pw.write(vals[1]+"\t"+vals[3]+"\t"+vals[4]+"\t"+vals[2]+"\n");
		}
		br.close();
		pw.close();
	}
	
//	public static void main(String[] args) throws IOException{
//		ecrf2Malt("data/ecrf/ecrf.train.MISC.txt", "data/malt/malt.train.txt");
//		ecrf2Malt("data/ecrf/ecrf.devel.MISC.txt", "data/malt/malt.devel.txt");
//		ecrf2Malt("data/ecrf/ecrf.test.MISC.txt", "data/malt/malt.test.txt");
//	}
	
}
