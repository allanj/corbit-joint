package corbit.tagdep.dict;

import java.util.Set;
import java.util.TreeSet;

public class MaltTagDictionary extends TagDictionary {

	
	//public static final String[] ssEngTags = { "NNS","CC","DT","JJ","NN","VBZ","IN",".","WP"};
	public static final String[] ssEngTags = { "B-person","I-person","B-gpe","I-gpe","B-organization","I-organization","B-MISC","I-MISC","O"};
	
	
	public MaltTagDictionary(){
		super(null, null, ssEngTags);
	}

	public static Set<String> copyTagSet()
	{
		Set<String> ss = new TreeSet<String>();
		for (int i = 0; i < ssEngTags.length; ++i)
			ss.add(ssEngTags[i]);
		return ss;
	}
}
