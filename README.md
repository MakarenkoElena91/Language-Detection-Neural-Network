# Language-Detection-Neural-Network
In data processing I used a vector of size 510. The first 255 indexes were used for storing 1-ngrams, the rest 
for storing 3-grams. I assume 1-grams helped to identify the alphabet (cyrilic, laten, etc) while 3grams
helped to identify the language within the same alphabet.
Activation function SoftMax outputs number between 0 and 1, that's why vector's values were normalized within that 
range. 
Increase of layers led to increase of time of the neural network to 'learn'. Moreover, the error rate rate
after dropping in the first couple of iterations started to increase. 
Dropout rate increased  the accuracy a bit but not much(≈2-5%).
The conclusion I drew for myself is that configuring the network itself (trying different activation functions, 
number of layers, etc) does not really help much (maybe just decimals of a procents), 
only data preparation is really what matters.
