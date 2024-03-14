package simulator.model;

import java.util.List;

public class SelectClosest implements SelectionStrategy {
    @Override
    public Animal select(Animal a, List<Animal> as) {
        if (as.isEmpty()) {
            return null;
        }
        Animal closer = as.get(0);
        double distance = a.get_position().distanceTo(closer.get_position());
        for(int i = 1; i < as.size(); i++) {
        	Animal aux = as.get(i);
        	if(a.get_position().distanceTo(aux.get_position()) < distance) {
        		closer = aux;
        		distance = a.get_position().distanceTo(aux.get_position());
        	}
        }
        return closer;
    }

}
