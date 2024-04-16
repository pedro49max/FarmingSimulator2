package simulator.factories;

import org.json.JSONObject;

import simulator.model.DynamicSupplyRegion;
import simulator.model.Region;

public class DynamicSupplyRegionBuilder  extends Builder<Region> {
    public DynamicSupplyRegionBuilder() {
        super("dynamic", "Dynamic Supply Region Builder");
    }

    @Override
    protected Region create_instance(JSONObject data) {
        double factor = data.optDouble("factor", 2.0);
        double food = data.optDouble("food", 1000.0);	
        return new DynamicSupplyRegion(factor, food);
    }

}
