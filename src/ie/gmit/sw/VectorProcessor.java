package ie.gmit.sw;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;

public class VectorProcessor {
    private double [] vector = new double[100];
    private DecimalFormat df = new DecimalFormat("###.###");
    private int n = 4;
    //private Language lang;

    public void go() throws Exception{
        try(BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(new File(""))))){
            String line = null;

            while(br.readLine() != null){
                process(line);
            }
        }catch(Exception e){
            e.getMessage();
        }
    }

    public void process(String line) throws Exception{
        String[] record = line.split("@");
        if(record.length > 2) return;

        String text = record[0].toUpperCase();
        String lang = record[1];

        for(int i = 0; i< vector.length; i++){
            //loop over text
            //compute hashcode
            //vector[index]++;
            Utilities.normalize(vector, -1, 1);
            //write out to csv file.. df.format(number)
        }
    }
}
