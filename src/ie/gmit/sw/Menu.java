package ie.gmit.sw;

import java.util.Scanner;

public class Menu {
    private  int selection;
    private Scanner scanner = new Scanner(System.in);
    private boolean keepRunning = true;
    private int input;
    private VectorProcessor vectorProcessor = new VectorProcessor();

    public int menu() throws Exception {
        while (keepRunning) {

            /***************************************************/

            System.out.println("Choose from these choices");
            System.out.println("-------------------------\n");
            System.out.println("1 - Enter n-Gram Number(s)");
            System.out.println("2 - Enter Vector Size");
            System.out.println("3 - Enter ...");
            System.out.println("4 - Quit");

            selection = scanner.nextInt();

            switch (selection) {
                case 1:
                    System.out.println("Enter n-Gram Number(s)");
                    input = scanner.nextInt();
                    System.out.println("ngram " + input);

                   // vectorProcessor.go();
                    break;
                case 2:
                    System.out.println("Enter Vector Size");
                    input = scanner.nextInt();
                    System.out.println("Vector Size " + input);
                    break;
                case 3:
                    System.out.println("Enter..");
                    input = scanner.nextInt();
                    System.out.println("V3 " + input);
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
