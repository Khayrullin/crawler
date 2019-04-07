package ru.kpfu.itis.util;

import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileUtils {
    public static final String BASE_PATH = "src/main/resources/";
    public static final String POSTS_PATH = BASE_PATH + "posts/";
    public static final String LEMMATIZED_PATH = BASE_PATH + "lemma/";
    public static final String INVERTED_INDEX = BASE_PATH + "inverdtedIndex.txt";
    public static final String TF_IDF = BASE_PATH + "tfidf/";
    public static final String IDF = BASE_PATH + "idf.json";

    public void writeDocument(Document document, String id) {
        createDirectories(POSTS_PATH);
        Path path = Paths.get(POSTS_PATH + id.replaceAll("[\\\\/:\"*?<>|]+", "_") +
                ".txt");
        try {
            String text = document.body().text();
//            String text = document.select("div > div.post__body.post__body_full > div").text();
            Pattern p = Pattern.compile("[a-zA-Z]{2,}");
            Matcher matcher = p.matcher(text);
            StringBuilder stringBuilder = new StringBuilder();
            while (matcher.find()) {
                stringBuilder.append(matcher.group()).append("\n");
            }
            byte[] textBytes = stringBuilder.toString().getBytes();
            Files.write(path, textBytes);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeFile(Path file, byte[] buff, OpenOption... options) {
        try {
            Files.write(file, buff, options);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeFile(Path file, byte[] buff) {

        try {
            Files.write(file, buff);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<Path> readAllDocuments() {
        return readAllDocuments(Paths.get(POSTS_PATH));
    }

    public List<Path> readAllDocuments(Path path) {
        try (Stream<Path> paths = Files.walk(path)) {
            return paths.filter(Files::isRegularFile).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }


    public void createDirectories(String path) {
        Path directory = Paths.get(path);
        if (!Files.exists(directory)) {
            try {
                Files.createDirectories(directory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void clearDirectory() {
        if (!Paths.get(BASE_PATH).toFile().exists()) return;
        try {
            Files.walk(Paths.get(BASE_PATH))
                    .filter(Files::isRegularFile)
                    .sorted(Comparator.reverseOrder())
                    .map(Path::toFile)
                    .forEach(File::delete);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
