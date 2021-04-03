
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


/**
 * @author Jesse Russell
 */
public class SpellCheck {
    static SpellingChecker checker = new SpellingChecker();
    static final String output_path = "mydoc-checked.txt";
    static final int MAX_EDIT_DISTANCE = 2;
    static final int MAX_ALTERNATIVE_NUMBER = 10;
    private static final Scanner input = new Scanner(System.in);
    
    /**
     * Writes a user prompt to std out, asking the user to pick from a list of possible alternatives to their misspelled word.
     * @return The alternative chosen by the user.
     */
    static String getUserReplacement(String word, Integer altNumber, Integer maxDifference){
        if (altNumber == null) altNumber = MAX_ALTERNATIVE_NUMBER;
        if (maxDifference == null) maxDifference = MAX_EDIT_DISTANCE;
        
        List<String> alternativeChoices = new ArrayList<String>();
        alternativeChoices.add(null); //option: something else, lazy way of ignoring 0 and shifting everything up one.
        
        // Get the nearest words in the dictionary and add them to the user's choices.
        alternativeChoices.addAll(checker.findNearest(word.toLowerCase(), altNumber, maxDifference));
        
        // Print the choices for the user to select.
        System.out.println(word + ": did you mean:");
        int i = 1;
        for(; i < alternativeChoices.size(); ++i)
            System.out.println(String.format("%-4s", i + ". ") + alternativeChoices.get(i));
        
        System.out.println(String.format("%-4s", i + ". ") + word + " (no change)");
        System.out.println(String.format("%-4s", 0 + ". ") + "something else");
        
        // Get the user's choice.
        int choice = -1;
        while(choice == -1){
            try{
                choice = Integer.parseInt(input.nextLine());
            }
            catch(NumberFormatException e){
                choice = -1;
            }
            
            if(choice < 0 || choice > alternativeChoices.size()){
                choice = -1;
                System.out.println("Invalid choice");
            }
        }
        
        // Apply the user's choice
        String replacement = word;
        
        if (choice == 0){
            System.out.print("something else:");
            replacement = input.nextLine();
        }
        else if (choice == i){
            replacement = word;
        }
        else{
            replacement = alternativeChoices.get(choice);
        }
        
        // Asthetic line break, deffinitely after all printlns.
        System.out.println();
        
        // done
        return replacement;
    }
    
    public static void main(String[] args) throws IOException{
        if (args.length != 2){
            System.out.println("Wrong number of arguements. Expected 2, recieved " + args.length + ".");
            System.exit(1);
        }
        
        String words_path = args[0];
        String file_path  = args[1];
        
        // Read in words file and add each word to the spell checker's dictionary.
        try(BufferedReader reader = new BufferedReader(new FileReader(words_path))){
            String w;
            while((w = reader.readLine()) != null)
                checker.addWord(w.toLowerCase());
        }
        
        // Read the input file to the output file, prompting the user about every detected spelling mistake.
        try(BufferedReader reader = new BufferedReader(new FileReader(file_path))){
            try(BufferedWriter writer = new BufferedWriter(new FileWriter(output_path))){
                StringBuilder word = new StringBuilder();
                int i;
                do{
                    i = reader.read();
                    char c = i != -1 ? (char)i : '\n';
                    
                    if (Character.isAlphabetic(c) || c == '\'')
                        word.append(c);
                    else{
                        if (word.length() != 0){
                            if (checker.checkSpelling(word.toString().toLowerCase())){
                                writer.append(word);
                            }
                            else{
                                String replacement = getUserReplacement(word.toString(), null, null);
                                writer.append(replacement);
                            }
                            word = new StringBuilder();
                        }
                        
                        if (i != -1)
                            writer.append(c);
                    }
                    
                } while(i != -1);
                
                
                
            }
        }
        
        
        
    }
}
