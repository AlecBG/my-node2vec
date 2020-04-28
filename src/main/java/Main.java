import org.apache.commons.cli.ParseException;

import java.io.IOException;
import java.nio.file.Path;
import java.util.logging.Logger;

public class Main {
    private static final Logger logger = Logger.getLogger(Main.class.getName());

    public static void main(String[] args) throws IOException, ParseException {
        CommandLineOptions clo = new CommandLineOptions(args);
        Graph graph = clo.getGraph();
        logger.info("Graph loaded:\n" + graph);
        NodeEmbeddingCalculator calculator =  clo.getCalculator(graph);
        logger.info("calculator initialised:\n" + calculator);
        calculator.doStochasticGradientDescent();
        calculator.printKeys();
        Path outputPath = clo.getOutputPath();
        calculator.writeKeys(outputPath);
    }
}
