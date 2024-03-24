package simulator.model;

public class DefaultRegion extends Region{
	public double get_food(Animal a, double dt) {
		double food = 0;
		if(a.get_diet() == Diet.HERBIVORE) {
			int n = 0;
			for(int i = 0; i < _animals.size();i++) {
				Animal animal = _animals.get(i);
				if(animal._diet == Diet.HERBIVORE)
					n++;
			}				
			food = 60.0*Math.exp(-Math.max(0,n - 5.0)*2.0)*dt;
		}			
		return food;
	}
	public void update(double dt) {
		
	}
	@Override
	public String toString() {
		return "Default region";
	}
}
