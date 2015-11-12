import java.io.BufferedReader;                   //all the imported packages
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.*;
import java.math.*;
public class InvertedIndex {
         // stop words that are ignored
	List<String> stopwords = Arrays.asList("a", "able", "about",
			"across", "after", "all", "almost", "also", "am", "among", "an",
			"and", "any", "are", "as", "at", "be", "because", "been", "but",
			"by", "can", "cannot", "could", "dear", "did", "do", "does",
			"either", "else", "ever", "every", "for", "from", "get", "got",
			"had", "has", "have", "he", "her", "hers", "him", "his", "how",
			"however", "i", "if", "in", "into", "is", "it", "its", "just",
			"least", "let", "like", "likely", "may", "me", "might", "most",
			"must", "my", "neither", "no", "nor", "not", "of", "off", "often",
			"on", "only", "or", "other", "our", "own", "rather", "said", "say",
			"says", "she", "should", "since", "so", "some", "than", "that",
			"the", "their", "them", "then", "there", "these", "they", "this",
			"tis", "to", "too", "twas", "us", "wants", "was", "we", "were",
			"what", "when", "where", "which", "while", "who", "whom", "why",
			"will", "with", "would", "yet", "you", "your");
 
	Map<String, List<Tuple>> index = new HashMap<String, List<Tuple>>();  //this is our index data structure
	List<String> files = new ArrayList<String>();                         //list of all files
 	List<String> answer;                                                  //list of answer docs
 	Map<String, Integer> dfreq;                                           //dfreq of words
 	Map<Integer, Double> tfidf;                                           //this will contain tfidf values for each file
 	Map<Integer, Double> tfidf1;
 	
 	//indexing file names
	public void indexFile(File file) throws IOException {
	
		int fileno = files.indexOf(file.getPath());
		if (fileno == -1) {
			files.add(file.getPath());
			fileno = files.size() - 1;
		}
                
		int pos = 0;   //pos contains count of numbers in doc
		
		//we read each word from the document and add it to the index
		BufferedReader reader = new BufferedReader(new FileReader(file));
		for (String line = reader.readLine(); line != null; line = reader
				.readLine()) {
			for (String _word : line.split(" ")) {
			
				String word = _word.toLowerCase();
				pos++;
				if (stopwords.contains(word))         //ignoring stop words
					continue;
					
				//getting the docs linked list for the word
				List<Tuple> idx = index.get(word);
				Tuple end = null;            //end of the linked list
				if(idx != null)           
				{
					for(Tuple t : idx)
						{
							end = t;
						}
				}	
				if (idx == null)                    //if word is not present add it to the index
				{
					idx = new LinkedList<Tuple>();
					//System.out.println("adding  " + word);
					index.put(word, idx);
					
				}
				if(end == null || end.fileno != fileno)           //adding new node for the doc
						idx.add(new Tuple(fileno,1));
				else
					end.freq++;                               //increase freq if ocurring in same document
		
						
			}
		}
		System.out.println("indexed " + file.getPath() + " " + pos + " words");
		
		
	}
	
	//this method build the document frequency hashmap for each word
	public void docFreq()
	{
		int nwords = index.size(); //unique words in the dictionary
		System.out.println("number of words in dictionary : " + nwords);
		dfreq = new HashMap<String, Integer>();
		Iterator it = index.entrySet().iterator();
		
		//iterating through the hashmap
		    while (it.hasNext())
		    {
			Map.Entry pair = (Map.Entry)it.next();
			List<Tuple> ll = (List<Tuple>)pair.getValue();
			dfreq.put((String)pair.getKey(),(Integer)ll.size());    //adding it to hashmap
			//it.remove(); // avoids a ConcurrentModificationException
		    }
			//printing the document frequency map
			
			Iterator<Map.Entry<String, Integer>> i = dfreq.entrySet().iterator(); 
			while(i.hasNext()){
			    String key = i.next().getKey();
			//    System.out.println(key+"->"+ dfreq.get(key));
			}
	
	
	}
 
