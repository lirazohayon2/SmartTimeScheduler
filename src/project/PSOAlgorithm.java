package project;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PSOAlgorithm {

	public static final int MAX_ITERATIONS = 200; // Maximum number of iterations
	public static final int POPULATION_SIZE = 1000; // Population size
	public static final double C1 = 1.49445; // Cognitive learning factor
	public static final double C2 = 1.49445; // Social learning factor
	public static final double MAX_VELOCITY = 0.5; // Maximum velocity
	public static final double MIN_VELOCITY = -0.5; // Minimum velocity
	public static final double THRESHOLD_NEW_COURSE = 0.1; // threshold for new course

	// Define constant variables
	public static final int DAYS_PER_WEEK = 7;
	public static final int START_DAY_HOUR = 8;
	public static final int Course_CLASH_PENALTY = 100;
	public static final int HOURS_PER_DAY = 12;
	public static final int START_HOUR = 8;

	private List<Particle> particles; 
	private Solution bestSolution;
	

	public PSOAlgorithm(List<Course> courses) {
		particles = new ArrayList<Particle>();
		Particle particle;
		// Initialize the population with random solutions
		for (int i = 0; i < POPULATION_SIZE; i++) {
			
			particle = new Particle();
			particles.add(particle);
			
			//System.out.println("################ SOLUTION " + i + "################");
			//System.out.println(particle.getBestSolution());
		}
		//System.out.println("###########################");
	}

	public void run() {
		int iteration = 0;
		
		while (iteration < MAX_ITERATIONS) {
			// Update the velocity and position of each particle
			
//			int i = 1;
//			System.out.print("\nRound #" + iteration + ":\n");
			for (Particle particle : particles) {
//				System.out.print(i++ + "\n\tCurrent ");
//				particle.printPositionValues();

				particle.updateVelocity(getGlobalBestSolution());
				particle.updatePosition();

//				System.out.print("\tNext    ");
//				particle.printPositionValues();
			}
			// Update the global best solution
			updateGlobalBestSolution();
			iteration++;
		}
		// Assign the best solution to the schedule
		this.bestSolution = getGlobalBestSolution();
	}

	public Solution getGlobalBestSolution() {
		// Find the particle with the best fitness value
		Particle bestParticle = Collections.min(particles);
		return bestParticle.getBestSolution();
	}
	


	private void updateGlobalBestSolution() {
		// Find the particle with the best fitness value
		Particle bestParticle = Collections.min(particles);
		// Update the global best solution of all particles
		for (Particle particle : particles) {
			particle.updateGlobalBestSolution(bestParticle.getBestSolution());
		}
	}

	public Solution getBestSolution() {
		return bestSolution;
	}

	public List<Solution> getAllSolutions() {
		List<Solution> allSolutions = new ArrayList<Solution>();
		for(Particle par : this.particles)
			allSolutions.add(par.getBestSolution());
		return allSolutions;
			
	}
}
