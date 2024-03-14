package simulator.factories;

import org.json.JSONArray;
import org.json.JSONObject;
import simulator.model.SelectionStrategy;
import simulator.model.Sheep;
import simulator.launcher.Main;
import simulator.misc.Vector2D;
import simulator.model.Animal;
import simulator.model.SelectFirst;

public class SheepBuilder extends Builder<Animal> {
	private Factory<SelectionStrategy> strategyFactory;
	
	public SheepBuilder(Factory<SelectionStrategy> strategyFactory) {
        super("sheep", "Sheep Builder");
        this.strategyFactory = strategyFactory;
    }

    @Override
    protected Animal create_instance(JSONObject data) {
        // Parsing genetic code, diet, and sight range


        // Parsing mate strategy
        SelectionStrategy mateStrategy = parseStrategy(data.optJSONObject("mate_strategy"));

        // Parsing danger strategy
        SelectionStrategy dangerStrategy = parseStrategy(data.optJSONObject("danger_strategy"));

        // Parsing position
        JSONObject posObject = data.optJSONObject("pos");
        

        // Creating Sheep instance with provided attributes
        return new Sheep( mateStrategy, dangerStrategy, this.parsePosition(posObject));
    }

    // Helper method to parse a SelectionStrategy from JSON
    private SelectionStrategy parseStrategy(JSONObject strategyJson) {
    	if (strategyJson == null) {
            // Default to SelectFirstStrategy if no strategy is provided
            return new SelectFirst();
        } else {
            // Create the strategy using the factory
            String strategyType = strategyJson.getString("type");
            JSONObject strategyData = strategyJson.optJSONObject("data");
            return strategyFactory.create_instance(new JSONObject().put("type", strategyType).put("data", strategyData));
        }
    }
    private Vector2D parsePosition(JSONObject posObject) {
    	if(posObject == null) {
    		Vector2D position = Vector2D.get_random_vectorXY(0, Main.width - 1, 0, Main.height - 1);
            return position;
    	}
    	JSONArray xRangeArray = posObject.optJSONArray("x_range");
        JSONArray yRangeArray = posObject.optJSONArray("y_range");
        
        if (xRangeArray == null || yRangeArray == null || xRangeArray.length() != 2 || yRangeArray.length() != 2) {
            throw new IllegalArgumentException("Invalid position range format");
        }

        double xPosMin = xRangeArray.getDouble(0);
        double xPosMax = xRangeArray.getDouble(1);
        double yPosMin = yRangeArray.getDouble(0);
        double yPosMax = yRangeArray.getDouble(1);

        Vector2D position = Vector2D.get_random_vectorXY(xPosMin, xPosMax, yPosMin, yPosMax);
        return position;
    }

}
