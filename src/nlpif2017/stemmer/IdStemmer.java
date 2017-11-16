/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nlpif2017.stemmer;

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

    private final static char[] GHQ = {'g','h','q'};

    private final static String[] INFLECTION_SUFFIXES = {
        "lah", "kah", "ku", "mu", "nya", "tah", "pun"
    };
    
    private final static String[] INFLECTION_SUFFIXES_PARTICLES = {
        "lah", "kah", "tah", "pun"
    };
    
    private final static String[] INFLECTION_SUFFIXES_POSSESIVE_PRONOUNS = {
       "ku", "mu", "nya"
    };
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
//                    String initialStem = word.replaceFirst("per","");
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
        //rule 1
        //contoh : berusaha -> usaha, berambut -> rambut
        new Rule("1",
                (w) -> w.startsWith("ber") && isVowel(w.charAt(3)),
                (w) -> {
                    ArrayList<String> possibleBaseWords = new ArrayList<>();
                    String initialStem;
                    if(BaseWordsManager.isBaseWord(w.substring(2, w.length())) == true){
                        initialStem = w.replaceFirst("be","");
                    }
                    else{
                        initialStem = w.replaceFirst("ber","");
                    }
                        
                    possibleBaseWords.add(initialStem);
                    possibleBaseWords.add("r" + initialStem);
                    
                    return BaseWordsManager.getFirstMatch(possibleBaseWords,w);
                }),
        //rule 2
        //contoh : berlari -> lari, berjalan -> jalan, berfaedah -> faedah
        new Rule("2",
                (w) -> (w.startsWith("ber") && isConsonant(w.charAt(3))) || (w.startsWith("ber") && isConsonant(w.charAt(3)) && (w.charAt(3) != 'r')
                        && (w.charAt(5) != 'e') && (w.charAt(6) != 'r')),
                (w) -> {
                    ArrayList<String> possibleBaseWords = new ArrayList<>();
                    String initialStem = w.replaceFirst("ber","");
                        
                    possibleBaseWords.add(initialStem);
                    possibleBaseWords.add("r" + initialStem);
                    
                    return BaseWordsManager.getFirstMatch(possibleBaseWords,w);
                }),
        //rule 3
        new Rule("3",
                (w) -> w.startsWith("ber") && isConsonant(w.charAt(3)) && (w.charAt(3) != 'r')
                        && (w.charAt(4) == 'a') && (w.charAt(5) == 'e') && (w.charAt(6) == 'r'),
                (w) -> {
                    ArrayList<String> possibleBaseWords = new ArrayList<>();
                    String initialStem = w.replaceFirst("ber","");
                        
                    possibleBaseWords.add(initialStem);
                    possibleBaseWords.add("r" + initialStem);
                    
                    return BaseWordsManager.getFirstMatch(possibleBaseWords,w);
                }),
        //rule 4
        //contoh : belajar -> ajar, belunjur -> lunjur
        new Rule("4",
                (w) -> w.startsWith("bel") && isVowel(w.charAt(3)),
                (w) -> {
                    ArrayList<String> possibleBaseWords = new ArrayList<>();
                    String initialStem = w.replaceFirst("bel","");
                    //return w.replaceFirst("bel","");
                    possibleBaseWords.add(initialStem);
                    possibleBaseWords.add("l" + initialStem);
                    
                    return BaseWordsManager.getFirstMatch(possibleBaseWords,w);
                }),
        //rule 5
        //contoh : beternak -> ternak, bekerja -> kerja
        new Rule("5",
                (w) -> w.startsWith("be") && !isVowel(w.charAt(2)) &&
                        (w.charAt(2) != 'r') && (w.charAt(2) != 'l') &&
                        (w.charAt(3) == 'e') && (w.charAt(4) == 'r') ,
                (w) -> {
                    ArrayList<String> possibleBaseWords = new ArrayList<>();
                    String initialStem = w.replaceFirst("be","");
                    
                    possibleBaseWords.add(initialStem);
                    possibleBaseWords.add("e" + initialStem);
                    
                    return BaseWordsManager.getFirstMatch(possibleBaseWords,w);
                }),
        //rule 6
        //contoh : terikat -> ikat, terasa -> rasa
        new Rule("6",
                (w) -> w.startsWith("ter") && isVowel(w.charAt(3)),
                (w) -> {
                    ArrayList<String> possibleBaseWords = new ArrayList<>();
                    String initialStem;
                    if(BaseWordsManager.isBaseWord(w.substring(2, w.length())) == true){
                        initialStem = w.replaceFirst("te","");
                    }
                    else{
                        initialStem = w.replaceFirst("ter","");
                    }
                        
                    possibleBaseWords.add(initialStem);
                    possibleBaseWords.add("r" + initialStem);
                    
                    return BaseWordsManager.getFirstMatch(possibleBaseWords,w);
                }),
        //rule 7
        new Rule("7",
                (w) -> w.startsWith("ter") && isConsonant(w.charAt(3)) && (w.charAt(3) != 'r')
                        && (w.charAt(4) == 'e') && (w.charAt(5) == 'r') && isVowel(w.charAt(6)),
                (w) -> {
                    ArrayList<String> possibleBaseWords = new ArrayList<>();
                    String initialStem = w.replaceFirst("ter","");
                        
                    possibleBaseWords.add(initialStem);
                    possibleBaseWords.add("r" + initialStem);
                    
                    return BaseWordsManager.getFirstMatch(possibleBaseWords,w);
                }),
        //rule 8
        new Rule("8",
                (w) -> (w.length() > 6) ? w.startsWith("ter") && w.charAt(3) != 'r' && !w.substring(4,6).equals("er") : false,
                (w) -> w.replaceFirst("ter", "")),
        //rule 9
        new Rule("9",
                (w) -> (w.length() > 5) ? w.startsWith("te") && w.charAt(2) != 'r' && w.substring(3, 5).equals("er") : false,
                (w) -> w.replaceFirst("te", "")),
       //rule 10
        new Rule("10", 
                (w) -> (w.length() > 4) ? w.startsWith("me") && isVowel(w.charAt(3)) && (w.charAt(2) == 'l' || w.charAt(2) == 'r' || w.charAt(2) == 'w' || w.charAt(2) == 'y') : false,
                (w) -> w.replaceFirst("me", "")),
        //rule 11
        new Rule("11", 
                (w) -> (w.length() > 4) ? w.startsWith("mem") && (w.charAt(3) == 'b' || w.charAt(3) == 'f' || w.charAt(3) == 'v') : false,
                (w) -> w.replaceFirst("mem", "")),
        //rule 12
        new Rule("12", 
                (w) -> (w.length() > 5) ? w.startsWith("mempe") && (w.charAt(5) == 'r' || w.charAt(5) == 'l') : false,
                (w) -> w.replaceFirst("mempe", "pe")),
        //rule 13
        new Rule("13", 
                (w) -> (w.length() > 4) ? w.startsWith("mem") && ( w.charAt(3) == 'r' && isVowel(w.charAt(4)) ) || isVowel(w.charAt(3)) : false,
                (w) -> {
                    ArrayList<String> possibleBaseWords = new ArrayList<>();
                    String initialStem = w.replaceFirst("mem", "");
                    
                    possibleBaseWords.add("p" + initialStem);
                    possibleBaseWords.add("m" + initialStem);
                    
                    return BaseWordsManager.getFirstMatch(possibleBaseWords,w);
                }),
        //rule 14
        new Rule("14", 
                (w) -> (w.length() > 3) ? w.startsWith("men") && (w.charAt(3) == 'c' || w.charAt(3) == 'd' || w.charAt(3) == 'j' || w.charAt(3) == 'z') : false,
                (w) -> w.replaceFirst("men", "")),
        //rule 15
        new Rule("15", 
                (w) -> w.startsWith("men") && isVowel(w, 3),
                (String w) -> {
                ArrayList<String> possibleBaseWords = new ArrayList<>();
                String initialStem = w.replaceFirst("men","");
                
                possibleBaseWords.add("n"+ initialStem);
                possibleBaseWords.add("t" + initialStem);
                
                return BaseWordsManager.getFirstMatch(possibleBaseWords);
                }),
        //rule 16
        new Rule("16", 
                (w) -> w.startsWith("meng") && isGHQ(w, 4),
                (w) -> w.replaceFirst("meng","")),
        //rule 17
        new Rule("17", 
                (w) -> w.startsWith("meng") && isVowel(w, 4),
                (w) -> {
                ArrayList<String> possibleBaseWords = new ArrayList<>();
                String initialStem = w.replaceFirst("meng","");
                
                possibleBaseWords.add("k" + initialStem);
                possibleBaseWords.add(initialStem);
                
                return BaseWordsManager.getFirstMatch(possibleBaseWords
                        );
                }),
        //rule 18
        new Rule("18", 
                (w) -> w.startsWith("meny") && isVowel(w, 4),
                (w) -> w.replaceFirst("meny","s")),
        //rule 19
        new Rule("19", 
                (w) -> w.startsWith("memp") && isVowel(w,4),
                (w) -> w.replaceFirst("mem","")),
        //rule 20
        new Rule("20", 
                (w) -> w.startsWith("pew")|| w.startsWith("pey") && isVowel(w,4),
                (w) -> w.replaceFirst("pe","")),
        //rule 21
        new Rule("21", 
                (w) -> w.startsWith("per") && isVowel(w, 3),
                (w) -> {
                ArrayList<String> possibleBaseWords = new ArrayList<>();
                String initialStem = w.replaceFirst("per","");
                
                possibleBaseWords.add("r" + initialStem);
                possibleBaseWords.add(initialStem);
               
                
                return BaseWordsManager.getFirstMatch(possibleBaseWords,
                        "r" + initialStem);
                }),
        new Rule("23/24",
                 (w) -> w.startsWith("per"),
                 (w) -> w.replaceFirst("per", "")),
        new Rule("25", 
                (w) -> w.matches("^pem([bf])[a-z]+$"),
                (w) -> w.replaceFirst("pem","")),
        new Rule("26", 
                (w) -> w.matches("^pem(([r][aiueo])|[aiueo])[a-z]+$"),
                (w) -> {
                ArrayList<String> possibleBaseWords = new ArrayList<>();
                String initialStem = w.replaceFirst("pem","");
                
                possibleBaseWords.add("p" + initialStem);
                possibleBaseWords.add("m" + initialStem);
               
                return BaseWordsManager.getFirstMatch(possibleBaseWords,w);}),
        new Rule("27", 
                (w) -> w.matches("^pen[cjdz][a-z]+$"),
                (w) -> w.replaceFirst("pen","")),
        //rule 28
        new Rule("28", 
                (w) -> w.startsWith("pen") && isVowel(w, 3),
                (w) -> {
                ArrayList<String> possibleBaseWords = new ArrayList<>();
                String initialStem = w.replaceFirst("pen","");
                
                possibleBaseWords.add("n" + initialStem);
                possibleBaseWords.add("t" + initialStem);
                
                return BaseWordsManager.getFirstMatch(possibleBaseWords,
                        w);
                }),
        //rule 29
        new Rule("29", 
                (w) -> w.startsWith("peng") && isGHQ(w, 4),
                (w) -> {
                
                return w.replaceFirst("peng","");
                }),
        //rule 30
        new Rule("30", 
                (w) -> w.startsWith("peng") && isVowel(w, 4),
                (w) -> {
                ArrayList<String> possibleBaseWords = new ArrayList<>();
                String initialStem = w.replaceFirst("peng","");
                
                possibleBaseWords.add(initialStem);
                possibleBaseWords.add("k" + initialStem);
                
                return BaseWordsManager.getFirstMatch(possibleBaseWords,
                        w);
                }),
        //rule 31
        new Rule("31", 
                (w) -> w.startsWith("peny") && isVowel(w, 4),
                (w) -> w.replaceFirst("peny","s")),
        //rule 32
        new Rule("32", 
                (w) -> w.startsWith("pel") && isVowel(w ,3),
                (w) -> w.equals("pelajar") ? "ajar" : w.replaceFirst("pel","l")),
        new Rule("33",
                (w) -> w.matches("^pe[^rwylmnaiueo\\W\\d](er)[aiueo][a-z]+$"),
                (w) -> w.substring(3)),
        new Rule("34", 
                (w) -> w.matches("^pe([^rwylmnaiueo\\W\\d])([^(er)\\W\\d])[a-z]+$"),
                (w) -> w.replaceFirst("pe",""))
    };
    
    private static boolean isConsonant(char c){
        Stream<Character> consonantStream = IntStream.range(0, CONSONANTS.length).mapToObj(i -> CONSONANTS[i]);
        return consonantStream.anyMatch((s) -> s == c);
    }
    
    private static boolean isConsonant(String word, int pos){
        //Apabila posisi diluar panjang string, return false
        if(word.length() < pos+1){
            return false;
        }
        
        return isConsonant(word.charAt(pos));
    }
    
    private static boolean isVowel(char c){
        Stream<Character> vowelStream = IntStream.range(0, VOWELS.length).mapToObj(i -> VOWELS[i]);
        return vowelStream.anyMatch((s) -> s == c);
    }
    
    private static boolean isVowel(String word, int pos){
        //Apabila posisi diluar panjang string, return false
        if(word.length() < pos+1){
            return false;
        }
        
        return isVowel(word.charAt(pos));
    }
    
    private static boolean isGHQ(char c){
        Stream<Character> GHQStream = IntStream.range(0, GHQ.length).mapToObj(i -> GHQ[i]);
        return GHQStream.anyMatch((s) -> s == c);
    }
    
     private static boolean isGHQ(String word, int pos){
        //Apabila posisi diluar panjang string, return false
        if(word.length() < pos+1){
            return false;
        }
        
        return isGHQ(word.charAt(pos));
    }
     
     private static boolean isParticle(String inflectionSuffix){
         for(String particle : INFLECTION_SUFFIXES_PARTICLES){
             if(inflectionSuffix.equals(particle)){
                 return true;
             }
         }
         return false;
     }
    
    private static String preprocess(String word){
        return word.trim().toLowerCase();
    }
    
    private static String cutSuffix(String word, String suffix){
       return word.substring(0, word.length() - suffix.length());
    }
    
    private static String cutPossesivePronouns(String word){
        
        for(String possesive_pronoun : INFLECTION_SUFFIXES_POSSESIVE_PRONOUNS){
            if(word.endsWith(possesive_pronoun)){
                return cutSuffix(word, possesive_pronoun);
            }
        }
        
        return word;
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
        System.out.println("Langkah 2: " + processedQuery);
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
       
        String result = word;
        String cut_suffix = null;
        
        for(String suffix : INFLECTION_SUFFIXES){
            if(word.endsWith(suffix)){
                result = cutSuffix(result, suffix);
                cut_suffix = suffix;
            }
        }
        
        //kalau tidak null, artinya ada suffix yang dipotong.
        if(cut_suffix != null){
            if(isParticle(cut_suffix)){
                result = cutPossesivePronouns(result);
            }
        }
        
        return result;
    }
    
     /*
    Aplikasi aturan ketiga
    */
    private static String applyStep3(String word){
        String result;
        
        if(BaseWordsManager.isBaseWord(word)){
            return word;
        }
        
        if(word.endsWith("i")){
            result = cutSuffix(word,"i");
            
            if(BaseWordsManager.isBaseWord(applyStep4(result))){
                return result;
            } 
        }
        
        if(word.endsWith("an")){
            result = cutSuffix(word,"an");
                
            if(BaseWordsManager.isBaseWord(applyStep4(result))){
                return result;
            }
            
            if(result.endsWith("k")) {
                result = cutSuffix(result, "k");
                
                if(BaseWordsManager.isBaseWord(applyStep4(result))){
                    return result;
                }
            }
            
        }
        
        return word;
    }
    
    private static String applyStep4(String word){
        String result = word;
        String temp = result;
        
        int ruleCount = 0;
        String previousRule = "";
        String currentRule;
        
        if(BaseWordsManager.isBaseWord(word)){
            return word;
        }
        
        while(ruleCount < 3){
            currentRule = previousRule;
            
            //Cek jika kata mengandung pasangan prefix-suffix yang dilarang
            if(containsForbiddenPair(word)){
                return word;
            }
            
            for(Rule rule : RULES){
                if(rule.match(result)){
                    currentRule = rule.definition();
                    System.out.println(rule.definition() + " applied.");
                    temp = rule.apply(result);
                    if(BaseWordsManager.isBaseWord(temp)){
                        break;
                    }
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
            if(BaseWordsManager.isBaseWord(result)){
                break;
            }
            
            ruleCount++;
        }
        
        System.out.println("Rule 4 iterates " + ruleCount + " times.");
        
        return result;
    }
}