	public void search(List<String> words)
	{
	
		int N = 768;
		tfidf = new HashMap<Integer, Double>();  
		tfidf1 = new HashMap<Integer, Double>();
		System.out.println("Searching for:");
		for (String _word : words)
		{
			answer = new ArrayList<String>();
			double s,r;
			String word = _word.toLowerCase();
			System.out.print(word+" " );                         //printing word that is being searched for
			List<Tuple> idx = index.get(word);
			if(idx == null)
			{
				System.out.println("Not found in any file");
			}
			if (idx != null)
			{
				
				//System.out.println("Not");
				s = Math.log10(N/(dfreq.get(word)));
				int loop=0;
				for (Tuple t : idx)
				{
					//answer.put(files.get(t.fileno), t.freq);
					
					loop++;
					int document = t.fileno;
					r = s * Math.log10(1+t.freq);
					//System.out.println("in file " + files.get(t.fileno) + " "+ t.freq);
					//double val = tfidf.get(t.fileno);
					if(tfidf.containsKey(t.fileno))
					{
						//System.out.print("contanis");
						// if already exits then adding and updating
						tfidf.put(t.fileno, r+tfidf.get(t.fileno));
					}
					else
					{
						//adding for new docs
						tfidf.put(t.fileno, r);
					}
					
					//for denominator
					if(tfidf1.containsKey(t.fileno))
					{
						//System.out.print("contanis");
						// if already exits then adding and updating
						tfidf1.put(t.fileno, (r*r) +tfidf1.get(t.fileno));
					}
					else
					{
						//adding for new docs
						tfidf1.put(t.fileno, r*r);
					}
					
				}
				//System.out.println("loop ran " + loop);
			}
			
			
			
		}
		System.out.println(" ");
		
		Iterator it = tfidf1.entrySet().iterator();
		
		//  calculating denominator vales to calculate vector
		    while (it.hasNext())
		    {
			Map.Entry pair = (Map.Entry)it.next();
			//List<Tuple> ll = (List<Tuple>)pair.getValue();
			Double d = (Double)pair.getValue();
			Integer a = (Integer)pair.getKey();
			Double dd = tfidf.get(a)/(Math.sqrt(d));
			tfidf.put(a,dd);    //adding it to hashmap
			//it.remove(); // avoids a ConcurrentModificationException
		    }
		     //sorting of the tfidf values  
		
			List<Integer> docs = new ArrayList<Integer>(tfidf.keySet());
			Collections.sort(docs, new Comparator<Integer>() {
			    @Override
			    public int compare(Integer s1, Integer s2) {
				Double rank1 = tfidf.get(s1);
				Double rank2 = tfidf.get(s2);
				return rank2.compareTo(rank1);
			    }
			});
			
			//printing top 10
			int itr=0;
			for(itr=0;itr<15;itr++)
			{
				//System.out.print(files.get(docs.get(itr)));
				//System.out.println(" "+tfidf.get(docs.get(itr)));
				answer.add(files.get(docs.get(itr)));
			}
			
	}
 
	public static void main(String[] args) {
		try {
			
			long startTime = System.currentTimeMillis();
			InvertedIndex idx = new InvertedIndex();
			for (int i = 0; i < 768; i++) {
				//idx.indexFile(new File("corpus/split"+i+".txt"));
				idx.indexFile(new File("corpus/split"+i+".txt"));
			}
			long stopTime = System.currentTimeMillis();
			System.out.println("Time taken for indexing = " + (stopTime - startTime) + " ms");
			
			idx.docFreq();
			idx.search(Arrays.asList(args[0].split(",")));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
 
       //linked list node data structure
	private class Tuple {
		private int fileno;
		private int freq;
 
		public Tuple(int fileno, int freq) {
			this.fileno = fileno;
			this.freq = freq;
		}
	}
}
 
 
