package corbit.commons.dict;

import java.util.Set;
import java.util.TreeSet;

public class EngTagDictionary extends TagDictionary {

	//public static final String[] ssEngTags = { "NNS","CC","DT","JJ","NN","VBZ","IN",".","WP"};
	public static final String[] ssEngTags = { "B-person","I-person","O","B-gpe"};
	public static final String[] arcLabels = { "a","b","c","d","e","f","g","h","i","j","k"
			,"l","m","n","o","p","q","r","s","t","u","v","w", };
	
	
	public EngTagDictionary(){
		super(null, null, ssEngTags, arcLabels);
	}

	private static final long serialVersionUID = -2119018635069478671L;

	@Override
	public Set<String> generateTagSet() {
		Set<String> ss = new TreeSet<String>();
		for (int i = 0; i < ssEngTags.length; ++i)
			ss.add(ssEngTags[i]);
		return ss;
	}

	@Override
	public String[] getArcLabels() {
		return arcLabels;
	}

}
