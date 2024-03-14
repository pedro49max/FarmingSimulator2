package simulator.factories;

import org.json.JSONObject;
import simulator.model.SelectFirst;
import simulator.model.SelectionStrategy;

public class SelectFirstBuilder extends Builder<SelectionStrategy> {
    public SelectFirstBuilder() {
        super("first", "Select First Builder");
    }



	@Override
	protected SelectionStrategy create_instance(JSONObject data) {
	    return new SelectFirst(); // Always return an instance of SelectFirstStrategy
	}

}
