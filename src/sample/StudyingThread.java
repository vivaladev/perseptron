package sample;

import java.util.List;

public class StudyingThread implements Runnable {
    private NeuralNetwork neuralNetwork;
    private List<Char> chars;

    public StudyingThread(NeuralNetwork neuralNetwork, List<Char> chars) {
        this.neuralNetwork = neuralNetwork;
        this.chars = chars;
    }

    @Override
    public void run() {
        neuralNetwork.study(chars);
        neuralNetwork.save();
    }
}
