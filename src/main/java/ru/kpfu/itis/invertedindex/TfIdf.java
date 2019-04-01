package ru.kpfu.itis.invertedindex;

import ru.kpfu.itis.util.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import static ru.kpfu.itis.util.FileUtils.LEMMATIZED_PATH;

public class TfIdf {

    public static void main(String[] args) {
        int documnetNumber = 95;
        String word = "Problem";

        FileUtils fileUtils = new FileUtils();
        List<Path> lemmatizedFiles = fileUtils.readAllDocuments(Paths.get(LEMMATIZED_PATH));
        List<List<String>> documents = new ArrayList<>();
        List<String> doc = new ArrayList<>();
        for (Path lemmatizedFile : lemmatizedFiles) {
            try {
                String strid = lemmatizedFile.getFileName().toString().split("_")[0];
                int id = Integer.parseInt(strid);
                List<String> words = Files.readAllLines(lemmatizedFile);
                if (id == documnetNumber) {
                    doc = words;
                }
                documents.add(words);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        TfIdf tfIdf = new TfIdf();
        double v = tfIdf.tfIdf(doc, documents, word);
        System.out.println(word);
        System.out.println(v);
    }

    public double tf(List<String> doc, String term) {
        double result = 0;
        for (String word : doc) {
            if (term.equalsIgnoreCase(word))
                result++;
        }
        return result / doc.size();
    }

    public double idf(List<List<String>> docs, String term) {
        double n = 0;
        for (List<String> doc : docs) {
            for (String word : doc) {
                if (term.equalsIgnoreCase(word)) {
                    n++;
                    break;
                }
            }
        }
        return Math.log(docs.size() / n);
    }

    public double tfIdf(List<String> doc, List<List<String>> docs, String term) {
        return tf(doc, term) * idf(docs, term);
    }
}
