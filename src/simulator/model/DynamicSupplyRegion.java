package simulator.model;

import simulator.misc.Utils;

public class DynamicSupplyRegion extends Region{
	private double _food;
	private double _growthFactor;
	
	public DynamicSupplyRegion(double food, double growthFactor) {
		this._food = food;
		this._growthFactor = growthFactor;
	}
	public double get_food(Animal a, double dt) {
		double food_ = 0;
		if(a.get_diet() == Diet.HERBIVORE) {
			int n = 0;
			for(int i = 0; i < _animals.size();i++) {
				Animal animal = _animals.get(i);
				if(animal._diet == Diet.HERBIVORE)
					n++;
			}				
			food_ = 60.0*Math.exp(-Math.max(0,n - 5.0)*2.0)*dt;
		}
		if(food_ > _food)
			food_ = _food;
		_food -= food_;
		return food_;
	}
	public void update(double dt) {
		if(Utils._rand.nextDouble(1) != 0)
			this._food *= this._growthFactor;
	}
	@Override
	public String toString() {
		return "Dynamic Supply Region";
	}
}
