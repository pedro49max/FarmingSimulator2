package simulator.model;

import java.util.List;
import java.util.function.Predicate;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class Wolf extends Animal{
	private Animal _hunt_target;
	private SelectionStrategy _hunting_strategy;
	
	public Wolf(SelectionStrategy mate_strategy, SelectionStrategy hunting_strategy, Vector2D _pos) {
		super("wolf", Diet.CARNIVORE, 50.0, 60.0, mate_strategy, _pos);
		_hunting_strategy = hunting_strategy;
	}
	protected Wolf(Wolf p1, Animal p2) {
		super(p1, p2);
		_hunting_strategy = p1._hunting_strategy;
		_hunt_target = null;
	}
	
	public void update(double dt) {
		if(_state == State.DEAD)
			return;
		else if(_state == State.NORMAL) {
			if(_pos.dot(_dest) < 8)
				_dest = Vector2D.get_random_vectorXY(0, _region_mngr.get_width()-1, 0, _region_mngr.get_height()-1);
			move(_speed*dt*Math.exp((_energy - 100.0)*0.007));
			_age += dt;
			_energy -= 18*dt;
			if(_energy < 0)
				_energy = 0;
			else if(_energy > 100)
				_energy = 100;
			_desire += 30*dt;
			if(_desire < 0)
				_desire = 0;
			else if(_desire > 100)
				_desire = 100;
			if(_energy < 50) {
				_state = State.HUNGER;
			}
			else if(_desire > 65)
				_state= State.MATE;				
		}
		else if(_state == State.HUNGER) {
			if(_hunt_target == null ||(_hunt_target != null && (_hunt_target.get_state() == State.DEAD || _pos.distanceTo(_hunt_target.get_position()) > _sight_range))) {
				Predicate<Animal> filter = animal -> animal.get_diet().equals(Diet.HERBIVORE) && (!(animal.get_state() == State.DEAD));
				List<Animal> animals = _region_mngr.get_animals_in_range(this, filter);
				_hunt_target = _hunting_strategy.select(this, animals);
			}
			if(_hunt_target == null) {
				if(_pos.dot(_dest) < 8)
					_dest = Vector2D.get_random_vectorXY(0, _region_mngr.get_width()-1, 0, _region_mngr.get_height()-1);
				move(_speed*dt*Math.exp((_energy - 100.0)*0.007));
				_age += dt;
				_energy -= 18*dt;
				if(_energy < 0)
					_energy = 0;
				else if(_energy > 100)
					_energy = 100;
				_desire += 30*dt;
				if(_desire < 0)
					_desire = 0;
				else if(_desire > 100)
					_desire = 100;
			}
			else {
				_dest = _hunt_target.get_position();
				move(_speed*3.0*dt*Math.exp((_energy - 100.0)*0.007));
				_age +=dt;
				_energy -= 18*1.2*dt;
				if(_energy < 0)
					_energy = 0;
				else if(_energy > 100)
					_energy = 100;
				_desire += 30*dt;
				if(_desire < 0)
					_desire = 0;
				else if(_desire > 100)
					_desire = 100;
				if(_pos.distanceTo(_hunt_target.get_position()) < 8) {
					if(_hunt_target._state == State.DEAD)
						System.out.println("lobo come oveja muerta");
					_hunt_target._state = State.DEAD;
					_hunt_target = null;
					_energy += 50;
					if(_energy < 0)
						_energy = 0;
					else if(_energy > 100)
						_energy = 100;
					if(_desire < 65)
						_state = State.NORMAL;
					else
						_state = State.MATE;
				}
			}
			if(_energy < 50)
				_state = State.HUNGER;
		}
		else if(_state == State.MATE) {
			if(_mate_target != null && _mate_target.get_state() == State.DEAD)
				_mate_target = null;
			if(_mate_target == null) {
				Predicate<Animal> filter = animal -> animal.get_diet().equals(Diet.CARNIVORE) && (!(animal.get_state() == State.DEAD));
				List<Animal> animals = _region_mngr.get_animals_in_range(this, filter);
				_mate_target = _mate_strategy.select(this, animals);
			}
			if(_mate_target != null) {
				_dest = _mate_target.get_position();
				move(3*_speed*dt*Math.exp((_energy - 100.0)*0.007));
				_age += dt;
				_energy -= 1.2*18*dt;
				if(_energy < 0)
					_energy = 0;
				else if(_energy > 100)
					_energy = 100;
				_desire += 30*dt;
				if(_desire < 0)
					_desire = 0;
				else if(_desire > 100)
					_desire = 100;
				if(_pos.distanceTo(_mate_target.get_position()) < 8) {
					_desire = 0;
					_mate_target._desire = 0;
					if(_baby == null) 
						if(Utils._rand.nextDouble(9) != 7)
							_baby = new Wolf(this, _mate_target);
					_mate_target = null;
				}
			}
			else {
				if(_pos.dot(_dest) < 8)
					_dest = Vector2D.get_random_vectorXY(0, _region_mngr.get_width()-1, 0, _region_mngr.get_height()-1);
				move(_speed*dt*Math.exp((_energy - 100.0)*0.007));
				_age += dt;
				_energy -= 18*dt;
				if(_energy < 0)
					_energy = 0;
				else if(_energy > 100)
					_energy = 100;
				_desire += 30*dt;
				if(_desire < 0)
					_desire = 0;
				else if(_desire > 100)
					_desire = 100;
			}
			if(_energy < 50)
				_state = State.HUNGER;
			else if(_desire < 65)
				_state = State.NORMAL;
		}
		if(_pos.getX() >= _region_mngr.get_width()) {
			_pos = new Vector2D(_region_mngr.get_width() - 1, _pos.getY());
			_state = State.NORMAL;
		}
		if(_pos.getY() >= _region_mngr.get_width()) {
			_pos = new Vector2D(_pos.getX(), _region_mngr.get_height() - 1);
			_state = State.NORMAL;
		}
		if(_energy == 0.0 || _age > 14.0)
			_state = State.DEAD;
		if(_state != State.DEAD) {
			_energy += _region_mngr.get_food(this, dt);
			if(_energy < 0)
				_energy = 0;
			else if(_energy > 100)
				_energy = 100;
		}
	}
	
	public SelectionStrategy getSecondStrategy() {
		return _hunting_strategy;
	}
}
