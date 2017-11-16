/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlp.stemmer.kel.pkg1;

import java.util.ArrayList;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 *
 * @author rynfd
 */
public class IdStemmer {
    
    private final static char[] CONSONANTS = {'b','c','d','f','g','h','j','k',
        'l','m','n','p','q','r','s','t','v','w','x','y','z'};
    
    private final static char[] VOWELS = {'a','i','u','e','o'};

    private final static PrefixSuffixPair[] FORBIDDEN_PREFIX_SUFFIX_PAIRS = {
        new PrefixSuffixPair("be", "i"),
        new PrefixSuffixPair("di", "an"),
        new PrefixSuffixPair("ke", "i"),
        new PrefixSuffixPair("ke", "kan"),
        new PrefixSuffixPair("me", "an"),
        new PrefixSuffixPair("se", "i"),
        new PrefixSuffixPair("se", "kan"),
        new PrefixSuffixPair("te", "an")
    };
    
//Definisi Rule 21 pada array setara dengan:
//    
//    Rule r = new Rule("21", 
//            new RuleMatcher() {
//                @Override
//                public boolean match(String word) {
//                    if(word.startsWith("per")){
//                        if(isVowel(word.charAt(3))){
//                            return true;
//                        }
//                    }
//                    return false;
//                }},
//            new RuleStemmer() {
//                @Override
//                public String process(String word) {
//                    ArrayList<String> possibleBaseWords = new ArrayList<>();
//                    String initialStem = word.replace("per","");
//                
//                    possibleBaseWords.add(initialStem);
//                    possibleBaseWords.add("r" + initialStem);
//                
//                    return BaseWordsManager.getFirstMatch(possibleBaseWords);
//                }
//            });
    
    private final static Rule[] RULES = {
        //Aturan standar di-
        new Rule("di",
                (w) -> w.startsWith("di"),
                (w) -> w.replaceFirst("di","")),
        //Aturan standar ke-
        new Rule("ke",
                (w) -> w.startsWith("ke"),
                (w) -> w.replaceFirst("ke","")),
        //Aturan standar se-
        new Rule("se",
                (w) -> w.startsWith("se"),
                (w) -> w.replaceFirst("se","")),
        //rule 1-20
        //........................
         //rule 21
         new Rule("21", 
                (w) -> w.startsWith("per") && isVowel(w.charAt(3)),
                (w) -> {
                ArrayList<String> possibleBaseWords = new ArrayList<>();
                String initialStem = w.replace("per","");
                
                possibleBaseWords.add(initialStem);
                possibleBaseWords.add("r" + initialStem);
                
                return BaseWordsManager.getFirstMatch(possibleBaseWords,
                        "r" + initialStem);
                }),
    
       new Rule("8",
                (w) -> w.startsWith("ter") && w.charAt(3) != 'r' && !w.substring(4,6).equals("er"),
                (w) -> w.replaceFirst("ter", "")),
        new Rule("9",
                (w) -> w.startsWith("te") && w.charAt(2) != 'r' && w.substring(3, 5).equals("er"),
                (w) -> w.replaceFirst("te", "")),
       new Rule("10", 
                (w) -> w.startsWith("me") && isVowel(w.charAt(3)) && (w.charAt(2) == 'l' || w.charAt(2) == 'r' || w.charAt(2) == 'w' || w.charAt(2) == 'y'),
                (w) -> w.replaceFirst("me", "")),
       
        new Rule("11", 
                (w) -> w.startsWith("mem") && (w.charAt(3) == 'b' || w.charAt(3) == 'f' || w.charAt(3) == 'v'),
                (w) -> w.replaceFirst("mem", "")),
        new Rule("12", 
                (w) -> w.startsWith("mempe") && (w.charAt(5) == 'r' || w.charAt(5) == 'l'),
                (w) -> w.replaceFirst("mempe", "pe")),
        
        new Rule("13", 
                (w) -> w.startsWith("mem") && ( w.charAt(3) == 'r' && isVowel(w.charAt(4)) ) || isVowel(w.charAt(3)),
                (w) -> {
                    ArrayList<String> possibleBaseWords = new ArrayList<>();
                    String initialStem = w.replaceFirst("mem", "");
                    
                    if(BaseWordsManager.isBaseWord("m" + initialStem)){
                        possibleBaseWords.add("m" + initialStem);
                    } else if(BaseWordsManager.isBaseWord("p" + initialStem)){
                        possibleBaseWords.add("p" + initialStem);
                    }
                    
                    return BaseWordsManager.getFirstMatch(possibleBaseWords,w);
                }),
        
        new Rule("14", 
                (w) -> w.startsWith("men") && (w.charAt(3) == 'c' || w.charAt(3) == 'd' || w.charAt(3) == 'j' || w.charAt(3) == 'z') ,
                (w) -> w.replaceFirst("men", "")),
        
        new Rule("32", 
                (w) -> w.startsWith("pel") && isVowel(w.charAt(3)),
                (w) -> {
                
                 if(w.equals("pelajar")){
                     return "ajar";
                 }
                    
                return w.replace("pel","l");
                })
    };
    
