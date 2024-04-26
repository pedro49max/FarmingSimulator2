package simulator.factories;

import org.json.JSONObject;
import simulator.model.SelectionStrategy;
import simulator.model.Sheep;
import simulator.model.Animal;


public class SheepBuilder extends AnimalBuilder {
	
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
}
