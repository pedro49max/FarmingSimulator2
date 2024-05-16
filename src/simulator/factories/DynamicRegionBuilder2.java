package simulator.factories;

import org.json.JSONObject;

import simulator.model.DynamicSupplyRegion;
import simulator.model.Region;

public class DynamicRegionBuilder2  extends Builder<Region> {
    public DynamicRegionBuilder2() {
        super("dynamic2", "Dynamic Supply Region Builder");
    }
    
    @Override 
    protected void fill_in_data(JSONObject o) {
    	o.put("power", "power yes");
        o.put("source", "eating");
    }

    @Override
    protected Region create_instance(JSONObject data) {
        double factor = data.optDouble("factor", 2.0);
        double food = data.optDouble("food", 1000.0);	
        return new DynamicSupplyRegion(factor, food);
    }

}
