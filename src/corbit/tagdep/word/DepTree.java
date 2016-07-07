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

package corbit.tagdep.word;

import java.util.ArrayList;
import java.util.List;

public class DepTree extends DepWord
{

	public final List<DepTree> children;

	public DepTree(DepTreeSentence sent, int index, String form, String pos,
			int head)
	{
		super(sent, index, form, pos, head);
		children = new ArrayList<DepTree>();
	}

	public DepTree(DepTree t)
	{
		super(t);
		children = new ArrayList<DepTree>(t.children);
	}

	public DepTree(DepWord w)
	{
		super(w);
		children = new ArrayList<DepTree>();
	}

	public DepTree()
	{
		super();
		children = new ArrayList<DepTree>();
	}

	@Override
	public boolean equals(Object obj)
	{
		if (obj == null || !(obj instanceof DepTree))
			return false;
		DepTree dt = (DepTree)obj;
		if (!super.equals((DepWord)dt) || children.size() != dt.children.size())
			return false;
		for (int i = 0; i < children.size(); ++i)
			if (!children.get(i).equals(dt.children.get(i)))
				return false;
		return true;
	}

	@Override
	public int hashCode()
	{
		int h = super.hashCode();
		for (int i = 0; i < children.size(); ++i)
			h = (h << 1 | h >> 31) ^ children.get(i).hashCode();
		return h;
	}

	@Override
	public String toString()
	{
		StringBuilder sb = new StringBuilder();
		sb.append(super.toString());
		sb.append(' ');
		if (children.size() > 0)
		{
			sb.append("{ ");
			for (int i = 0; i < children.size(); ++i)
			{
				sb.append(children.get(i).toString());
				if (i < children.size() - 1)
					sb.append(", ");
			}
			sb.append("} ");
		}
		return sb.toString();
	}
}
