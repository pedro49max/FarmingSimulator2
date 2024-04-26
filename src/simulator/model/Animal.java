package simulator.model;

import org.json.JSONObject;

import simulator.launcher.Main;
import simulator.misc.Utils;
import simulator.misc.Vector2D;

public abstract class Animal implements Entity, AnimalInfo{
	protected String _genetic_code;
	protected Diet _diet;
	protected State _state;
	protected Vector2D _pos;
	protected Vector2D _dest;
	protected double _energy;
	protected double _speed;
	protected double _age;
	protected double _desire;
	protected double _sight_range;
	protected Animal _mate_target;
	protected Animal _baby;
	protected AnimalMapView _region_mngr;
	protected SelectionStrategy _mate_strategy;
	
	protected Animal(String _genetic_code, Diet _diet, double _sight_range, double init__speed, SelectionStrategy _mate_strategy, Vector2D _pos) {
		this._genetic_code = _genetic_code;//needs to thrown exception for wrong values
		this._diet = _diet;
		this._sight_range = _sight_range;
		this._speed = Utils.get_randomized_parameter(init__speed, 0.1);
		this._mate_strategy = _mate_strategy;
		this._pos = _pos;
		this._state = _state.NORMAL;
		this._energy = 100.0;
		this._desire = 0.0;
		this._dest = null;
		this._mate_target = null;
		this._baby = null;
		this._region_mngr = null;
		this._age= 0;//apparently now everything starts at 0 age
	}
	
	protected Animal(Animal p1, Animal p2) {
		this._genetic_code = p1.get_genetic_code();
		this._diet = p1.get_diet();
		this._sight_range = Utils.get_randomized_parameter((p1.get_sight_range()+p2.get_sight_range())/2, 0.2);
		this._speed = Utils.get_randomized_parameter((p1.get_speed()+p2.get_speed())/2, 0.2);
		this._mate_strategy = p2._mate_strategy;
		this._pos = p1.get_position().plus(Vector2D.get_random_vector(-1,1).scale(60.0*(Utils._rand.nextGaussian()+1)));
		while(_pos.getX() >= Main.width || _pos.getY() >= Main.width) {
			this._pos = p1.get_position().plus(Vector2D.get_random_vector(-1,1).scale(60.0*(Utils._rand.nextGaussian()+1)));
			//System.out.println("_baby animal out of the map");
		}
		this._state = _state.NORMAL;
		this._energy = (p1.get_energy() + p2.get_energy())/2;
		this._desire = 0.0;
		this._dest = null;
		this._mate_target = null;
		this._baby = null;
		this._region_mngr = null;
		this._age = 0.0;
	}
	void init(AnimalMapView reg_mngr) {
		this._region_mngr = reg_mngr;
		if(_pos == null)
			_pos = Vector2D.get_random_vectorXY(0, _region_mngr.get_width()-1, 0, _region_mngr.get_height()-1);
		else {}
			//Adjusting _pos
		_dest = Vector2D.get_random_vectorXY(0, _region_mngr.get_width()-1, 0, _region_mngr.get_height()-1);
		
	}
	Animal deliver_baby() {
		Animal _babyx;
		if(this._diet == _diet.CARNIVORE)
			_babyx = new Wolf(this._baby._mate_strategy, this._baby.getSecondStrategy(), this._baby.get_position());
		else
			_babyx = new Sheep(this._baby._mate_strategy, this._baby.getSecondStrategy(), this._baby.get_position());
		this._baby = null;
		return _babyx;
	}
	protected void move(double _speed) {
		this._pos = this._pos.plus(this._dest.minus(this._pos).direction().scale(_speed));
	}
	public JSONObject as_JSON() {
		JSONObject json = new JSONObject();
        
        // Add _position
        json.put("_pos", new double[]{this._pos.getX(), this._pos.getY()});
        
        // Add genetic code
        json.put("gcode", this._genetic_code);
        
        // Add _diet
        String _diet = this._diet == Diet.CARNIVORE ? "CARNIVORE" : "HERBIVORE";
        json.put("_diet", _diet);
        
        // Add _state
        json.put("_state", this._state.toString());
        
        return json;
	}
	
	@Override
	public State get_state() {
		return this._state;
	}
	@Override
	public Vector2D get_position() {
		return this._pos;
	}
	@Override
	public String get_genetic_code() {
		return this._genetic_code;
	}
	@Override
	public Diet get_diet() {
		return this._diet;
	}
	@Override
	public double get_speed() {
		return this._speed;
	}
	@Override
	public double get_sight_range() {
		return this._sight_range;
	}
	@Override
	public double get_energy() {
		return this._energy;
	}
	@Override
	public double get_age() {
		return this._age;
	}
	@Override
	public Vector2D get_destination() {
		return this._dest;
	}
	@Override
	public boolean is_pregnant() {
		boolean pregnant = true;
		if(this._baby == null)
			pregnant = false;
		return pregnant;
	}
	
	abstract SelectionStrategy getSecondStrategy();
}
