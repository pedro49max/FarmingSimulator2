package simulator.model;

import java.util.List;

public class SelectYoungest implements SelectionStrategy {
    @Override
    public Animal select(Animal a, List<Animal> as) {
        if (as.isEmpty()) {
            return null;
        }
        Animal younger = as.get(0);
        double age = younger.get_age();
        for(int i = 1; i < as.size(); i++) {
        	Animal aux = as.get(i);
        	if(aux.get_age() < age) {
        		younger = aux;
        		age = aux.get_age();
        	}
        }
        return younger;
    }

}
