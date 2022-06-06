package app.files;

import app.model.ProblemInput;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class JsonReaderTest {
    private final String inputJsonFilepath = "src\\test\\resources\\input_example_1.json";
    private final long expectedInitialJunctionId = 57058937;
    private final int expectedAmountOfCars = 5;
    private final double expectedTimeAllowedForCarsItinerariesInSeconds = 2500;
    private final int expectedNumberOfJunctions = 132;
    private final int expectedNumberOfStreets = 291;
    private JsonReader jsonReader = new JsonReader();

    @Test
    public void readAndParseInputJsonExampleFileSuccessfullyTest() {
        ProblemInput problemInput = jsonReader.read(inputJsonFilepath, ProblemInput.class);
        assertThat(problemInput.getMissionProperties().getInitialJunctionId(), is(expectedInitialJunctionId));
        assertThat(problemInput.getMissionProperties().getAmountOfCars(), is(expectedAmountOfCars));
        assertThat(problemInput.getMissionProperties().getTimeAllowedForCarsItinerariesInSeconds(), is(expectedTimeAllowedForCarsItinerariesInSeconds));
        assertThat(problemInput.getJunctionsList().size(), is(expectedNumberOfJunctions));
        assertThat(problemInput.getStreetsList().size(), is(expectedNumberOfStreets));
    }
}
