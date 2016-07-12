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

package corbit.tagdep.handler;

import java.util.List;
import java.util.TreeSet;

import corbit.commons.Vocab;
import corbit.commons.ml.IntFeatVector;
import corbit.commons.transition.PDAction;
import corbit.tagdep.SRParserParameters;
import corbit.tagdep.SRParserState;
import corbit.tagdep.word.DepTree;

/** 
 * Dependency parsing features from Huang and Sagae (2010)
 */
public class SRParserCtbHandlerHS10 extends SRParserHandler
{
	class AtomsHS10 extends AtomicFeatures
	{
		static final int F_curidx = 0;
		static final int F_span_bgn = 1;
		static final int F_span_end = 2;
		static final int F_idx_st0 = 3;
		static final int F_head_st0 = 4;
		static final int F_idx_st1 = 5;
		static final int F_f_st0 = 6;
		static final int F_f_st1 = 7;
		static final int F_f_qp1 = 8;
		static final int F_f_qf1 = 9;
		static final int F_p_st0 = 10;
		static final int F_p_st1 = 11;
		static final int F_p_st2 = 12;
		static final int F_p_qp2 = 13;
		static final int F_p_qp1 = 14;
		static final int F_p_qf1 = 15;
		static final int F_p_qf2 = 16;
		static final int F_p_st0rc = 17;
		static final int F_p_st0lc = 18;
		static final int F_p_st1rc = 19;
		static final int F_p_st1lc = 20;
		static final int F_prevEntity = 21;
		static final int F_adjoin = 22;
		static final int F_npos = 23;

		static final int NUM_FEATURE = 24;

		AtomsHS10(
				int curidx,
				int span_bgn,
				int span_end,
				int idx_st0,
				int head_st0,
				int idx_st1,
				String f_st0,
				String f_st1,
				String f_qp1,
				String f_qf1,
				String p_st0,
				String p_st1,
				String p_st2,
				String p_qp2,
				String p_qp1,
				String p_qf1,
				String p_qf2,
				String p_st0rc,
				String p_st0lc,
				String p_st1rc,
				String p_st1lc,
				String p_prevEntity,
				boolean adjoin,
				String npos,
				TreeSet<String> fvdelay)
		{
			super(NUM_FEATURE, fvdelay);
			features = new String[NUM_FEATURE];
			features[F_curidx] = Integer.toString(curidx);
			features[F_span_bgn] = Integer.toString(span_bgn);
			features[F_span_end] = Integer.toString(span_end);
			features[F_idx_st0] = Integer.toString(idx_st0);
			features[F_head_st0] = Integer.toString(head_st0);
			features[F_idx_st1] = Integer.toString(idx_st1);
			features[F_f_st0] = f_st0;
			features[F_f_st1] = f_st1;
			features[F_f_qp1] = f_qp1;
			features[F_f_qf1] = f_qf1;
			features[F_p_st0] = p_st0;
			features[F_p_st1] = p_st1;
			features[F_p_st2] = p_st2;
			features[F_p_qp2] = p_qp2;
			features[F_p_qp1] = p_qp1;
			features[F_p_qf1] = p_qf1;
			features[F_p_qf2] = p_qf2;
			features[F_p_st0rc] = p_st0rc;
			features[F_p_st0lc] = p_st0lc;
			features[F_p_st1rc] = p_st1rc;
			features[F_p_st1lc] = p_st1lc;
			features[F_prevEntity] = p_prevEntity;
			features[F_adjoin] = Boolean.toString(adjoin);
			features[F_npos] = npos;
			setHash();
		}
	}

	public SRParserCtbHandlerHS10(Vocab v, SRParserParameters params)
	{
		super(v, params);
	}

