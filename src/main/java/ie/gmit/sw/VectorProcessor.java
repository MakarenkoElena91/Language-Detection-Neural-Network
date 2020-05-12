package ie.gmit.sw;

import java.io.*;
import java.text.DecimalFormat;
import java.io.FileWriter;
import java.text.NumberFormat;
import java.util.Locale;

public class VectorProcessor {
    private File file = new File("wili-2018-Small-11750-Edited.txt");
    private File csvOutputFile = new File("data.csv");
    private int kmerSize;
    private int vectorSize = 500;
    private int NUMBER_OF_LANGUAGES = 235;
    private double [] vector = new double[100];
    private double [] langInd = new double[NUMBER_OF_LANGUAGES];
    NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
    DecimalFormat df = (DecimalFormat)nf;


    private Language lang;

    public VectorProcessor(int vectorSize, int kmerSize) {
        this.vectorSize = vectorSize;
        this.kmerSize = kmerSize;
    }

    public void go() throws Exception{
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;

            while ((line = br.readLine()) != null) {
               process(line);
            }
        } catch (IOException e) {
            e.getMessage();
        }
    }

    public void process(String line) throws Exception{
        String[] record = line.trim().split("@");
        if (record.length > 2) return;

        String text = record[0].replaceAll("\\p{P}", "").toUpperCase();//Any punctuation character
        String lang = record[1];

        Language[] langs = Language.values();

        for (int i = 0; i <= vector.length - kmerSize; i++) {vector[i] = 0;
            String kmer = text.substring(i, i + kmerSize);
            vector[i] = kmer.hashCode() % vector.length;
            vector[i]++;
        }
        vector = Utilities.normalize(vector, 0, 1);

        FileWriter fw = new FileWriter(csvOutputFile, true);
        BufferedWriter bw = new BufferedWriter(fw);
        for(int i = 0; i < vector.length - kmerSize; i++){
            bw.write( df.format(vector[i]) + ", ");
        }

        for(int i = 0; i < langs.length; i++){
            if(lang.equalsIgnoreCase(String.valueOf(langs[i]))){
                langInd[i] = 1;
            }
            bw.write(langInd[i]  + ", ");
            langInd[i] = 0;

        }
        bw.newLine();
        bw.close();
    }

    public static void main(String[] args) throws Exception {
        VectorProcessor vectorProcessor = new VectorProcessor(100, 4);
        vectorProcessor.go();
    }
}
