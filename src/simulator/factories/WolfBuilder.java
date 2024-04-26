package simulator.factories;

import org.json.JSONObject;
import simulator.model.Animal;
import simulator.model.SelectionStrategy;
import simulator.model.Wolf;

public class WolfBuilder extends AnimalBuilder {

	
	public WolfBuilder(Factory<SelectionStrategy> strategyFactory) {
        super("wolf", "Wolf Builder");
        this.strategyFactory = strategyFactory;
    }

    @Override
    protected Animal create_instance(JSONObject data) {
        // Parsing genetic code, diet, and sight range


        // Parsing mate strategy
        SelectionStrategy mateStrategy = parseStrategy(data.optJSONObject("mate_strategy"));

        // Parsing danger strategy
        SelectionStrategy hunting_strategy = parseStrategy(data.optJSONObject("hunger_strategy"));

        // Parsing position
        JSONObject posObject = data.optJSONObject("pos");

        // Creating Sheep instance with provided attributes
        return new Wolf( mateStrategy, hunting_strategy, this.parsePosition(posObject));
    }

    

}