	@Override
	public AtomicFeatures getAtomicFeatures(SRParserState s0)
	{
		SRParserState s1 = s0.preds.size() > 0 ? s0.pred0 : null;

		DepTree wst0 = s0.pstck[0];
		DepTree wst1 = s0.pstck[1];
		DepTree wst2 = s0.pstck[2];
		
		DepTree wst0rc = wst0 != null ? getRightmostChild(wst0) : null;
		DepTree wst0lc = wst0 != null ? getLeftmostChild(wst0) : null;
		DepTree wst1rc = wst1 != null ? getRightmostChild(wst1) : null;
		DepTree wst1lc = wst1 != null ? getLeftmostChild(wst1) : null;

		int idx = s0.curidx;

		String sfst0 = wst0.form;
		String sfst1 = wst1 != null ? wst1.form : OOR;
		String sfqp1 = idx > 0 ? s0.sent.get(idx - 1).form : OOR;
		String sfqf1 = idx < s0.sent.size() ? s0.sent.get(idx).form : OOR;

//		String spst0 = wst0.pos;
//		String spst1 = wst1 != null ? wst1.pos : OOR;
//		String spst2 = wst2 != null ? wst2.pos : OOR;
//		String spqp1 = idx > 0 ? s0.pos[idx - 1] : OOR;
//		String spqp2 = idx > 1 ? (s0.idbgn <= idx - 2) ? s0.pos[idx - 2] : s1.pos[idx - 2] : OOR;
//		String spqf1 = idx < s0.sent.size() ? s0.sent.get(idx).pos : OOR;
//		String spqf2 = idx < s0.sent.size() - 1 ? s0.sent.get(idx + 1).pos : OOR;
//		String spst0rc = wst0rc != null ? s0.pos[wst0rc.index] : OOR;
//		String spst0lc = wst0lc != null ? s0.pos[wst0lc.index] : OOR;
//		String spst1rc = wst1rc != null ? s1.pos[wst1rc.index] : OOR;
//		String spst1lc = wst1lc != null ? s1.pos[wst1lc.index] : OOR;
		
		//cause the entity now is the real pos tag
		String spst0 = wst0.entity;
		String spst1 = wst1 != null ? wst1.entity : OOR;
		String spst2 = wst2 != null ? wst2.entity : OOR;
		String spqp1 = idx > 0 ? s0.sent.get(idx-1).entity : OOR;
		String spqp2 = idx > 1 ? (s0.idbgn <= idx - 2) ? s0.sent.get(idx-2).entity : s1.sent.get(idx-2).entity : OOR;
		String spqf1 = idx < s0.sent.size() ? s0.sent.get(idx).entity : OOR;
		String spqf2 = idx < s0.sent.size() - 1 ? s0.sent.get(idx + 1).entity : OOR;
		String spst0rc = wst0rc != null ? s0.sent.get(wst0rc.index).entity : OOR;
		String spst0lc = wst0lc != null ? s0.sent.get(wst0lc.index).entity : OOR;
		String spst1rc = wst1rc != null ? s1.sent.get(wst1rc.index).entity : OOR;
		String spst1lc = wst1lc != null ? s1.sent.get(wst1lc.index).entity : OOR;

		//String sPunct = (wst0 != null && wst1 != null) ? getPunctInBetween(s0.sent, wst0.index, wst1.index) : OOR;
		boolean bAdjoin = (wst0 != null && wst1 != null && Math.abs(wst1.index - wst0.index) == 1);

		String prevEntity = idx > 0 ? s0.pos[idx-1] : "O";
		
		// debugging assertions
		
		assert (spst0 != null);
		assert (spst1 != null);
		assert (spst2 != null);
		assert (spqp1 != null);
		assert (spqp2 != null);
		assert (spst0rc != null);
		assert (spst0lc != null);
		assert (spst1rc != null);
		assert (spst1lc != null);

		// ad-hoc modification

		return new AtomsHS10(
				s0.curidx,
				s0.idbgn,
				s0.idend,
				wst0.index,
				wst0.index >= 0 ? s0.heads[wst0.index] : -2,
				wst1 != null ? wst1.index : -2,
				sfst0,
				sfst1,
				sfqp1,
				sfqf1,
				spst0,
				spst1,
				spst2,
				spqp2,
				spqp1,
				spqf1,
				spqf2,
				spst0rc,
				spst0lc,
				spst1rc,
				spst1lc,
				prevEntity,
				bAdjoin,
				OOR,
				s0.fvdelay != null ? new TreeSet<String>(s0.fvdelay) : null);
	}

