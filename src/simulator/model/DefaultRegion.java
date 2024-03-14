package simulator.model;

public class DefaultRegion extends Region{
	public double get_food(Animal a, double dt) {
		double food = 0;
		if(a.get_diet() == Diet.HERBIVORE) {
			int n = 0;
			for(int i = 0; i < animals.size();i++) {
				Animal animal = animals.get(i);
				if(animal.diet == Diet.HERBIVORE)
					n++;
			}				
			food = 60.0*Math.exp(-Math.max(0,n - 5.0)*2.0)*dt;
		}			
		return food;
	}
	public void update(double dt) {
		
	}
}