    private static boolean isConsonant(char c){
        Stream<Character> consonantStream = IntStream.range(0, CONSONANTS.length).mapToObj(i -> CONSONANTS[i]);
        return consonantStream.anyMatch((s) -> s == c);
    }
    
    private static boolean isVowel(char c){
        Stream<Character> vowelStream = IntStream.range(0, VOWELS.length).mapToObj(i -> VOWELS[i]);
        return vowelStream.anyMatch((s) -> s == c);
    }
    private static String preprocess(String word){
        return word.trim().toLowerCase();
    }
    
     private static boolean containsForbiddenPair(String word){
        String prefix;
        String suffix;
        
        for(PrefixSuffixPair pair : FORBIDDEN_PREFIX_SUFFIX_PAIRS){
            prefix = pair.getPrefix();
            suffix = pair.getSuffix();
            
            if(word.startsWith(prefix) && word.endsWith(suffix)){
                System.out.println("Word contains illegal "
                        + "prefix and suffix pair: "
                        + pair.getPrefix() + "- & -" + pair.getSuffix());       
                return true;
            }
        }
        return false;
    }
    
    public static String stem(String word){
        String originalQuery = preprocess(word);
        
        //Cek apakah kata yang diberikan adalah kata dasar
        if(BaseWordsManager.isBaseWord(originalQuery)){
            System.out.println("Word given is a base word.");
            return originalQuery;
        }
        
        String processedQuery = originalQuery;
        
        //-----------------------------------------------
        //LANGKAH 2 
        processedQuery = applyStep2(processedQuery);
        //C4 apakah kata sudah berbentuk kata dasar 
        if(BaseWordsManager.isBaseWord(processedQuery)){
            return processedQuery;
        }
        
        //-----------------------------------------------
        //LANGKAH 3
        processedQuery = applyStep3(processedQuery);
        //Cek apakah kata sudah berbentuk kata dasar 
        if(BaseWordsManager.isBaseWord(processedQuery)){
            return processedQuery;
        }
        
        //-----------------------------------------------
        //LANGKAH 4
        processedQuery = applyStep4(processedQuery);
        //Cek apakah kata sudah berbentuk kata dasar
        if(BaseWordsManager.isBaseWord(processedQuery)){
            return processedQuery;
        }
        
        //Jika tidak ada di kamus, asumsikan query adalah kata dasar.
        return originalQuery;
    }
    
    /*
    Aplikasi aturan kedua
    */
    private static String applyStep2(String word){
        return word;
    }
    
     /*
    Aplikasi aturan ketiga
    */
    private static String applyStep3(String word){
        return word;
    }
    
    private static String applyStep4(String word){
        String result = word;
        String temp = result;
        
        int ruleCount = 0;
        String previousRule = "";
        String currentRule;
        
        while(ruleCount < 3){
            currentRule = previousRule;
            
            //Cek jika kata mengandung pasangan prefix-suffix yang dilarang
            if(containsForbiddenPair(word)){
                return word;
            }
            
            for(Rule rule : RULES){
                if(rule.match(result)){
                    currentRule = rule.definition();
                    temp = rule.apply(result);
                    break;
                }
            }

            /*
            Jika aturan sekarang dan sebelumnya sama, maka ada 2 kemungkinan:
            1. Tidak ada aturan yang match sama sekali pada iterasi terakhir
            2. Aturan yang match pada iterasi terakhir sama dengan aturan
               sebelumnya.
            Kedua kemungkinan menghentikan iterasi.
            */
            if(currentRule.equals(previousRule)){
                System.out.println("Process stops because either "
                        + "no rule matches or "
                        + "the same rule are matched twice in a row.");
                break;
            } else {
                previousRule = currentRule;
                result = temp;
            }
            
            //Cek apakah kata sudah berbentuk kata dasar
            if(BaseWordsManager.isBaseWord(word)){
                break;
            }
            
            ruleCount++;
        }
        
        System.out.println("Rule 4 iterates " + ruleCount + " times.");
        
        return result;
    }
}