	@Override
	public IntFeatVector getFeatures(SRParserState s0, PDAction act, List<String> vd, boolean bAdd)
	{
		IntFeatVector v = new IntFeatVector();

		String sfst0 = s0.atoms.get(AtomsHS10.F_f_st0);
		String sfst1 = s0.atoms.get(AtomsHS10.F_f_st1);
		String sfqp1 = s0.atoms.get(AtomsHS10.F_f_qp1);
		String sfqf1 = s0.atoms.get(AtomsHS10.F_f_qf1);
		String spst0 = s0.atoms.get(AtomsHS10.F_p_st0);
		String spst1 = s0.atoms.get(AtomsHS10.F_p_st1);
		String spst2 = s0.atoms.get(AtomsHS10.F_p_st2);
		String spqp1 = s0.atoms.get(AtomsHS10.F_p_qp1);
		String spqp2 = s0.atoms.get(AtomsHS10.F_p_qp2);
		String spqf1 = s0.atoms.get(AtomsHS10.F_p_qf1);
		String spqf2 = s0.atoms.get(AtomsHS10.F_p_qf2);
		String spst0rc = s0.atoms.get(AtomsHS10.F_p_st0rc);
		String spst0lc = s0.atoms.get(AtomsHS10.F_p_st0lc);
		String spst1rc = s0.atoms.get(AtomsHS10.F_p_st1rc);
		String spst1lc = s0.atoms.get(AtomsHS10.F_p_st1lc);
		String prevEntity = s0.atoms.get(AtomsHS10.F_prevEntity);
		String sAdjoin = s0.atoms.get(AtomsHS10.F_adjoin);
		
//		System.err.println("just print the features..  current state:"+s0.toString()+" action:"+act.toString());
//		System.err.println("sfst0:"+sfst0);
//		System.err.println("sfst1:"+sfst1);
//		System.err.println("sfqp1:"+sfqp1);
//		System.err.println("sfqf1:"+sfqf1);
//		System.err.println("spst0:"+spst0);
//		System.err.println("spst1:"+spst1);
//		System.err.println("spst2:"+spst2);
//		System.err.println("spqp1:"+spqp1);
//		System.err.println("spqp2:"+spqp2);
//		System.err.println("spqf1:"+spqf1);
//		System.err.println("spqf2:"+spqf2);
//		System.err.println("spst0rc:" +spst0rc);
//		System.err.println("spst0lc:" +spst0lc);
//		System.err.println("spst1rc:" +spst1rc);
//		System.err.println("spst1lc:" +spst1lc);
//		System.err.println("sPunct:" + sPunct);
//		System.err.println("sAdjoin:"+sAdjoin);
		
		int curidx = s0.curidx;
		final int szSent = s0.sent.size();

		String sfqf2 = s0.curidx < s0.sent.size() - 1 ? s0.sent.get(s0.curidx + 1).form : OOR;
		String sAct = act.toString();

//		if (act.isShiftPosAction()) spqf1 = act.getPos();
//		else if (act.isPosAction()) throw new UnsupportedOperationException();
		
		/*
		 *  parsing features
		 */
		
		if (act == PDAction.REDUCE_LEFT || act == PDAction.REDUCE_RIGHT || act == PDAction.SHIFT || act.isShiftPosAction())
			SRParserCtbHandlerHS10.setParseFeaturesHS10(v, vd, m_vocab, bAdd, sAct, sfst0, sfst1, sfqf1, spst0, spst1, spst2, spqf1, spqf2, spst0rc, spst0lc, spst1rc, spst1lc, sAdjoin, curidx, szSent, m_params.m_bUseLookAhead);

		/*
		 *  evaluate delayed features
		 */
		
		if (vd != null && (act.isPosAction() || act.isShiftPosAction()))
		{
			//System.err.println("adding the delayed features");
			//evaluateDelayedFeatures(v, vd, curidx + 1, spqf1, bAdd);
			assert (curidx + 1 < szSent || vd.size() == 0);
		}
		
		/*
		 *  evaluate tagging features
		 */
		
		if (m_params.m_bUseTagFeature && act.isShiftPosAction())
		{
			//SRParserCtbHandlerZC08.setTagFeaturesZC08(v, m_vocab, m_dict, bAdd, sAct, sfqp1, sfqf1, sfqf2, spqp1, spqp2);
//			if (m_params.m_bUseSyntax)
//				SRParserCtbHandlerHS10.setTagSyntacticFeatures(v, m_vocab, bAdd, sAct, sfst0, sfqf1, spst0, spst1, spst0lc);
		}
		
		//use the spqp to construct the entity features
		if(act == PDAction.REDUCE_LEFT || act == PDAction.REDUCE_RIGHT  || act.isShiftPosAction()){
//			System.err.println(s0.toString());
//			System.err.println(act.toString());
			setEntityFeatures(v, m_vocab, bAdd, sAct, sfst0, spst0, spst0lc, spst0rc, sfqf1, spqf1, sfqp1, spqp1, sfqf2, spqf2, prevEntity);
		}
		
		
		return v;
	}

