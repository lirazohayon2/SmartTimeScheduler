package project;
import java.util.Random;

public class Particle implements Comparable<Particle> {

	private Solution currentSolution;
	private Solution bestSolution;
	private Solution globalBestSolution;
	private double[] velocity;
	private Random random;

	public Particle() {
		currentSolution = new Solution();
		bestSolution = new Solution(currentSolution);
		globalBestSolution = new Solution(currentSolution);
		velocity = new double[Course.getAllCourses().size()];
		random = new Random();
	}

	public void updateVelocity(Solution globalBestSolution) {
		for (int i = 0; i < velocity.length; i++) {

			int currentGeneValue = currentSolution.getGenePosValue(i).getValue();
			int bestGeneValue = bestSolution.getGenePosValue(i).getValue();
			int globalBestGeneValue = globalBestSolution.getGenePosValue(i).getValue();

			// Calculate the cognitive and social components of the velocity
			double cognitiveComponent = PSOAlgorithm.C1 * random.nextDouble() * (bestGeneValue - currentGeneValue);
			double socialComponent = PSOAlgorithm.C2 * random.nextDouble() * (globalBestGeneValue - currentGeneValue);
			velocity[i] += cognitiveComponent + socialComponent;
			// Ensure that the velocity does not exceed the maximum or minimum values
			if (velocity[i] > PSOAlgorithm.MAX_VELOCITY) {
				velocity[i] = PSOAlgorithm.MAX_VELOCITY;
			} else if (velocity[i] < PSOAlgorithm.MIN_VELOCITY) {
				velocity[i] = PSOAlgorithm.MIN_VELOCITY;
			}
		}
//		System.out.print("Velocity: ");
//		for (int i = 0; i < velocity.length; i++) {
//			System.out.print(velocity[i] + " | ");
//		}
//		System.out.print("\n");
	}

	public void updatePosition() {
		for (int i = 0; i < Course.getAllCourses().size(); i++) {
			// Update the gene value based on the current velocity
			Solution.posValue currentGeneValue = currentSolution.getGenePosValue(i);
			Solution.posValue newValue = currentGeneValue;

			if (velocity[i]!=0) {
				if (velocity[i] > PSOAlgorithm.THRESHOLD_NEW_COURSE && 
						currentSolution.canAddCourse(i)) {
					newValue = Solution.posValue.TAKE_COURSE;
				} else {
					newValue = Solution.posValue.DONT_TAKE_COURSE;
				}
			}
			// Update the gene value in the current solution
			currentSolution.setGenePosValue(i, newValue);
		}
		// Update the personal best solution if the new solution is better
		if (currentSolution.getFitness() < bestSolution.getFitness()) {
			bestSolution = new Solution(currentSolution);
		}
	}

	public void updateGlobalBestSolution(Solution newGlobalBestSolution) {
		// Update the global best solution if the new solution is better
		if (newGlobalBestSolution.getFitness() < globalBestSolution.getFitness()) {
			globalBestSolution = new Solution(newGlobalBestSolution);
		}
	}

	public Solution getBestSolution() {
		return bestSolution;
	}

	public void printPosition() {
		System.out.println("Current position:");
		for (int i = 0; i <Course.getAllCourses().size(); i++) {
			Solution.posValue geneValue = currentSolution.getGenePosValue(i);
			System.out.println("Course " + i + ": " + geneValue.toString());
		}
		System.out.println("Fitness: " + currentSolution.getFitness());
	}

	public void printPositionValues() {
		System.out.print("position: ");
		for (int i = 0; i < Course.getAllCourses().size(); i++) {
			Solution.posValue geneValue = currentSolution.getGenePosValue(i);
			System.out.print(geneValue.getValue());
		}
		System.out.println(" (Fitness: " + currentSolution.getFitness() + ")");
	}

	@Override
	public int compareTo(Particle other) {
		// Compare particles based on the fitness of their personal best solutions
		return Double.compare(bestSolution.getFitness(), other.getBestSolution().getFitness());
	}
}