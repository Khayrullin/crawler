package ru.kpfu.itis.lemm;

import ru.kpfu.itis.util.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        Lemmatizer lemmatizer = new Lemmatizer();
        FileUtils fileUtils = new FileUtils();
        fileUtils.createDirectories(FileUtils.LEMMATIZED_PATH);
        List<Path> paths = fileUtils.readAllDocuments();

        for (Path path : paths) {
            fileUtils.writeFile(Paths.get(FileUtils.LEMMATIZED_PATH + path.getFileName()),
                    lemmatizer.lemmatize(new String(Files.readAllBytes(path))).replaceAll(" ", "\n").getBytes());
        }


    }
}
