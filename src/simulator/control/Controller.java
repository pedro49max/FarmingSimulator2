package simulator.control;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import org.json.JSONObject;
import org.json.JSONArray;
import java.util.ArrayList;

import simulator.model.AnimalInfo;
import simulator.model.MapInfo;
import simulator.model.Simulator;
import simulator.view.SimpleObjectViewer;
import simulator.view.SimpleObjectViewer.ObjInfo;

public class Controller {
	private Simulator sim;
	
	public Controller(Simulator sim) {
		this.sim = sim;
	}
	public void load_data(JSONObject data) {
		// Check if regions are present
        if (data.has("regions")) {
            JSONArray regions = data.getJSONArray("regions");
            for (int i = 0; i < regions.length(); i++) {
                JSONObject regionObj = regions.getJSONObject(i);
                int rf = regionObj.getJSONArray("row").getInt(0);
                int rt = regionObj.getJSONArray("row").getInt(1);
                int cf = regionObj.getJSONArray("col").getInt(0);
                int ct = regionObj.getJSONArray("col").getInt(1);
                JSONObject spec = regionObj.getJSONObject("spec");
                for (int row = rf; row <= rt; row++) {
                    for (int col = cf; col <= ct; col++) {
                        this.sim.set_region(row, col, spec);
                    }
                }
            }
        }
        // Check if animals are present
        if (data.has("animals")) {
            JSONArray animals = data.getJSONArray("animals");
            for (int i = 0; i < animals.length(); i++) {
                JSONObject animalObj = animals.getJSONObject(i);
                int amount = animalObj.getInt("amount");
                JSONObject spec = animalObj.getJSONObject("spec");
                for (int j = 0; j < amount; j++) {
                    this.sim.add_animal(spec);
                }
            }
        }
	}
	public void run(double t, double dt, boolean sv, OutputStream out) {
		// Store the initial state
		SimpleObjectViewer view = null;
		if (sv) {
			MapInfo m = sim.get_map_info();
			view = new SimpleObjectViewer("[ECOSYSTEM]",m.get_width(), m.get_height(),m.get_cols(), m.get_rows());
			view.update(to_animals_info(sim.get_animals()), sim.get_time(), dt);
		}
	    //
	    JSONObject init_state = this.sim.as_JSON();

	    // Run the simulation until the specified time 't'
	    while (this.sim.get_time() < t) {
	        this.sim.advance(dt);
	        if (sv) view.update(to_animals_info(this.sim.get_animals()), this.sim.get_time(), dt);
	    }

	    // Store the final state
	    JSONObject final_state = this.sim.as_JSON();

	    // Construct the output JSON structure
	    JSONObject output = new JSONObject();
	    output.put("in", init_state);
	    output.put("out", final_state);

	    // Write the JSON structure to the output stream
	    try {
	        out.write(output.toString().getBytes());
	        out.flush();
	    } catch (IOException e) {
	        System.err.println("Error writing JSON output to the output stream: " + e.getMessage());
	        e.printStackTrace();
	    } finally {
	        if (sv) {
	            try {
	                out.close();
	                view.close();
	            } catch (IOException e) {
	                System.err.println("Error closing the output stream: " + e.getMessage());
	                e.printStackTrace();
	            }
	        }
	    }
	    if (sv) view.close();
	}
	private List<ObjInfo> to_animals_info(List<? extends AnimalInfo> animals) {
		List<ObjInfo> ol = new ArrayList<>(animals.size());
		for (AnimalInfo a : animals)
		ol.add(new ObjInfo(a.get_genetic_code(),
		(int) a.get_position().getX(),
		(int) a.get_position().getY(),(int)Math.round(a.get_age())+2));
		return ol;
		}
}
