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

package corbit.segdep;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import corbit.commons.util.Pair;
import corbit.commons.util.Statics;

public class DPParserChart
{
	int m_iTotalState = 0;
	int m_iMergedState = 0;
	int m_iEvaluatedState = 0;
	double m_dMargin = 0.0d;
	boolean m_dp = true;
	LinkedHashMap<SRParserState,Pair<SRParserState,double[]>> m_entries;

	SRParserStateGenerator m_generator;

	public int numTotalState()
	{
		return m_iTotalState;
	}

	public int numMergedState()
	{
		return m_iMergedState;
	}

	public int numEvaluatedNonDPState()
	{
		return m_iEvaluatedState;
	}

	public boolean isDP()
	{
		return m_dp;
	}

	public void setDP(boolean m_dp)
	{
		this.m_dp = m_dp;
	}

	public int size()
	{
		return m_entries.size();
	}

	public DPParserChart(boolean dp, SRParserStateGenerator generator)
	{
		this(dp, generator, 0.0);
	}

	public DPParserChart(boolean dp, SRParserStateGenerator generator, double margin)
	{
		m_dp = dp;
		m_entries = new LinkedHashMap<SRParserState,Pair<SRParserState,double[]>>();
		m_generator = generator;
		m_dMargin = margin;
	}

	public Set<SRParserState> keySet()
	{
		return m_entries.keySet();
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		for (Pair<SRParserState,double[]> p: entries())
			sb.append((float)p.second[0] + " " + (float)p.second[1] + " " + p.first.toString() + "\n");
		return sb.toString();
	}

	public void remove(SRParserState s)
	{
		m_entries.remove(s);
	}

	public List<Pair<SRParserState,double[]>> entries()
	{
		List<Pair<SRParserState,double[]>> l = new ArrayList<Pair<SRParserState,double[]>>();
		for (Entry<SRParserState,Pair<SRParserState,double[]>> p: m_entries.entrySet())
			l.add(new Pair<SRParserState,double[]>(p.getValue().first, p.getValue().second));
		return l;
	}

	public void clear()
	{
		m_entries.clear();
		m_iEvaluatedState = 0;
	}

	public SRParserState getKey(SRParserState s)
	{
		return m_entries.get(s).first;
	}

	public synchronized SRParserState updateEntry(SRParserState s)
	{
		assert (s != null);
		++m_iTotalState;
		if (m_dp && m_entries.containsKey(s))
		{
			SRParserState _s = m_entries.get(s).first;
			SRParserState sMerged = m_generator.merge(_s, s);
			m_entries.remove(s);
			m_entries.put(sMerged, new Pair<SRParserState,double[]>(sMerged, new double[] { sMerged.gold ? sMerged.scprf - m_dMargin : sMerged.scprf, sMerged.scins }));
			++m_iMergedState;
			if (_s.preds.size() == 1 && sMerged.preds.size() == 2)
				++m_iMergedState;
			m_iEvaluatedState += sMerged.nstates;
			return sMerged;
		}
		else
		{
			m_entries.put(s, new Pair<SRParserState,double[]>(s, new double[] { s.gold ? s.scprf - m_dMargin : s.scprf, s.scins }));
			++m_iEvaluatedState;
			return s;
		}
	}

	public static Pair<SRParserState,SRParserState> horizontalPrune(DPParserChart[] charts, int curidx, int goldidx, int iBeam)
	{
		List<Entry<SRParserState,Pair<SRParserState,double[]>>> l = new ArrayList<Entry<SRParserState,Pair<SRParserState,double[]>>>();

		for (int i = 0; i < charts.length; ++i)
		{
			for (Entry<SRParserState,Pair<SRParserState,double[]>> p: charts[i].m_entries.entrySet())
				l.add(p);
			charts[i].m_entries.clear();
		}
		sort(l);
		double[] dLastScore = new double[] { Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY };
		for (int i = 0; i < l.size(); ++i)
		{
			SRParserState state = l.get(i).getKey();
			Pair<SRParserState,double[]> value = l.get(i).getValue();
			if (i >= iBeam && !Statics.arrayEquals(value.second, dLastScore))
				break;
			charts[state.curstep].m_entries.put(state, value);
		}
		SRParserState _sref = null;
		return new Pair<SRParserState,SRParserState>(l.get(0).getKey(), _sref);
	}

	public void prune(int iBeam)
	{
		List<Entry<SRParserState,Pair<SRParserState,double[]>>> l = new ArrayList<Entry<SRParserState,Pair<SRParserState,double[]>>>();
		for (Entry<SRParserState,Pair<SRParserState,double[]>> p: m_entries.entrySet())
			l.add(p);
		m_entries.clear();
		sort(l);
		double[] dLastScore = new double[] { Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY };
		for (int i = 0; i < l.size(); ++i)
		{
			SRParserState state = l.get(i).getKey();
			Pair<SRParserState,double[]> value = l.get(i).getValue();
			if (i >= iBeam && !Statics.arrayEquals(value.second, dLastScore))
				break;
			m_entries.put(state, value);
		}
	}

	static void sort(List<Entry<SRParserState,Pair<SRParserState,double[]>>> l)
	{
		Collections.sort(l, new Comparator<Entry<SRParserState,Pair<SRParserState,double[]>>>()
		{
			public int compare(Entry<SRParserState,Pair<SRParserState,double[]>> p1,
					Entry<SRParserState,Pair<SRParserState,double[]>> p2)
			{
				double[] d1 = p1.getValue().second;
				double[] d2 = p2.getValue().second;
				return d1[0] < d2[0] ? 1 : (d1[0] == d2[0] ? (d1[1] < d2[1] ? 1 : d1[1] == d2[1] ? 0 : -1) : -1);
			}
		});
	}

	public SRParserState getBestEntry()
	{
		SRParserState sBest = null;
		double[] dBest = new double[] { Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY };
		for (Pair<SRParserState,double[]> p: entries())
		{
			if (p.second[0] > dBest[0] || p.second[0] == dBest[0] && p.second[1] > dBest[1])
			{
				sBest = p.first;
				dBest = p.second;
			}
		}

		return sBest;
	}

	public boolean containsGoldState()
	{
		boolean bFound = false;
		for (SRParserState s: keySet())
		{
			if (s.gold)
			{
				bFound = true;
				break;
			}
		}
		return bFound;
	}

}
