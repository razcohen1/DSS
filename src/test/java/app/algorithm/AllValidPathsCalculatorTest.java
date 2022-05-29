package app.algorithm;

import app.files.JsonReader;
import app.model.ProblemInput;
import org.junit.Test;

public class AllValidPathsCalculatorTest {
    private JsonReader jsonReader = new JsonReader();
    private AllValidPathsCalculator calculator = new AllValidPathsCalculator();

    @Test
    public void name() {
        ProblemInput problemInput = jsonReader.read("C:\\Users\\Raz\\Desktop\\DssProject\\DSS Project\\Input examples\\input_example_1.json", ProblemInput.class);
        calculator.calculate(problemInput);
    }
}
