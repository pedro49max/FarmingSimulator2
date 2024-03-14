package simulator.model;

import java.util.List;

public class SelectFirst implements SelectionStrategy {
    @Override
    public Animal select(Animal a, List<Animal> as) {
       // System.out.println(as.size());//debbuging
        if (as.isEmpty()) {
            return null;
        }
        // Return the first animal in the list
        return as.get(0);
    }
}
