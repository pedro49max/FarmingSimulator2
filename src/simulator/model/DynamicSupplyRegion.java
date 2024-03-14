package simulator.model;

import simulator.misc.Utils;

public class DynamicSupplyRegion extends Region{
	private double food;
	private double growthFactor;
	
	public DynamicSupplyRegion(double food, double growthFactor) {
		this.food = food;
		this.growthFactor = growthFactor;
	}
	public double get_food(Animal a, double dt) {
		double food_ = 0;
		if(a.get_diet() == Diet.HERBIVORE) {
			int n = 0;
			for(int i = 0; i < animals.size();i++) {
				Animal animal = animals.get(i);
				if(animal.diet == Diet.HERBIVORE)
					n++;
			}				
			food_ = 60.0*Math.exp(-Math.max(0,n - 5.0)*2.0)*dt;
		}
		if(food_ > food)
			food_ = food;
		food -= food_;
		return food_;
	}
	public void update(double dt) {
		if(Utils._rand.nextDouble(1) != 0)
			this.food *= this.growthFactor;
	}
}
