package simulator.misc;

import java.util.Random;

public class Utils {

	// ANIMAL
	public final static double SPEED_MIN = 0.1;
	public final static double MAX_ENERGY = 100.0;
	public final static double MIN_ENERGY = 0.0;
	public final static double MAX_DESIRE = 100.0;
	public final static double MIN_DESIRE = 0.0;
	public final static double POS_MULTIPLIER = 60.0;
	public final static double SIGHT_RANGE_MIN = 0.2;
	public final static double BABY_SPEED_MIN = 0.2;

	// SHEEP
	public static final double INI_FIELD_VIEW = 40.0;
	public static final double INI_VELOCITY = 35.0;
	public static final double NORMAL_DIST = 8.0;
	public static final double NORMAL_MOVE = 0.007;
	public static final double NORMAL_ENERGY_REMOVAL = 20.0;
	public static final double NORMAL_DESIRE_ADD = 40.0;
	public static final double MIN_DESIRE_TO_MATE = 65.0;
	public static final double MOVE_DANGER_MULT = 2.0;
	public static final double DANGER_ENERGY_MULT = 1.2;
	public static final double MAX_AGE = 8.0;
	public static final double SHEEP_PREGNANCY_PROB = 0.9;

	// WOLF
	public static final double INI_FIELD_VIEW_WOLF = 50.0;
	public static final double INI_VELOCITY_WOLF = 60.0;
	public static final double MAX_AGE_WOLF = 14.0;
	public static final double NORMAL_ENERGY_REMOVAL_WOLF = 18.0;
	public static final double NORMAL_DESIRE_ADD_WOLF = 30.0;
	public static final double MIN_ENERGY_EAT = 50.0;
	public static final double MOVE_DANGER_MULT_WOLF = 3.0;
	public static final double ENERGY_FOR_MATING = 10.0;
	public static final double MATE_SPEED_MULT = 3.0;
	public static final double WOLF_PREGNANCY_PROB = 0.9;
	
	// REGIONS
	public static final double FOOD_MULTIPLIER = 60.0;
	public static final double FOOD_HERB_TAKEAWAY = 5.0;
	public static final double FOOD_HERB_MULTIPLIER = 2.0;

	public static final Random _rand = new Random();

	public static double constrain_value_in_range(double value, double min, double max) {
		value = value > max ? max : value;
		value = value < min ? min : value;
		return value;
	}

	public static double get_randomized_parameter(double value, double tolerance) {
		assert (tolerance > 0 && tolerance <= 1);
		double t = (_rand.nextDouble() - 0.5) * 2 * tolerance;
		return value * (1 + t);
	}

}
