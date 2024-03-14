package simulator.model;

import org.json.JSONObject;

import simulator.launcher.Main;
import simulator.misc.Utils;
import simulator.misc.Vector2D;

public abstract class Animal implements Entity, Animalnfo{
	protected String genetic_code;
	protected Diet diet;
	protected State state;
	protected Vector2D pos;
	protected Vector2D dest;
	protected double energy;
	protected double speed;
	protected double age;
	protected double desire;
	protected double sight_range;
	protected Animal mate_target;
	protected Animal baby;
	protected AnimalMapView region_mngr;
	protected SelectionStrategy mate_strategy;
	
	protected Animal(String genetic_code, Diet diet, double sight_range, double init_speed, SelectionStrategy mate_strategy, Vector2D pos) {
		this.genetic_code = genetic_code;//needs to thrown exception for wrong values
		this.diet = diet;
		this.sight_range = sight_range;
		this.speed = Utils.get_randomized_parameter(init_speed, 0.1);
		this.mate_strategy = mate_strategy;
		this.pos = pos;
		this.state = State.NORMAL;
		this.energy = 100.0;
		this.desire = 0.0;
		this.dest = null;
		this.mate_target = null;
		this.baby = null;
		this.region_mngr = null;
		if(this.diet == Diet.CARNIVORE)
			this.age = Utils._rand.nextDouble(13);//Random number when initialize the program
		else
			this.age = Utils._rand.nextDouble(7);//Random number when initialize the program
	}
	
	protected Animal(Animal p1, Animal p2) {
		this.genetic_code = p1.get_genetic_code();
		this.diet = p1.get_diet();
		this.sight_range = Utils.get_randomized_parameter((p1.get_sight_range()+p2.get_sight_range())/2, 0.2);
		this.speed = Utils.get_randomized_parameter((p1.get_speed()+p2.get_speed())/2, 0.2);
		this.mate_strategy = p2.mate_strategy;
		this.pos = p1.get_position().plus(Vector2D.get_random_vector(-1,1).scale(60.0*(Utils._rand.nextGaussian()+1)));
		while(pos.getX() >= Main.width || pos.getY() >= Main.width) {
			this.pos = p1.get_position().plus(Vector2D.get_random_vector(-1,1).scale(60.0*(Utils._rand.nextGaussian()+1)));
			//System.out.println("baby animal out of the map");
		}
		this.state = State.NORMAL;
		this.energy = (p1.get_energy() + p2.get_energy())/2;
		this.desire = 0.0;
		this.dest = null;
		this.mate_target = null;
		this.baby = null;
		this.region_mngr = null;
		this.age = 0.0;
	}
	void init(AnimalMapView reg_mngr) {
		this.region_mngr = reg_mngr;
		if(pos == null)
			pos = Vector2D.get_random_vectorXY(0, region_mngr.get_width()-1, 0, region_mngr.get_height()-1);
		else {}
			//Adjusting pos
		dest = Vector2D.get_random_vectorXY(0, region_mngr.get_width()-1, 0, region_mngr.get_height()-1);
		
	}
	Animal deliver_baby() {
		Animal babyx;
		if(this.diet == Diet.CARNIVORE)
			babyx = new Wolf(this.baby.mate_strategy, this.baby.getSecondStrategy(), this.baby.get_position());
		else
			babyx = new Sheep(this.baby.mate_strategy, this.baby.getSecondStrategy(), this.baby.get_position());
		this.baby = null;
		return babyx;
	}
	protected void move(double speed) {
		this.pos = this.pos.plus(this.dest.minus(this.pos).direction().scale(speed));
	}
	public JSONObject as_JSON() {
		JSONObject json = new JSONObject();
        
        // Add position
        json.put("pos", new double[]{this.pos.getX(), this.pos.getY()});
        
        // Add genetic code
        json.put("gcode", this.genetic_code);
        
        // Add diet
        String diet = this.diet == Diet.CARNIVORE ? "CARNIVORE" : "HERBIVORE";
        json.put("diet", diet);
        
        // Add state
        json.put("state", this.state.toString());
        
        return json;
	}
	abstract SelectionStrategy getSecondStrategy();
}
