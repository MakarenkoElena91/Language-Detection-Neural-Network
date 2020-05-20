package ie.gmit.sw;

import org.encog.Encog;
import org.encog.neural.networks.BasicNetwork;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;

public class Menu {
    private int selection;
    private Scanner scanner = new Scanner(System.in);
    private boolean keepRunning = true;
    private int ngramSize;
    private int vectorSize;
    NeuralNetwork neuralNetwork = new NeuralNetwork();

    public int menu() throws Exception {
        while (keepRunning) {

            /***************************************************/

            System.out.println("Choose from the following:");
            System.out.println("-------------------------\n");
            System.out.println("1 - Choose file");
            System.out.println("2 - Enter text");
            System.out.println("3 - Train network");
            System.out.println("4 - Quit");

            selection = scanner.nextInt();

            switch (selection) {
                case 1:
                    System.out.println("Enter file name");
                    File file = new File(scanner.next());
                    Test test = new Test(file);
                    double[] vector = test.go();

                    BasicNetwork network = Utilities.loadNeuralNetwork("test.nn");
                    neuralNetwork.test(network, vector);
                    Encog.getInstance().shutdown();
                    break;
                case 2:
                    System.out.println("Enter text");
                    Scanner s = new Scanner(System.in);
                    String text = s.next();
                    File fileName = new File("saved.txt");
                    BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
                    writer.write(text);
                    writer.close();
                    test = new Test(fileName);
                    vector = test.go();

                    BasicNetwork mnetwork = Utilities.loadNeuralNetwork("test.nn");
                    neuralNetwork.test(mnetwork, vector);
                    Encog.getInstance().shutdown();
                    break;
                case 3:
                    System.out.println("Training starts...");
                    neuralNetwork.run();
                    Encog.getInstance().shutdown();
                    break;
                case 4:
                    keepRunning = false;
                    break;
                default:
                    System.out.println(" The user input an unexpected choice.");
            }
        }
        return selection;
    }
}
