package tfip.project.appbackend.services;

import java.util.LinkedList;
import java.util.List;


public class Util {

    public static String toTitleCase(String text){
        String[] words = text.split(" ");
        List<String> wordList = new LinkedList<>();
        for (String str : words) {
            wordList.add(str.substring(0, 1).toUpperCase()+ str.substring(1).toLowerCase());
        }
        String textTitleCase = String.join(" ", wordList);
        return textTitleCase;
    }
    
}
