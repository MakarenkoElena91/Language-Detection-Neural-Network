package ie.gmit.sw;

import java.io.*;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Test {
    private int kmerOneSize = 1;
    private int kmerSize = 3;
    private int kmerOneVectorLenght = 255;
    private int kmerTwoVectorLenght = 255;
    private File file;
    NumberFormat nf = NumberFormat.getNumberInstance(Locale.ENGLISH);
    DecimalFormat df = (DecimalFormat) nf;

    public Test(File file) {
        this.file = file;
    }

    public double[]  go() throws Exception {
        double [] vector = new double[510];
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(file)))) {
            String line;

            while ((line = br.readLine()) != null) {

                // reset vector
                for (int i = 0; i < vector.length; i++) {
                    vector[i] = 0;
                }

                String text = line.replaceAll("\\p{P}", "").toUpperCase();//Any punctuation character

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
            }
        } catch (IOException e) {
            e.getMessage();
        }
        return vector;
    }
}
