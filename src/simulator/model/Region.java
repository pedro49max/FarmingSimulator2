package simulator.model;

import java.util.List;
import java.util.ArrayList;
import org.json.JSONObject;
import org.json.JSONArray;

public abstract class Region implements Entity, FoodSupplier, RegionInfo{
	protected List<Animal> _animals;
	
	public Region() {
		_animals = new ArrayList<>();
	}
	final void add_animal(Animal a) {
		_animals.add(a);
	}
	final void remove_animal(Animal a) {
		int i = 0;
		boolean found = false;
		while(!found && i < _animals.size()) {
			if(_animals.get(i).equals(a))
				found = true;
			else
				i++;
		}
		_animals.remove(a);
	}
	final List<Animal> getAnimals(){
		final List<Animal> animalss;
		animalss = _animals;
		return animalss;
	}
	public abstract String toString();
	public JSONObject as_JSON() {//returns a JSON structure as follows where ai is what is returned by as_JSON() of the corresponding animal:
		JSONObject json = new JSONObject();
        JSONArray animalArray = new JSONArray();

        // Add animals to the JSON array
        for (Animal animal : _animals) {
            animalArray.put(animal.as_JSON());
        }

        // Add the JSON array of animals to the JSON object
        json.put("animals", animalArray);

        return json;
	}
	public List<AnimalInfo> getAnimalsInfo() {
		// can use Collections.unmodifiableList(_animals);
		// since Java 9, we can also use List.of() instead of unmodifiableList
		return new ArrayList<>(_animals);
	}
	
}
