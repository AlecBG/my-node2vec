import org.apache.commons.cli.*;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

class CommandLineOptions {
    private final Options options = new Options();
    private final CommandLine commandline;

    CommandLineOptions(String[] args) throws ParseException {
        addOptions();
        CommandLineParser commandLineParser = new DefaultParser();
        commandline = commandLineParser.parse(options, args);
    }

    Graph getGraph() throws IOException  {
        String graphFileName = commandline.getOptionValue("graph");
        double returnParam = Double.parseDouble(commandline.getOptionValue("return-param"));
        double inOutParam = Double.parseDouble(commandline.getOptionValue("in-out-param"));
        return Graph.loadFromFile(graphFileName, returnParam, inOutParam);
    }

    NodeEmbeddingCalculator getCalculator(Graph graph) throws ParseException {
        int nWalks = Integer.parseInt(commandline.getOptionValue("n-walks"));
        checkNWalks(nWalks);
        int walkLength = Integer.parseInt(commandline.getOptionValue("walk-length"));
        checkWalkLength(walkLength);
        int nNegativeSamples = Integer.parseInt(commandline.getOptionValue("n-negative-samples"));
        int dimension = Integer.parseInt(commandline.getOptionValue("dimension"));
        double exponent = Double.parseDouble(commandline.getOptionValue("exponent"));
        double learningRate = Double.parseDouble(commandline.getOptionValue("learn-rate"));
        return new NodeEmbeddingCalculator(graph, nWalks, walkLength, nNegativeSamples, exponent, learningRate, dimension);
    }

    Path getOutputPath() {
        return Paths.get(commandline.getOptionValue("output"));
    }

    private void checkWalkLength(int walkLength) throws ParseException {
        if (walkLength <= 0) {
            throw new ParseException("Walk length must be strictly positive.");
        }
    }

    private void checkNWalks(int nWalks) throws ParseException {
        if (nWalks <= 0) {
            throw new ParseException("Number of walks must be strictly positive.");
        }
    }

    private void addOptions() {
        options.addOption(createNWalkOption());
        options.addOption(createNNegativeSamplesOption());
        options.addOption(createWalkLengthOption());
        options.addOption(createGraphOption());
        options.addOption(createWeightingExponentOption());
        options.addOption(createLearningRateOption());
        options.addOption(createReturnParamOption());
        options.addOption(createInOutParamOption());
        options.addOption(createOutputParamOption());
        options.addOption(createDimensionOption());
    }

    private Option createWeightingExponentOption() {
        return Option.builder("we")
                .argName("weighting exponent")
                .desc("Exponent of unigram distribution for sampling.")
                .numberOfArgs(1)
                .longOpt("exponent")
                .required(true)
                .build();
    }

    private Option createLearningRateOption() {
        return Option.builder("lr")
                .argName("learning rate")
                .desc("The learning rate.")
                .numberOfArgs(1)
                .longOpt("learn-rate")
                .required(true)
                .build();
    }

    private Option createNNegativeSamplesOption() {
        return Option.builder("nn")
                .argName("nNegative")
                .desc("Number of negative samples.")
                .numberOfArgs(1)
                .longOpt("n-negative-samples")
                .required(true)
                .build();
    }

    private Option createNWalkOption() {
        return Option.builder("nw")
                .argName("nWalks")
                .desc("Number of random walks.")
                .numberOfArgs(1)
                .longOpt("n-walks")
                .required(true)
                .build();
    }

    private Option createWalkLengthOption() {
        return Option.builder("wl")
                .argName("walk length")
                .desc("Maximum umber of steps taken in a random walk.")
                .numberOfArgs(1)
                .longOpt("walk-length")
                .required(true)
                .build();
    }

    private Option createGraphOption() {
        return Option.builder("g")
                .argName("graph filename")
                .desc("Graph filename.")
                .numberOfArgs(1)
                .longOpt("graph")
                .required(true)
                .build();
    }

    private Option createDimensionOption() {
        return Option.builder("d")
                .argName("embedding dimension")
                .desc("The dimension of the embedding vector.")
                .numberOfArgs(1)
                .longOpt("dimension")
                .required(true)
                .build();
    }

    private Option createReturnParamOption() {
        return Option.builder("p")
                .argName("return parameter")
                .desc("The return parameter. The larger this is, the less likely we are to return to nodes.")
                .numberOfArgs(1)
                .longOpt("return-param")
                .required(true)
                .build();
    }

    private Option createInOutParamOption() {
        return Option.builder("q")
                .argName("In-out parameter")
                .desc("The in-out parameter. The larger this is, the more local the walks will be.")
                .numberOfArgs(1)
                .longOpt("in-out-param")
                .required(true)
                .build();
    }

    private Option createOutputParamOption() {
        return Option.builder("o")
                .argName("output filename")
                .desc("Learnt vectors written to this output file.")
                .numberOfArgs(1)
                .longOpt("output")
                .required(true)
                .build();
    }
}
