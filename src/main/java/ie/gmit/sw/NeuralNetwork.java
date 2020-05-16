package ie.gmit.sw;

import java.io.File;
import org.encog.Encog;
import org.encog.engine.network.activation.ActivationReLU;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationSoftMax;
import org.encog.engine.network.activation.ActivationTANH;
import org.encog.ml.data.MLData;
import org.encog.ml.data.MLDataPair;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.buffer.MemoryDataLoader;
import org.encog.ml.data.buffer.codec.CSVDataCODEC;
import org.encog.ml.data.buffer.codec.DataSetCODEC;
import org.encog.ml.data.folded.FoldedDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.ml.train.strategy.RequiredImprovementStrategy;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.cross.CrossValidationKFold;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.csv.CSVFormat;
import org.encog.util.simple.EncogUtility;

public class NeuralNetwork {
    public NeuralNetwork() {
        int inputs = 500;
        int outputs = 235;
        int hiddenLayerNodes = inputs/3;
        final double MAX_ERROR = 0.004;

        final BasicNetwork[] networks;
        //Configure the neural network topology.
        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(null, true, inputs));
        network.addLayer(new BasicLayer(new ActivationReLU(), true, hiddenLayerNodes));

        network.addLayer(new BasicLayer(new ActivationSoftMax(), false, outputs));
        network.getStructure().finalizeStructure();
        network.reset();

        //Read the CSV file "data.csv" into memory. Encog expects your CSV file to have input + output number of columns.
        DataSetCODEC dsc = new CSVDataCODEC(new File("data.csv"), CSVFormat.DECIMAL_POINT,
                false, inputs, outputs, false);
        MemoryDataLoader mdl = new MemoryDataLoader(dsc);
        MLDataSet trainingSet = mdl.external2Memory();


        FoldedDataSet folded = new FoldedDataSet(trainingSet);
        MLTrain train = new ResilientPropagation(network, folded);
        CrossValidationKFold trainFolded = new CrossValidationKFold(train, 5);
        //ResilientPropagation trainFolded = new ResilientPropagation(network, trainingSet);
        trainFolded.addStrategy(new RequiredImprovementStrategy(5));

        EncogUtility.trainToError(trainFolded, MAX_ERROR);

        Utilities.saveNeuralNetwork(network, "./test.nn");
        trainFolded.finishTraining();

        BasicNetwork loadedNetwork = Utilities.loadNeuralNetwork("./test.nn");
        MLDataSet training = mdl.external2Memory();
        double err = loadedNetwork.calculateError(training);
        //System.out.println("Loaded network’s error is (should be same as above) : "+err);
        EncogUtility.evaluate(loadedNetwork, training);


        //Test the NN
        double correct = 0;
        double total = 0;
        int number_of_languages = 0;
        int resultIndex = -1;
        int idealIndex = 0;

        for (MLDataPair pair : trainingSet) {
            total++;
            MLData output = network.compute(pair.getInput());
            MLData actual = output;
            MLData ideal = pair.getIdeal();
            double results[] = actual.getData();


            for (int i = 0; i < results.length; i++) {
                if (results[i] > 0 && (resultIndex == -1 || results[i] > results[resultIndex])) {
                    resultIndex = i;
                }
            }

            for (int i = 0; i < ideal.size(); i++){
                if(ideal.getData(i) == 1.0){
                    idealIndex = i;
                    if(idealIndex == resultIndex){
                        correct++;
                    }
                }
            }
//            System.out.println();
//            System.out.print(" ideal "  + idealIndex);
//            System.out.print(" result " + resultIndex);

            number_of_languages++;
//            System.out.println(" " + number_of_languages + " "
                    //+ EncogUtility.formatNeuralData(pair.getInput())
//                    + ", Actual=" + EncogUtility.formatNeuralData(output)
//                    + ", Ideal="
//                    + EncogUtility.formatNeuralData(pair.getIdeal()));
        }
        System.out.println("[INFO] Testing complete. Accuracy: " + (correct / total) * 100 + " %");
        Encog.getInstance().shutdown();
    }

    public static void main(String[] args) {
        for(int i = 0; i < 5; i++){
            new NeuralNetwork();
        }
    }
}