package ie.gmit.sw;

import java.io.File;

import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.buffer.MemoryDataLoader;
import org.encog.ml.data.buffer.codec.CSVDataCODEC;
import org.encog.ml.data.buffer.codec.DataSetCODEC;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.util.csv.CSVFormat;

public class NeuralNetwork {

	/*
	 * *************************************************************************************
	 * NB: READ THE FOLLOWING CAREFULLY AFTER COMPLETING THE TWO LABS ON ENCOG AND REVIEWING
	 * THE LECTURES ON BACKPROPAGATION AND MULTI-LAYER NEURAL NETWORKS! YOUR SHOULD ALSO 
	 * RESTRUCTURE THIS CLASS AS IT IS ONLY INTENDED TO DEMO THE ESSENTIALS TO YOU. 
	 * *************************************************************************************
	 * 
	 * The following demonstrates how to configure an Encog Neural Network and train
	 * it using backpropagation from data read from a CSV file. The CSV file should
	 * be structured like a 2D array of doubles with input + output number of columns.
	 * Assuming that the NN has two input neurons and two output neurons, then the CSV file
	 * should be structured like the following:
	 *
	 *			-0.385,-0.231,0.0,1.0
	 *			-0.538,-0.538,1.0,0.0
	 *			-0.63,-0.259,1.0,0.0
	 *			-0.091,-0.636,0.0,1.0
	 * 
	 * The each row consists of four columns. The first two columns will map to the input
	 * neurons and the last two columns to the output neurons. In the above example, rows 
	 * 1 an 4 train the network with features to identify a category 2. Rows 2 and 3 contain
	 * features relating to category 1.
	 * 
	 * You can normalize the data using the Utils class either before or after writing to 
	 * or reading from the CSV file. 
	 */
	public NeuralNetwork() {
		int inputs = 2; //Change this to the number of input neurons
		int outputs =  235; //Change this to the number of output neurons
		int hiddenLayerNodes = 1;
		
		//Configure the neural network topology.
		BasicNetwork network = new BasicNetwork();
		network.addLayer(new BasicLayer(null, true, inputs)); //You need to figure out the activation function
		network.addLayer(new BasicLayer(new ActivationSigmoid(), true, hiddenLayerNodes)); //You need to figure out the activation function
		network.addLayer(new BasicLayer(new ActivationSigmoid(), false, outputs));
		network.getStructure().finalizeStructure();
		network.reset();

		//Read the CSV file "data.csv" into memory. Encog expects your CSV file to have input + output number of columns.
		DataSetCODEC dsc = new CSVDataCODEC(new File("data.csv"), CSVFormat.ENGLISH,
				false, inputs, outputs, false);
		MemoryDataLoader mdl = new MemoryDataLoader(dsc);
		MLDataSet trainingSet = mdl.external2Memory();

		//Use backpropagation training with alpha=0.1 and momentum=0.2
		Backpropagation trainer = new Backpropagation(network, trainingSet, 0.1, 0.2);

		//Train the neural network
		double minError = 0.01;
		int epoch = 1; //Use this to track the number of epochs
		System.out.println("[INFO] Training...");

		do {
			trainer.iteration(); 
			epoch++;
		} while(trainer.getError() > minError);
		trainer.finishTraining();
		System.out.println("[INFO] Training complete in " + epoch + " epochs with e= " + trainer.getError());

		//Test the NN
//		for(MLDataPair pair: trainingSet ) {
//			MLData output = network.compute(pair.getInput());
//			System.out.println(pair.getInput().getData(0) + ","
//					+ pair.getInput().getData(1)
//					+ ", Y=" + (int)Math.round(output.getData(0)) //Round the result
//					+ ", Yd=" + (int) pair.getIdeal().getData(0));
//		}
		Encog.getInstance().shutdown();
	}
}