package ie.gmit.sw;

import java.io.*;
import java.text.DecimalFormat;
import java.io.FileWriter;
import java.text.NumberFormat;
import java.util.Locale;
import java.util.StringJoiner;

public class VectorProcessor {
    private File file = new File("wili-2018-Small-11750-Edited.txt");
    private File csvOutputFile = new File("data.csv");
    private int kmerSize;
    private int vectorSize = 500;
    private int NUMBER_OF_LANGUAGES = 235;
    private double[] vector = new double[vectorSize];
    private double[] langInd = new double[NUMBER_OF_LANGUAGES];
    NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
    DecimalFormat df = (DecimalFormat) nf;


    private Language lang;

    public VectorProcessor(int vectorSize, int kmerSize) {
        this.vectorSize = vectorSize; // kmerOneSize + kmerTwoSize ...
        this.kmerSize = kmerSize;
    }

    public void go() throws Exception {
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;

            while ((line = br.readLine()) != null) {
                process(line);
            }
        } catch (IOException e) {
            e.getMessage();
        }
    }

    public void process(String line) throws Exception {
        var languagSeparator = line.lastIndexOf('@');
        var lang = Language.valueOf(line.substring(languagSeparator + 1).trim());
        var text = line.substring(0, languagSeparator).toLowerCase();

        if (text.length() < 10) return;

        // reset vector
        for(int i = 0; i < vector.length; i++){
            vector[i] = 0;
        }

        text = text.replaceAll("\\p{P}", "").toUpperCase();//Any punctuation character


        Language[] langs = Language.values();

        for (int i = 0; i < text.length() - kmerOneSize; i++) {
            String kmer = text.substring(i, i + kmerSize);
            var hash = kmer.hashCode();
            var hashIndex = hash % kmerOneVectorLenght +kmerOneStartIndex;
            vector[hashIndex]++;
        }

        // another kmer


        vector = Utilities.normalize(vector, 0, 1);

        FileWriter fw = new FileWriter(csvOutputFile, true);
        BufferedWriter bw = new BufferedWriter(fw);
        for (int i = 0; i < vector.length - kmerSize; i++) {
            bw.write(df.format(vector[i]) + ", ");
        }
        bw.write(languageToVectorString(lang));

//        for (int i = 0; i < langs.length; i++) {
//
//            if (lang.equalsIgnoreCase(String.valueOf(langs[i]))) {
//                langInd[i] = 1;
//            }
//            bw.write(langInd[i] + ", ");
//            langInd[i] = 0;
//        }
        bw.newLine();
        bw.close();

    }

    private static String languageToVectorString(Language language){
        var sj = new StringJoiner(", ");
        // loop ,
        sj.add("0.0");
        return sj.toString();
    }

    public static void main(String[] args) throws Exception {
        VectorProcessor vectorProcessor = new VectorProcessor(500, 4);
        vectorProcessor.go();
    }
}