	// parsing features described in Huang and Sagae (2010)
	
	static String setParseFeaturesHS10(
			IntFeatVector v,
			List<String> vd,
			Vocab vocab,
			boolean bAdd,
			String sAct,
			String sfst0, String sfst1, String sfqf1, String spst0,
			String spst1, String spst2, String spqf1, String spqf2,
			String spst0rc, String spst0lc, String spst1rc, String spst1lc,
			String sAdjoin,
			final int curidx, final int szSent,
			boolean bUseLookAhead)
	{
		addFeature(v, "FP01-" + sfst0, sAct, 1.0, bAdd, vocab); //s0w
		addFeature(v, "FP02-" + spst0, sAct, 1.0, bAdd, vocab); //s0t
		addFeature(v, "FP03-" + sfst0 + SEP + spst0, sAct, 1.0, bAdd, vocab); //s0w+s0t

		addFeature(v, "FP04-" + sfst1, sAct, 1.0, bAdd, vocab); //s1w
		addFeature(v, "FP05-" + spst1, sAct, 1.0, bAdd, vocab); //s1t
		addFeature(v, "FP06-" + sfst1 + SEP + spst1, sAct, 1.0, bAdd, vocab); //s1w+s1t

		addFeature(v, "FP07-" + sfqf1, sAct, 1.0, bAdd, vocab); //q0w

		addFeature(v, "FP10-" + sfst0 + SEP + sfst1, sAct, 1.0, bAdd, vocab); //s0w+s1w
		addFeature(v, "FP11-" + spst0 + SEP + spst1, sAct, 1.0, bAdd, vocab); //s0t+s1t
		addFeature(v, "FP13-" + sfst0 + SEP + spst0 + SEP + spst1, sAct, 1.0, bAdd, vocab); //S0W+S0t+s1t
		addFeature(v, "FP14-" + sfst0 + SEP + spst0 + SEP + sfst1, sAct, 1.0, bAdd, vocab); //s0w+s0t+s1w
		addFeature(v, "FP15-" + sfst0 + SEP + sfst1 + SEP + spst1, sAct, 1.0, bAdd, vocab); //s0w+s1w+s1t
		addFeature(v, "FP16-" + spst0 + SEP + sfst1 + SEP + spst1, sAct, 1.0, bAdd, vocab); //s0t + s1w+s1t
		addFeature(v, "FP17-" + sfst0 + SEP + spst0 + SEP + sfst1 + SEP + spst1, sAct, 1.0, bAdd, vocab); //s0w+s0t+s1w+s1t

		if (bUseLookAhead)
		{
			if (vd != null) // use delayed evaluation
			{
				boolean bAddToDelay1 = false;
				boolean bAddToDelay2 = false;
				if (spqf1 == null && curidx < szSent)
				{
					spqf1 = getPosArgString(curidx);
					bAddToDelay1 = true;
				}
				if (spqf2 == null && curidx < szSent - 1)
				{
					spqf2 = getPosArgString(curidx + 1);
					bAddToDelay2 = true;
				}

				if (bAddToDelay1)
				{
					vd.add("FP08d-" + spqf1 + SEP + sAct); //  q0t
					vd.add("FP09d-" + sfqf1 + SEP + spqf1 + SEP + sAct); // q0w+q0t
					vd.add("FP12d-" + spst0 + SEP + spqf1 + SEP + sAct); // s0t+q0t
					vd.add("FP19d-" + spst0 + SEP + spst1 + SEP + spqf1 + SEP + sAct); //s0t+s1t+q0t
					vd.add("FP21d-" + sfst0 + SEP + spst1 + SEP + spqf1 + SEP + sAct); //s0w+s1t+q0t
				}
				else
				{
					addFeature(v, "FP08d-" + spqf1, sAct, 1.0, bAdd, vocab); // q0t
					addFeature(v, "FP09d-" + sfqf1 + SEP + spqf1, sAct, 1.0, bAdd, vocab);
					addFeature(v, "FP12d-" + spst0 + SEP + spqf1, sAct, 1.0, bAdd, vocab);
					addFeature(v, "FP19d-" + spst0 + SEP + spst1 + SEP + spqf1, sAct, 1.0, bAdd, vocab);
					addFeature(v, "FP21d-" + sfst0 + SEP + spst1 + SEP + spqf1, sAct, 1.0, bAdd, vocab);
				}
				if (bAddToDelay1 || bAddToDelay2)
				{
					vd.add("FP18d-" + spst0 + SEP + spqf1 + SEP + spqf2 + SEP + sAct); //s0t+q0t+q1t
					vd.add("FP20d-" + sfst0 + SEP + spqf1 + SEP + spqf2 + SEP + sAct); //s0w+q0t+q1t
				}
				else
				{
					addFeature(v, "FP18d-" + spst0 + SEP + spqf1 + SEP + spqf2, sAct, 1.0, bAdd, vocab);
					addFeature(v, "FP20d-" + sfst0 + SEP + spqf1 + SEP + spqf2, sAct, 1.0, bAdd, vocab);
				}
			}
			else
			{
				if (spqf1 != null)
				{
					addFeature(v, "FP08-" + spqf1, sAct, 1.0, bAdd, vocab); //q0t
					addFeature(v, "FP09-" + sfqf1 + SEP + spqf1, sAct, 1.0, bAdd, vocab);
					addFeature(v, "FP12-" + spst0 + SEP + spqf1, sAct, 1.0, bAdd, vocab);
					addFeature(v, "FP19-" + spst0 + SEP + spst1 + SEP + spqf1, sAct, 1.0, bAdd, vocab);
					addFeature(v, "FP21-" + sfst0 + SEP + spst1 + SEP + spqf1, sAct, 1.0, bAdd, vocab);

					if (spqf2 != null)
					{
						addFeature(v, "FP18-" + spst0 + SEP + spqf1 + SEP + spqf2, sAct, 1.0, bAdd, vocab);
						addFeature(v, "FP20-" + sfst0 + SEP + spqf1 + SEP + spqf2, sAct, 1.0, bAdd, vocab);
					}
				}
			}
		}

		addFeature(v, "FP22-" + spst0 + SEP + spst1 + SEP + spst1lc, sAct, 1.0, bAdd, vocab);  //s0t+s1t+s1.lc.t
		addFeature(v, "FP23-" + spst0 + SEP + spst1 + SEP + spst1rc, sAct, 1.0, bAdd, vocab); //s0t+s1t+s1.rc.t
		addFeature(v, "FP24-" + spst0 + SEP + spst0rc + SEP + spst1, sAct, 1.0, bAdd, vocab);  //s0t+s0.rc.t+s1t
		addFeature(v, "FP25-" + spst0 + SEP + spst0lc + SEP + spst1, sAct, 1.0, bAdd, vocab);  //s0t+s0.lc.t+s1t not presented in paper  it should be 
//		addFeature(v, "FP25-" + spst0 + SEP + spst1lc + SEP + spst1, sAct, 1.0, bAdd, vocab); // compatible with run0818--run0905
		addFeature(v, "FP26-" + sfst0 + SEP + spst1 + SEP + spst1rc, sAct, 1.0, bAdd, vocab);  //s0w+s1t+s1.rc.t
		addFeature(v, "FP27-" + sfst0 + SEP + spst1 + SEP + spst0lc, sAct, 1.0, bAdd, vocab); //s0w+s1t+s1.lc.t
		addFeature(v, "FP28-" + spst0 + SEP + spst1 + SEP + spst2, sAct, 1.0, bAdd, vocab);  //s0t+s1t+s2t

		addFeature(v, "FP29-", sAct, sAdjoin.equals("true") ? 1.0 : 0.0, bAdd, vocab);
		addFeature(v, "FP30-" + spst0 + SEP + spst1, sAct, sAdjoin.equals("true") ? 1.0 : 0.0, bAdd, vocab);
		
		//remove the punctuation features
		//addFeature(v, "FP31-" + sPunct, sAct, 1.0, bAdd, vocab); 
		//addFeature(v, "FP32-" + spst0 + SEP + spst1 + SEP + sPunct, sAct, 1.0, bAdd, vocab);
		return spqf1;
	}

