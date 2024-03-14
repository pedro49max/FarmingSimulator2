package simulator.model;

import java.util.List;
import java.util.function.Predicate;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class Sheep extends Animal{
	private Animal danger_source;
	private SelectionStrategy danger_strategy;
	
	public Sheep(SelectionStrategy mate_strategy, SelectionStrategy danger_strategy, Vector2D pos) {
		super("sheep", Diet.HERBIVORE, 40.0, 35.0, mate_strategy, pos);
		this.danger_strategy = danger_strategy;
	}
	protected Sheep(Sheep p1, Animal p2) {
		super(p1, p2);
		this.danger_strategy = p1.danger_strategy;
		this.danger_source = null;
	}
	public void update(double dt) {
		//System.out.println(this.state.toString());//debbuging
		if(this.state == State.DEAD)
			return;
		else if(this.state == State.NORMAL) {
			if(this.pos.dot(dest) < 8)
				this.dest = Vector2D.get_random_vectorXY(0, region_mngr.get_width()-1, 0, region_mngr.get_height()-1);
			this.move(speed*dt*Math.exp((energy - 100.0)*0.007));
			this.age += dt;
			this.energy -= 20*dt;
			if(energy < 0)
				energy = 0;
			else if(energy > 100)
				energy = 100;
			this.desire += 40*dt;
			if(desire < 0)
				desire = 0;
			else if(desire > 100)
				desire = 100;
			if(danger_source == null) {			
				Predicate<Animal> filter = animal -> animal.get_diet().equals(Diet.CARNIVORE) && (!(animal.get_state() == State.DEAD));
				List<Animal> animals = this.region_mngr.get_animals_in_range(this, filter);
				this.danger_source = this.danger_strategy.select(this, animals);
			}
			if(danger_source != null) {
				this.state = State.DANGER;
			}
			else if(danger_source == null&& this.desire > 65)
				this.state = State.MATE;
				
		}
		else if(this.state == State.DANGER) {
			if(danger_source != null && danger_source.get_state() == State.DEAD)
				danger_source = null;
			if(danger_source == null) {
				if(this.pos.dot(dest) < 8)
					this.dest = Vector2D.get_random_vectorXY(0, region_mngr.get_width()-1, 0, region_mngr.get_height()-1);
				this.move(speed*dt*Math.exp((energy - 100.0)*0.007));
				this.age += dt;
				this.energy -= 20*dt;
				if(energy < 0)
					energy = 0;
				else if(energy > 100)
					energy = 100;
				this.desire += 40*dt;
				if(desire < 0)
					desire = 0;
				else if(desire > 100)
					desire = 100;
			}
			else {
				this.dest = this.pos.plus(pos.minus(danger_source.get_position()).direction());
				this.move(2*speed*dt*Math.exp((energy - 100.0)*0.007));
				this.age += dt;
				this.energy -= 1.2*20*dt;
				if(energy < 0)
					energy = 0;
				else if(energy > 100)
					energy = 100;
				this.desire += 40*dt;
				if(desire < 0)
					desire = 0;
				else if(desire > 100)
					desire = 100;
			}
			//Changing STATE
			if(danger_source == null || pos.distanceTo(danger_source.get_position()) > this.sight_range) {
				Predicate<Animal> filter = animal -> animal.get_diet().equals(Diet.CARNIVORE) && (!(animal.get_state() == State.DEAD));
				List<Animal> animals = this.region_mngr.get_animals_in_range(this, filter);
				this.danger_source = this.danger_strategy.select(this, animals);
				if(danger_source == null) {
					if(this.desire < 65)
						this.state = State.NORMAL;
					else
						this.state = State.MATE;
				}				
			}
		}
		else if(this.state == State.MATE) {
			if(mate_target != null && (mate_target.get_state() == State.DEAD || this.pos.dot(mate_target.pos) > this.sight_range))
				mate_target = null;
			if(mate_target == null) {
				Predicate<Animal> filter = animal -> animal.get_diet().equals(Diet.HERBIVORE) && (!(animal.get_state() == State.DEAD));
				List<Animal> animals = this.region_mngr.get_animals_in_range(this, filter);
				this.mate_target = this.mate_strategy.select(this, animals);
			}
			if(mate_target != null) {
				this.dest = mate_target.get_position();
				this.move(2*speed*dt*Math.exp((energy - 100.0)*0.007));
				this.age += dt;
				this.energy -= 1.2*20*dt;
				if(energy < 0)
					energy = 0;
				else if(energy > 100)
					energy = 100;
				this.desire += 40*dt;
				if(desire < 0)
					desire = 0;
				else if(desire > 100)
					desire = 100;
				if(this.pos.distanceTo(mate_target.get_position()) < 8) {
					this.desire = 0;
					mate_target.desire = 0;
					if(this.baby == null) 
						if(Utils._rand.nextDouble(9) != 7)
							this.baby = new Sheep(this, mate_target);
					mate_target = null;
				}
			}
			else {
				if(this.pos.dot(dest) < 8)
					this.dest = Vector2D.get_random_vectorXY(0, region_mngr.get_width()-1, 0, region_mngr.get_height()-1);
				this.move(speed*dt*Math.exp((energy - 100.0)*0.007));
				this.age += dt;
				this.energy -= 20*dt;
				if(energy < 0)
					energy = 0;
				else if(energy > 100)
					energy = 100;
				this.desire += 40*dt;
				if(desire < 0)
					desire = 0;
				else if(desire > 100)
					desire = 100;
			}
			//Change of STATE
			if(danger_source == null) {
				Predicate<Animal> filter = animal -> animal.get_diet().equals(Diet.CARNIVORE) && (!(animal.get_state() == State.DEAD));
				List<Animal> animals = this.region_mngr.get_animals_in_range(this, filter);
				this.danger_source = this.danger_strategy.select(this, animals);
			}
			if(danger_source != null)
				this.state = State.DANGER;
			else if(danger_source == null && desire < 65)
				this.state = State.NORMAL;
		}
		if(this.pos.getX() >= region_mngr.get_width()) {
			this.pos = new Vector2D(region_mngr.get_width() - 1, this.pos.getY());
			this.state = State.NORMAL;
		}
		if(this.pos.getY() >= region_mngr.get_width()) {
			this.pos = new Vector2D(this.pos.getX(), region_mngr.get_height() - 1);
			this.state = State.NORMAL;
		}
		if(this.energy == 0.0 || this.age > 8.0) {
			this.state = State.DEAD;
		}
		if(this.state != State.DEAD) {
			this.energy += region_mngr.get_food(this, dt);
			if(energy < 0)
				energy = 0;
			else if(energy > 100)
				energy = 100;
		}
	}
	@Override
	public State get_state() {
		return this.state;
	}
	@Override
	public Vector2D get_position() {
		return this.pos;
	}
	@Override
	public String get_genetic_code() {
		return this.genetic_code;
	}
	@Override
	public Diet get_diet() {
		return this.diet;
	}
	@Override
	public double get_speed() {
		return this.speed;
	}
	@Override
	public double get_sight_range() {
		return this.sight_range;
	}
	@Override
	public double get_energy() {
		return this.energy;
	}
	@Override
	public double get_age() {
		return this.age;
	}
	@Override
	public Vector2D get_destination() {
		return this.dest;
	}
	@Override
	public boolean is_pregnant() {
		boolean pregnant = true;
		if(this.baby == null)
			pregnant = false;
		return pregnant;
	}
	public SelectionStrategy getSecondStrategy() {
		return this.danger_strategy;
	}
}
