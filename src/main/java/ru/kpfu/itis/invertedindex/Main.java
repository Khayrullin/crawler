package ru.kpfu.itis.invertedindex;

import ru.kpfu.itis.util.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.*;
import java.util.stream.Collectors;

import static ru.kpfu.itis.util.FileUtils.INVERTED_INDEX;
import static ru.kpfu.itis.util.FileUtils.LEMMATIZED_PATH;

public class Main {
    public static void main(String[] args) {
        Map<String, LinkedHashSet<Integer>> invertedInd = new HashMap<>();
        FileUtils fileUtils = new FileUtils();
        List<Path> lemmatizedFiles = fileUtils.readAllDocuments(Paths.get(LEMMATIZED_PATH));
        List<List<String>> documents = new ArrayList<>();

        for (Path lemmatizedFile : lemmatizedFiles) {
            try {
                String strid = lemmatizedFile.getFileName().toString().split("_")[0];
                Integer id = Integer.valueOf(strid);
                List<String> words = Files.readAllLines(lemmatizedFile);
                documents.add(words);
                for (String word : words) {
                    if (invertedInd.containsKey(word)) {
                        invertedInd.get(word).add(id);
                    } else {
                        invertedInd.put(word, new LinkedHashSet<>(Collections.singleton(id)));
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            Files.delete(Paths.get(INVERTED_INDEX));
        } catch (IOException e) {
            e.printStackTrace();
        }
        invertedInd.forEach((s, integers) -> {
            fileUtils.writeFile(Paths.get(FileUtils.BASE_PATH + "inverdtedIndex.txt"), (s + ":" + integers.stream()
                            .sorted()
                            .map(Object::toString).collect(Collectors.joining(",")) + "\n").getBytes(),
                    StandardOpenOption.APPEND, StandardOpenOption.CREATE);
        });
    }


}
