package ru.kpfu.itis.invertedindex;

import ru.kpfu.itis.util.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;

import static ru.kpfu.itis.util.FileUtils.LEMMATIZED_PATH;
import static ru.kpfu.itis.util.FileUtils.TF_IDF;

public class TfIdf {

    public static void main(String[] args) {
        Integer documnetNumber = 0;
        HashSet<String> lemmatizedWords = new HashSet<>();

//        HashMap<String, List<Integer>> words = new HashMap<>();
//
//        FileUtils fileUtils = new FileUtils();
//        try {
//            Stream<String> invertedFileLines = Files.lines(Paths.get(INVERTED_INDEX));
//            invertedFileLines.forEach(s -> {
//                int sepInd = s.indexOf(":");
//                String word = s.substring(0, sepInd);
//                List<Integer> docNumbers = Arrays.stream(s.substring(sepInd + 1).split(",")).map(Integer::valueOf)
//                        .collect(Collectors.toList());
//                words.put(word, docNumbers);
//            });
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        for (Map.Entry<String, List<Integer>> stringListEntry : words.entrySet()) {
//            for (Integer integer : stringListEntry.getValue()) {
//                if (integer > documnetNumber) {
//                    documnetNumber = integer;
//                }
//            }
//        }




        FileUtils fileUtils = new FileUtils();
        fileUtils.createDirectories(TF_IDF);
        List<Path> lemmatizedFiles = fileUtils.readAllDocuments(Paths.get(LEMMATIZED_PATH));
        lemmatizedFiles.sort((o1, o2) -> {
            int n1 = Integer.parseInt(o1.getFileName().toString().split("_")[0]);
            int n2 = Integer.parseInt(o2.getFileName().toString().split("_")[0]);
            return n1 - n2;
        });
        List<List<String>> documents = new ArrayList<>();
        for (Path lemmatizedFile : lemmatizedFiles) {
            try {
                String strid = lemmatizedFile.getFileName().toString().split("_")[0];
                int id = Integer.parseInt(strid);
                List<String> words = Files.readAllLines(lemmatizedFile);
                documents.add(words);
                lemmatizedWords.addAll(words);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        TfIdf tfIdf = new TfIdf();
        lemmatizedWords.forEach(s -> {
            StringBuilder sb = new StringBuilder();
            int i = 1;
            for (List<String> doc : documents) {
                sb.append(i++).append(" - ");
                double v = tfIdf.tfIdf(doc, documents, s);
                DecimalFormat decimalFormat = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
                decimalFormat.setMaximumFractionDigits(20);
                sb.append(decimalFormat.format(v)).append("\n");
            }

            try {
                Files.write(Paths.get(TF_IDF + s + ".txt"), sb.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });



//
//        TfIdf tfIdf = new TfIdf();
//        double v = tfIdf.tfIdf(doc, documents, word);
//        System.out.println(word);
//        System.out.println(v);
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
