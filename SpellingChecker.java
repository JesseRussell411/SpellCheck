
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.SortedSet;
import java.util.TreeMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.Comparator;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author jesse
 */
public class SpellingChecker {
    private Set<String> words = new HashSet<String>();
    
    /**
     * Adds the given word to the dictionary.
     * @return true if the word was not in the dictionary already.
     */
    public boolean addWord(String word){
        return words.add(word);
    }
    
    
    /**
     * Removes the given word from the dictionary.
     * @return true if the word was in the dictionary.
     */
    public boolean removeWord(String word){
        return words.remove(word);
    }
    
    
    /**
     * @return true if the word is found in the dictionary.
     */
    public boolean checkSpelling(String word){
        return words.contains(word);
    }
    
    
    private class Pair<A, B>{
        public Pair(A a, B b){ this.a = a; this.b = b; }
        A a;
        B b;
    }
    /**
     * Returns a SortedMap of the words nearest to the given word no larger than the given size.
     * @param size Max size of the result.
     * @param maxEditDistance Max edit distance (number of changes, insertions, or removals) between the given word and each word in the result.
     */
    public List<String> findNearest(String word, int size, int maxEditDistance){
        if (words.size() == 0 || size <= 0 || maxEditDistance < 0) return new ArrayList<>();
        
        
        // set up list to store result. This list needs to be sorted every time a value is added. cmd can be given to Collections.sort.
        // *I'm doing it this way because TreeMap doesn't allow duplicate keys.
        List<Pair<Integer, String>> result = new ArrayList<>();
        Comparator<Pair<Integer, String>> cmp = (pair1, pair2) -> pair1.a.compareTo(pair2.a);
        
        
        var iter = words.iterator();
        
        
        // prime the list by filling it up to the max size with near matches, as long as the edit distance for each isn't too big.
        while(result.size() < size && iter.hasNext()){
            String w = iter.next();
            int dif = rateDif(w, word);
            if (dif <= maxEditDistance) result.add(new Pair(dif, w));
        }
        Collections.sort(result, cmp); // must sort list!
        
        
        // go through the rest of the words in the dictionary and add them to the list if they're better than what's already there. Still not letting the list grow past the max size.
        while(iter.hasNext()){
            String w = iter.next();
            
            int dif = rateDif(w, word);

            if (dif < result.get(result.size() - 1).a){
                result.remove(result.size() - 1);
                result.add(new Pair(dif, w));
                Collections.sort(result, cmp);
            }
            else if (result.size() < size && dif < maxEditDistance){
                result.add(new Pair(dif, w));
                Collections.sort(result, cmp);
            }
        }
        
        
        // copy the results into a more palatable format, trimming out the Pair objects.
        List<String> realResult = new ArrayList<String>();
        for(var p : result)
            realResult.add(p.b);
        
        
        //done
        return realResult;
    }
    
    // o--------------------o
    // | Utility Functions: |
    // o--------------------o
    
    /**
     * Rates the difference between the given words. The higher the return value, the less similar the words are. A value of 0 means the words are identical.
     */
    private static int rateDif(String word1, String word2){
        return minEditDist(word1, word2);
    }
    
    /**
     * Calculates the minimum edit distance between the two given strings.
     */
    private static int minEditDist(String a, String b){
        // based on this youtube video: https://www.youtube.com/watch?v=We3YDTzNXEk
        
        //     a b c d e f <- string a
        //   0 1 2 3 4 5 6
        // a 1 0 1 2 3 4 5
        // z 2 1 1 2 3 4 5
        // c 3 2 2 1 2 3 4
        // f 4 3 3 2 2 3(3) <- minimum edit distance
        // ^
        //  ` - stirng b
        
        int[][] T = new int[a.length() + 1][b.length() + 1];
        
        //     a b c d e f
        //
        // a
        // z
        // c
        // f
        
        for(int i = 0; i < a.length() + 1; ++i)
            T[i][0] = i;
        
        //     a b c d e f
        //   0 1 2 3 4 5 6
        // a
        // z
        // c
        // f
        
        for(int i = 0; i < b.length() + 1; ++i)
            T[0][i] = i;
        
        //     a b c d e f
        //   0 1 2 3 4 5 6
        // a 1
        // z 2
        // c 3
        // f 4
        
        for(int i = 1; i < a.length() + 1; ++i)
            for(int j = 1; j < b.length() + 1; ++j){
                if (a.charAt(i - 1) == b.charAt(j - 1))
                    T[i][j] = T[i - 1][j - 1];
                else
                    T[i][j] = 1 + Math.min(Math.min(T[i - 1][j], T[i][j - 1]), T[i - 1][j - 1]);    
            }
        
        //     a b c d e f
        //   0 1 2 3 4 5 6
        // a 1 0 1 2 3 4 5
        // z 2 1 1 2 3 4 5
        // c 3 2 2 1 2 3 4
        // f 4 3 3 2 2 3(3) <- value to return
        
        return T[a.length()][b.length()];
    }
}
