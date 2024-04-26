package simulator.model;

import java.util.List;
import java.util.function.Predicate;

import simulator.misc.Utils;
import simulator.misc.Vector2D;

public class Sheep extends Animal{
	private Animal _danger_source;
	private SelectionStrategy _danger_strategy;
	
	public Sheep(SelectionStrategy mate_strategy, SelectionStrategy danger_strategy, Vector2D pos) {
		super("sheep", Diet.HERBIVORE, 40.0, 35.0, mate_strategy, pos);
		_danger_strategy = danger_strategy;
	}
	protected Sheep(Sheep p1, Animal p2) {
		super(p1, p2);  
		_danger_strategy = p1._danger_strategy;
		_danger_source = null;
	}
	public void update(double dt) {
		//System.out.println(_state.toString());//debbuging
		if(_state == _state.DEAD)
			return;
		else if(_state == _state.NORMAL) {
			if(_pos.distanceTo(_dest) < 8)
				_dest = Vector2D.get_random_vectorXY(0, _region_mngr.get_width()-1, 0, _region_mngr.get_height()-1);
			move(_speed*dt*Math.exp((_energy - 100.0)*0.007));
			_age += dt;
			_energy -= 20*dt;
			if(_energy < 0)
				_energy = 0;
			else if(_energy > 100)
				_energy = 100;
			_desire += 40*dt;
			if(_desire < 0)
				_desire = 0;
			else if(_desire > 100)
				_desire = 100;
			if(_danger_source == null) {			
				Predicate<Animal> filter = animal -> animal.get_diet().equals(Diet.CARNIVORE) && (!(animal.get_state() == _state.DEAD));
				List<Animal> animals = _region_mngr.get_animals_in_range(this, filter);
				_danger_source = _danger_strategy.select(this, animals);
			}
			if(_danger_source != null) {
				_state = _state.DANGER;
			}
			else if(_danger_source == null&& _desire > 65)
				_state = _state.MATE;
				
		}
		else if(_state == _state.DANGER) {
			if(_danger_source != null && _danger_source.get_state() == _state.DEAD)
				_danger_source = null;
			if(_danger_source == null) {
				if(_pos.dot(_dest) < 8)
					_dest = Vector2D.get_random_vectorXY(0, _region_mngr.get_width()-1, 0, _region_mngr.get_height()-1);
				move(_speed*dt*Math.exp((_energy - 100.0)*0.007));
				_age += dt;
				_energy -= 20*dt;
				if(_energy < 0)
					_energy = 0;
				else if(_energy > 100)
					_energy = 100;
				_desire += 40*dt;
				if(_desire < 0)
					_desire = 0;
				else if(_desire > 100)
					_desire = 100;
			}
			else {
				_dest = _pos.plus(_pos.minus(_danger_source.get_position()).direction());
				move(2*_speed*dt*Math.exp((_energy - 100.0)*0.007));
				_age += dt;
				_energy -= 1.2*20*dt;
				if(_energy < 0)
					_energy = 0;
				else if(_energy > 100)
					_energy = 100;
				_desire += 40*dt;
				if(_desire < 0)
					_desire = 0;
				else if(_desire > 100)
					_desire = 100;
			}
			//Changing _state
			if(_danger_source == null || _pos.distanceTo(_danger_source.get_position()) > _sight_range) {
				Predicate<Animal> filter = animal -> animal.get_diet().equals(Diet.CARNIVORE) && (!(animal.get_state() == _state.DEAD));
				List<Animal> animals = _region_mngr.get_animals_in_range(this, filter);
				_danger_source = _danger_strategy.select(this, animals);
				if(_danger_source == null) {
					if(_desire < 65)
						_state = _state.NORMAL;
					else
						_state = _state.MATE;
				}				
			}
		}
		else if(_state == _state.MATE) {
			if(_mate_target != null && (_mate_target.get_state() == _state.DEAD || _pos.dot(_mate_target._pos) > _sight_range))
				_mate_target = null;
			if(_mate_target == null) {
				Predicate<Animal> filter = animal -> animal.get_diet().equals(Diet.HERBIVORE) && (!(animal.get_state() == _state.DEAD));
				List<Animal> animals = _region_mngr.get_animals_in_range(this, filter);
				_mate_target = _mate_strategy.select(this, animals);
			}
			if(_mate_target != null) {
				_dest = _mate_target.get_position();
				move(2*_speed*dt*Math.exp((_energy - 100.0)*0.007));
				_age += dt;
				_energy -= 1.2*20*dt;
				if(_energy < 0)
					_energy = 0;
				else if(_energy > 100)
					_energy = 100;
				_desire += 40*dt;
				if(_desire < 0)
					_desire = 0;
				else if(_desire > 100)
					_desire = 100;
				if(_pos.distanceTo(_mate_target.get_position()) < 8) {
					_desire = 0;
					_mate_target._desire = 0;
					if(_baby == null) 
						if(Utils._rand.nextDouble(9) != 7)
							_baby = new Sheep(this, _mate_target);
					_mate_target = null;
				}
			}
			else {
				if(_pos.dot(_dest) < 8)
					_dest = Vector2D.get_random_vectorXY(0, _region_mngr.get_width()-1, 0, _region_mngr.get_height()-1);
				move(_speed*dt*Math.exp((_energy - 100.0)*0.007));
				_age += dt;
				_energy -= 20*dt;
				if(_energy < 0)
					_energy = 0;
				else if(_energy > 100)
					_energy = 100;
				_desire += 40*dt;
				if(_desire < 0)
					_desire = 0;
				else if(_desire > 100)
					_desire = 100;
			}
			//Change of _state
			if(_danger_source == null) {
				Predicate<Animal> filter = animal -> animal.get_diet().equals(Diet.CARNIVORE) && (!(animal.get_state() == _state.DEAD));
				List<Animal> animals = _region_mngr.get_animals_in_range(this, filter);
				_danger_source = _danger_strategy.select(this, animals);
			}
			if(_danger_source != null)
				_state = _state.DANGER;
			else if(_danger_source == null && _desire < 65)
				_state = _state.NORMAL;
		}
		if(_pos.getX() >= _region_mngr.get_width()) {
			_pos = new Vector2D(_region_mngr.get_width() - 1, _pos.getY());
			_state = _state.NORMAL;
		}
		if(_pos.getY() >= _region_mngr.get_width()) {
			_pos = new Vector2D(_pos.getX(), _region_mngr.get_height() - 1);
			_state = _state.NORMAL;
		}
		if(_energy == 0.0 || _age > 8.0) {
			_state = _state.DEAD;
		}
		if(_state != _state.DEAD) {
			_energy += _region_mngr.get_food(this, dt);
			if(_energy < 0)
				_energy = 0;
			else if(_energy > 100)
				_energy = 100;
		}
	}

	public SelectionStrategy getSecondStrategy() {
		return _danger_strategy;
	}
}
