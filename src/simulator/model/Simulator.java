package simulator.model;

import java.util.ArrayList;
import java.util.List;
import org.json.JSONObject;
import simulator.factories.Factory;

public class Simulator implements JSONable, Observable<EcoSysObserver>{
	private Factory<Animal> _animals_factory;
	private Factory<Region> _regions_factory;
	private RegionManager _region_mngr;
	private List<Animal> _animals;
	private List<EcoSysObserver> _observers;
	private double _time;
	
	public Simulator(int cols, int rows, int width, int height,Factory<Animal> animals_factory, Factory<Region> regions_factory) {
		this._animals_factory = animals_factory;
		this._regions_factory = regions_factory;
		this._region_mngr = new RegionManager(cols, rows, width, height);
		this._animals = new ArrayList<>();
		this._time = 0.0;
		_observers = new ArrayList<>();
	}
	public void reset(int cols, int rows, int width, int height) {
		this._region_mngr = new RegionManager(cols, rows, width, height);
		this._animals = new ArrayList<>();
		this._time = 0.0;
		//Notify all observers
		List<AnimalInfo> animals = new ArrayList<>(_animals);
		for (EcoSysObserver observer : _observers) {
			observer.onReset(this._time, this._region_mngr, animals);
		}
	}
	private void set_region(int row, int col, Region r) {
		this._region_mngr.set_region(row, col, r);
	}
	public void set_region(int row, int col, JSONObject r_json) {
		Region r = this._regions_factory.create_instance(r_json); // Create a region from the JSON representation
        set_region(row, col, r); // Call the private method to set the region.
        //Notify all observers
    	for (EcoSysObserver observer : _observers) {
      		observer.onRegionSet(row, col, this._region_mngr, r);
      	}
	}
	private void add_animal(Animal a) {
		//System.out.println("T");
		this._animals.add(a);
		this._region_mngr.register_animal(a);
		//Notify all observers
		AnimalInfo animal = a;
		List<AnimalInfo> animals = new ArrayList<>(_animals);
		for (EcoSysObserver observer : _observers) {
			observer.onAnimalAdded(this._time, this._region_mngr, animals, animal);
		}
	}
	public void add_animal(JSONObject a_json){
		Animal animal = _animals_factory.create_instance(a_json); // Create an animal from the JSON representation
        add_animal(animal); // Call the private method to add the animal to the simulation
    }
	
	public MapInfo get_map_info() {
		return this._region_mngr;
	}
	public List<? extends AnimalInfo> get_animals(){
		final List<Animal> copy = this._animals;
		return copy;
	}
	public double get_time() {
		return this._time;
	}
	public void advance(double dt) {
		this._time += dt;

		List<Animal> toKeep = new ArrayList<>();
		for (Animal a: _animals) {
			if (a.get_state() != State.DEAD) {
				toKeep.add(a);
				a.update(dt);
				_region_mngr.update_animal_region(a);
			}
			_region_mngr.unregister_animal(a);
		}
		//int counter = 0;
		
		
		for(int i = 0; i < this._animals.size(); i++) {
			Animal animal = _animals.get(i);
			Animal baby;
			if(animal.is_pregnant()) {
				baby = animal.deliver_baby(); 
				toKeep.add(baby);
				//this.add_animal(baby);
			}						
		}
		
		_animals = new ArrayList<>();
		
		for (Animal k : toKeep) {
			if (k.get_state() != State.DEAD) {
				//System.out.println(_animals.size());
				add_animal(k);
				//counter++;
			}
		}
		
		this._region_mngr.update_all_regions(dt);
		//System.out.println(_animals.size());
		
		List<AnimalInfo> animals = new ArrayList<>(_animals);
		for (EcoSysObserver observer : _observers) {
			observer.onAdvanced(this._time, this._region_mngr, animals, dt);
		}
	}
	
	@Override
	public JSONObject as_JSON() {
		 JSONObject json = new JSONObject();
	        // Add current time
	        json.put("time", _time);

	        // Add state of the region manager
	        json.put("state", _region_mngr.as_JSON());

	        return json;
	}
	@Override
    public void addObserver(EcoSysObserver o) {
        if (!_observers.contains(o)) {
            _observers.add(o);
            // Notify the observer upon registration
            o.onRegister(_time, _region_mngr, new ArrayList<>(_animals));
        }
    }
	 @Override
	    public void removeObserver(EcoSysObserver o) {
	        _observers.remove(o);
	    }
	/*Notice that there are two versions of the add_animal and set_region methods, one receives the input
in JSON format while the other receives the corresponding objects once they have been created. The ones
that receive the objects are private. The purpose of having the 2 versions is to facilitate development and
debugging practice: in your first implementation, before implementing the factories, change those methods
from private to public and use them directly to add animals and regions from outside. When you have
implemented the factories change them back to private again. This way you can debug the program
without having implemented the factories.*/
}
