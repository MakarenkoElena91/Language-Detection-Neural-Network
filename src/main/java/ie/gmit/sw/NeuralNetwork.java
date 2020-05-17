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
import org.encog.util.arrayutil.TemporalWindowArray;
import org.encog.util.csv.CSVFormat;
import org.encog.util.simple.EncogUtility;

public class NeuralNetwork {
    private static final int inputs = 510;
    private static final int outputs = 235;
    int hiddenLayerNodes = inputs/4;
    final double MAX_ERROR = 0.0017;

    public NeuralNetwork() {}

    public MLDataSet generateTraining() {
        DataSetCODEC dsc = new CSVDataCODEC(new File("data.csv"), CSVFormat.DECIMAL_POINT,
                false, inputs, outputs, false);
        MemoryDataLoader mdl = new MemoryDataLoader(dsc);
        MLDataSet trainingSet = mdl.external2Memory();
        return  trainingSet;
    }

    public BasicNetwork createNetwork() {
        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(null, true, inputs));
        network.addLayer(new BasicLayer(new ActivationReLU(), true, hiddenLayerNodes, 400));
        network.addLayer(new BasicLayer(new ActivationSoftMax(), false, outputs));
        network.getStructure().finalizeStructure();
        network.reset();
        return network;
    }

    public void train(BasicNetwork network, MLDataSet trainingSet) {
//        FoldedDataSet folded = new FoldedDataSet(trainingSet);
//        MLTrain train = new ResilientPropagation(network, folded);
//        CrossValidationKFold trainFolded = new CrossValidationKFold(train, 5);
        ResilientPropagation trainFolded = new ResilientPropagation(network, trainingSet);
        trainFolded.addStrategy(new RequiredImprovementStrategy(5));
        EncogUtility.trainToError(trainFolded, MAX_ERROR);

        Utilities.saveNeuralNetwork(network, "./test.nn");
        trainFolded.finishTraining();
    }
    public void evaluate(MLDataSet trainingSet){
        BasicNetwork loadedNetwork = Utilities.loadNeuralNetwork("./test.nn");
        double err = loadedNetwork.calculateError(trainingSet);
        System.out.println("Loaded network’s error is (should be same as above) : "+err);
        EncogUtility.evaluate(loadedNetwork, trainingSet);
    }
    public void predict(BasicNetwork network, MLDataSet trainingSet) {
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
    }
    public void run() {
        BasicNetwork network = createNetwork();
        MLDataSet training = generateTraining();
        train(network, training);
        predict(network, training);
    }

    public static void main(String[] args) {
        NeuralNetwork neuralNetwork = new NeuralNetwork();
        for(int i = 0; i < 3;i++){
            neuralNetwork.run();
        }

        Encog.getInstance().shutdown();
    }
}