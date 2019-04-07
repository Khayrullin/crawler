package ru.kpfu.itis.invertedindex;

import com.fasterxml.jackson.databind.ObjectMapper;
import ru.kpfu.itis.util.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

import static ru.kpfu.itis.util.FileUtils.*;

public class TfIdfC {
    String q = "javascript";

    Idf idf = new Idf();
    double[][] matrix;

    public static void main(String[] args) {
        TfIdfC tfIdfC = new TfIdfC();
        try {
            tfIdfC.calculateTfIdf();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
        double log = Math.log(docs.size() / n);
        idf.getIdf().put(term, log);
        return log;
    }

    public double tfIdf(List<String> doc, List<List<String>> docs, String term) {
        return tf(doc, term) * idf(docs, term);
    }

    public void calculateTfIdf() throws IOException {
        Integer documnetNumber = 0;
        TreeSet<String> lemmatizedWords = new TreeSet<>();
        ObjectMapper objectMapper = new ObjectMapper();

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

        matrix = new double[lemmatizedFiles.size()][lemmatizedWords.size()];

        List<String> toSort = new ArrayList<>(lemmatizedWords);
        for (int i2 = 0; i2 < toSort.size(); i2++) {
            String s = toSort.get(i2);
            StringBuilder sb = new StringBuilder();
            int i = 1;
            for (int i1 = 0; i1 < documents.size(); i1++) {
                List<String> doc = documents.get(i1);
                sb.append(i++).append(" - ");
                double v = tfIdf(doc, documents, s);
                matrix[i1][i2] = v;
                DecimalFormat decimalFormat = new DecimalFormat("0", DecimalFormatSymbols.getInstance(Locale.ENGLISH));
                decimalFormat.setMaximumFractionDigits(20);
                sb.append(decimalFormat.format(v)).append("\n");
            }

            try {
                Files.write(Paths.get(TF_IDF + s + ".txt"), sb.toString().getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        Files.write(Paths.get(IDF), objectMapper.writeValueAsString(idf).getBytes());

        double[] docLength = new double[lemmatizedFiles.size()];
        for (int i = 0; i < matrix.length; i++) {
            double sum = 0;
            for (int k = 0; k < matrix[i].length; k++) {
                sum += Math.pow(matrix[i][k], 2);
            }
            docLength[i] = Math.sqrt(sum);
        }
        double[] query = new double[lemmatizedWords.size()];

        HashMap<String, Integer> map = new HashMap<>();
        Arrays.stream(q.split(" ")).forEach(s -> {
            if (map.containsKey(s)) {
                map.put(s, map.get(s) + 1);
            } else {
                map.put(s, 1);
            }
        });

        Integer value = map.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue)).get().getValue();
        map.entrySet().forEach(en -> {
            query[toSort.indexOf(en.getKey())] = en.getValue() / value * idf.getIdf().get(en.getKey());
        });

        double queryLength = Math.sqrt(Arrays.stream(query).map(o -> Math.pow(o, 2)).sum());
//        System.out.println(queryLength);

        for (int i = 0; i < matrix.length; i++) {
            double sum = 0.;
            for (int k = 0; k < matrix[i].length; k++) {
                sum += query[k] * matrix[i][k];
            }
            sum /= (docLength[i] * queryLength);
            System.out.println(sum);
        }

    }
}
