
import experiment.Logger;
import experiment.Main2;
import experiment.exploration.IntegerExplorationPlusOne;
import experiment.explorer.CallExplorer;
import experiment.explorer.Explorer;
import experiment.explorer.RandomExplorer;
import main.Main;
import org.junit.After;
import org.junit.Test;
import processor.UtilPerturbation;
import quicksort.QuickSort;
import quicksort.QuickSortManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

public class MainTest {

    @After
    public void tearDown() {
        // required for upcoming tests
        UtilPerturbation.reinitPerturbableTypes();
    }

    @Test
    public void testTransformationWithAPI() throws Exception {
        // this is an example of using the API main.Main for transforming code
        // contract: the main transforms the quicksort
        Main main = new Main();
        main.addInputResource("src/main/java/quicksort/QuickSort.java");
        main.run();
        // main perturbation points have been added
        assertEquals(41, main.spoon.getFactory().Class().get(QuickSort.class).getFields().size());
        assertEquals("__L40", main.spoon.getFactory().Class().get(QuickSort.class).getFields().get(0).getSimpleName());

    }

    @Test
    public void testMain() throws Exception {
        // this is an example of performing a complete exploration of the perturbation space with PONE
        // contract: the main does not throw an exception and return the correct values

        experiment.Main2.main(new String[] {"-v", "-s", "quicksort.QuickSortManager", "-nb", "10", "-size", "10", "-exp", "call", "pone"});

        Logger result = Main2.lastResultOfMainCall;

		assertEquals("Numerical", Main2.exploration.getType());
		assertEquals(10, result.getNumberOfTasks());

		assertEquals(41, result.getNumberOfLocations());

        assertEquals(1, result.searchSpaceSizePerMagnitude.length);
		assertEquals(4331, result.searchSpaceSizePerMagnitude[0]);
		assertEquals(1, result.numberOfSuccessPerMagnitude.length);
		assertEquals(3323, result.numberOfSuccessPerMagnitude[0]);

		assertEquals(19, result.getAntifragilePoints().size());
		assertEquals("30\tend (/home/martin/martin-no-backup/jPerturb/src/main/java/quicksort/QuickSortInstr.java:32)\tNumerical", result.getAntifragilePoints().get(0).toString());
    }

	@Test
	public void testApi() throws Exception {
		// shows that the API usage works

		int nbTask = 10;
		int sizeTask = 10;
		Explorer expl = new CallExplorer(new QuickSortManager(nbTask, sizeTask), new IntegerExplorationPlusOne());
		Logger result = expl.run();

		assertEquals(10, result.getNumberOfTasks());

		assertEquals(41, result.getNumberOfLocations());

		assertEquals(1, result.searchSpaceSizePerMagnitude.length);
		assertEquals(4331, result.searchSpaceSizePerMagnitude[0]);
		assertEquals(1, result.numberOfSuccessPerMagnitude.length);
		assertEquals(3323, result.numberOfSuccessPerMagnitude[0]);

	}

	@Test
	public void testApiRandomExplorer() throws Exception {
		// contract: the random explorer does something....
		// with the seed, it is deterministic

		int nbTask = 5;
		int sizeTask = 12;
		int repeat = 5;
		QuickSortManager manager = new QuickSortManager(nbTask, sizeTask);
		float pertubProbability = 0.5f;
		RandomExplorer expl = new RandomExplorer(manager, new IntegerExplorationPlusOne(),
				repeat, // we repeat 50 times per task/input
				pertubProbability // probability of injection is 50%
		);
		Logger result = expl.run();

		assertEquals(nbTask*repeat*manager.getLocations().size(), expl.nbPerturbedExecution);
		assertEquals(nbTask, result.getNumberOfTasks());

		assertEquals(manager.getLocations().size(), result.getNumberOfLocations());

		assertEquals(261, result.getNbFailures());
		assertEquals(796, result.getNbSuccess());
		assertEquals(173, result.getNbExceptions());

		assertEquals(16515, result.getNbCalls());
		// approx the half this pertubProbability = 50%
		assertEquals(7124, result.getNbEnactions());

		// TODO: this fails, bug or incorrect understanding?
		//assertEquals(expl.nbPerturbedExecution, result.getNbFailures() + result.getNbSuccess() + result.getNbExceptions());
	}

}