	static void setTagSyntacticFeatures(
			IntFeatVector v, Vocab vocab, boolean bAdd, String sAct,
			String sfst0, String sfqf1, String spst0, String spst1, String spst0lc)
	{
		final int ln_sfst0 = sfst0.length();
		final char c_sfst0_b = sfst0.charAt(0); //s0w first char
		final char c_sfst0_e = sfst0.charAt(ln_sfst0 - 1); //s0w last char
		
		addFeature(v, "SF01-" + sfst0 + SEP + sfqf1, sAct, 1.0, bAdd, vocab); //s0w+q0w
		addFeature(v, "SF02-" + spst0 + SEP + sfqf1, sAct, 1.0, bAdd, vocab); //s0t+q0w
		addFeature(v, "SF03-" + spst0 + SEP + spst0lc + sfqf1, sAct, 1.0, bAdd, vocab); //s0t+s0.lc.t+q0w
		addFeature(v, "SF04-" + c_sfst0_b, sAct, 1.0, bAdd, vocab); // s0w.b
		addFeature(v, "SF05-" + c_sfst0_e, sAct, 1.0, bAdd, vocab); //s0w.e
		addFeature(v, "SF06-" + c_sfst0_e + sfqf1, sAct, 1.0, bAdd, vocab); //s0w.e+q0w
		addFeature(v, "SF07-" + spst1 + SEP + spst0 + SEP + sfqf1, sAct, 1.0, bAdd, vocab); // s1t+s0t+q0w
	}
	
