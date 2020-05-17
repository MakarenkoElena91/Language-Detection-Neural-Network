
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
    private int vectorSize;
    private int kmerOneSize = 1;
    private int kmerSize = 3;
    private int kmerOneVectorLenght = 255;
    private int kmerTwoVectorLenght = 255;

    private int NUMBER_OF_LANGUAGES = 235;
    private double[] vector = new double[kmerOneVectorLenght + kmerTwoVectorLenght];
    private double[] langInd = new double[NUMBER_OF_LANGUAGES];
    NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
    DecimalFormat df = (DecimalFormat) nf;


    private Language lang;

    public VectorProcessor(int vectorSize, int kmerSize) {
        this.vectorSize = kmerOneVectorLenght + kmerTwoVectorLenght; // kmerOneSize + kmerTwoSize ...
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
        int languagSeparator = line.lastIndexOf('@');
        Language lang = Language.valueOf(line.substring(languagSeparator + 1).trim());
        String text = line.substring(0, languagSeparator).toLowerCase();

        if (text.length() < 10) return;

        // reset vector
        for (int i = 0; i < vector.length; i++) {
            vector[i] = 0;
        }

        text = text.replaceAll("\\p{P}", "").toUpperCase();//Any punctuation character


        Language[] langs = Language.values();

        for (int i = 0; i < text.length() - kmerOneSize; i++) {
            String kmer = text.substring(i, i + kmerOneSize);
            int hash = kmer.hashCode();
            int hashIndex = hash % kmerOneVectorLenght;
            vector[hashIndex]++;
        }

        // another kmer
        int kmerTwoStartIndex = 255;
        for (int i = 0; i < text.length() - kmerSize; i++) {
            String kmer = text.substring(i, i + kmerSize);
            int hash = kmer.hashCode();
            int hashIndex = hash % kmerTwoVectorLenght + kmerTwoStartIndex;
            vector[hashIndex]++;
        }

        vector = Utilities.normalize(vector, 0, 1);

        FileWriter fw = new FileWriter(csvOutputFile, true);
        BufferedWriter bw = new BufferedWriter(fw);
        for (int i = 0; i < vector.length; i++) {
            bw.write(df.format(vector[i]) + ", ");
        }
        // bw.write(languageToVectorString(lang));

        for (int i = 0; i < langs.length; i++) {

            if (lang.toString().equalsIgnoreCase(String.valueOf(langs[i]))) {
                langInd[i] = 1;
            }
            bw.write(langInd[i] + ", ");
            langInd[i] = 0;
        }
        bw.newLine();
        bw.close();
    }

//    private static String languageToVectorString(Language language){
//        var sj = new StringJoiner(", ");
//        // loop ,
//        sj.add("0.0");
//        return sj.toString();
//    }

    public static void main(String[] args) throws Exception {
        VectorProcessor vectorProcessor = new VectorProcessor(510, 3);
        vectorProcessor.go();
    }
}