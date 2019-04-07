package ru.kpfu.itis.lemm;

import ru.kpfu.itis.util.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) throws IOException {
        Lemmatizer lemmatizer = new Lemmatizer();
        FileUtils fileUtils = new FileUtils();
        fileUtils.createDirectories(FileUtils.LEMMATIZED_PATH);
        List<Path> paths = fileUtils.readAllDocuments();
        List<String> stopwords = Arrays.asList("i", "me", "my", "myself", "we", "our", "ours", "ourselves", "you", "your", "yours", "yourself", "yourselves", "he", "him", "his", "himself", "she", "her", "hers", "herself", "it", "its", "itself", "they", "them", "their", "theirs", "themselves", "what", "which", "who", "whom", "this", "that", "these", "those", "am", "is", "are", "was", "were", "be", "been", "being", "have", "has", "had", "having", "do", "does", "did", "doing", "a", "an", "the", "and", "but", "if", "or", "because", "as", "until", "while", "of", "at", "by", "for", "with", "about", "against", "between", "into", "through", "during", "before", "after", "above", "below", "to", "from", "up", "down", "in", "out", "on", "off", "over", "under", "again", "further", "then", "once", "here", "there", "when", "where", "why", "how", "all", "any", "both", "each", "few", "more", "most", "other", "some", "such", "no", "nor", "not", "only", "own", "same", "so", "than", "too", "very", "s", "t", "can", "will", "just", "don", "should", "now");

        for (Path path : paths) {
            String[] data = lemmatizer.lemmatize(new String(Files.readAllBytes(path))).split(" ");
            List<String> strings = Arrays.stream(data).map(String::toLowerCase).collect(Collectors.toList());
            strings.removeIf(s -> stopwords.stream().anyMatch(s1 -> s1.equalsIgnoreCase(s)));
//            strings.removeAll(stopwords);
//                    .replaceAll(" ", "\n").getBytes());
            Files.write(Paths.get(FileUtils.LEMMATIZED_PATH + path.getFileName()), strings);
        }


    }
}