	/**
	 * The function to set the entity features
	 * @param v
	 * @param vocab
	 * @param bAdd
	 * @param sAct
	 * @param sfqf1: q0w
	 * @param spqf1: q0t
	 * @param sfqp1: q_{idx-1}w
	 * @param spqp1: q_{idx-1}t
	 * @param sfqf2: q_{idx+1}w
	 * @param spqf2: q_{idx+1}t
	 */
	static void setEntityFeatures(IntFeatVector v, Vocab vocab, boolean bAdd, String sAct,
			String sfst0, String spst0, String spst0lc, String spst0rc,
			String sfqf1, String spqf1, String sfqp1, String spqp1, String sfqf2, String spqf2, String prevEntity){
		addFeature(v, "EN01-" + sfqf1, sAct, 1.0, bAdd, vocab);
		addFeature(v, "EN02-" + spqf1, sAct, 1.0, bAdd, vocab);
		addFeature(v, "EN03-" + sfqp1, sAct, 1.0, bAdd, vocab);
		addFeature(v, "EN04-" + spqp1, sAct, 1.0, bAdd, vocab);
		addFeature(v, "EN05-" + sfqf2, sAct, 1.0, bAdd, vocab);
		addFeature(v, "EN06-" + spqf2, sAct, 1.0, bAdd, vocab);
		
		addFeature(v, "EN07-" + spqp1 + SEP + spqf1, sAct, 1.0, bAdd, vocab);
		
		for(int plen = 1;plen<=6;plen++){
			if(sfqf1.length()>=plen){
				String suff = sfqf1.substring(sfqf1.length()-plen, sfqf1.length());
				addFeature(v, "EN08-"+ "LEN:"+ plen + SEP + suff, sAct, 1.0, bAdd, vocab);
				String pref = sfqf1.substring(0,plen);
				addFeature(v, "EN09-"+ "LEN:"+ plen + SEP + pref, sAct, 1.0, bAdd, vocab);
			}
		}
		addFeature(v, "EN10-"+ prevEntity, sAct, 1.0, bAdd, vocab);
		addFeature(v, "EN11-"+ sfqf1 + SEP + prevEntity, sAct, 1.0, bAdd, vocab);
		addFeature(v, "EN12-"+ sfqp1 + SEP + prevEntity, sAct, 1.0, bAdd, vocab);
		addFeature(v, "EN13-"+ sfqf2 + SEP + prevEntity, sAct, 1.0, bAdd, vocab);
		
		addFeature(v, "EN14-"+ spqf1 + SEP + prevEntity, sAct, 1.0, bAdd, vocab);
		addFeature(v, "EN15-"+ spqp1 + SEP + prevEntity, sAct, 1.0, bAdd, vocab);
		addFeature(v, "EN16-"+ spqf2 + SEP + prevEntity, sAct, 1.0, bAdd, vocab);
		addFeature(v, "EN17-"+ spqp1 + SEP + spqf1 + SEP + prevEntity, sAct, 1.0, bAdd, vocab);
		
		
		addFeature(v, "EN18-" + sfst0, sAct, 1.0, bAdd, vocab);
		addFeature(v, "EN19-" + spst0, sAct, 1.0, bAdd, vocab);
		addFeature(v, "EN20-" + sfst0 + SEP + sfqf1, sAct, 1.0, bAdd, vocab);
		addFeature(v, "EN21-" + spst0 + SEP + sfqf1, sAct, 1.0, bAdd, vocab);
		addFeature(v, "EN22-" + spst0 + SEP + spst0rc, sAct, 1.0, bAdd, vocab);
		addFeature(v, "EN23-" + spst0 + SEP + spst0lc, sAct, 1.0, bAdd, vocab);
		
		addFeature(v, "EN24-" + sfst0 + SEP + spst0 + SEP + spst0rc, sAct, 1.0, bAdd, vocab);
		addFeature(v, "EN25-" + sfst0 + SEP + spst0 + SEP + spst0lc, sAct, 1.0, bAdd, vocab);
	}
	
	

}
