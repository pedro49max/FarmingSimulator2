package simulator.view;

import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;
import simulator.model.State;
import java.util.ArrayList;
import java.util.HashMap;

class SpeciesTableModel extends AbstractTableModel implements EcoSysObserver {
    private Controller _ctrl;
    private Map<String, Map<State, Integer>> _speciesMap;
    private State[] _states;

    SpeciesTableModel(Controller ctrl) {
        _ctrl = ctrl;
        _speciesMap = new HashMap<>();
        _states = State.values();
        _ctrl.addObserver(this);
    }
    
    @Override
    public String getColumnName(int column) {
        String result = "";
        
        if (column == 0)
        	result = "Species";
        else if (column - 1 < _states.length)
        	result = _states[column - 1].toString();
       
        return result;
    }

    @Override
    public int getRowCount() {
        return _speciesMap.size(); // Each row represents a unique genetic code
    }

    @Override
    public int getColumnCount() {
        return _states.length + 1; // Each column represents a state
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        List<String> geneticCodes = new ArrayList<>(_speciesMap.keySet());
        String geneticCode = geneticCodes.get(rowIndex);

        if (columnIndex == 0) {
            return geneticCode;
        } else if (columnIndex > 0 && columnIndex <= _states.length){
            State state = _states[columnIndex - 1];
            return _speciesMap.get(geneticCode).getOrDefault(state,0);
            //return _speciesMap.getOrDefault(geneticCode, new HashMap<>()).getOrDefault(state, 0);
        }
        else
        	return "";
    }

    @Override
    public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
        updateSpeciesMap(animals);
    }

    @Override
    public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
    	if (!_speciesMap.isEmpty())
    		_speciesMap.clear();
    }

    @Override
    public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
        updateSpeciesMap(animals);
    }

    @Override
    public void onAdvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
        updateSpeciesMap(animals);
    }

    private void updateSpeciesMap(List<AnimalInfo> animals) {
        if (!_speciesMap.isEmpty()) {
            // Clear the existing counts
            _speciesMap.clear();
        }

        // Loop through the animals
        for (AnimalInfo animalInfo : animals) {
            String geneticCode = animalInfo.get_genetic_code();
            State state = animalInfo.get_state();
            
            // Retrieve the map for the genetic code
            Map<State, Integer> stateMap = _speciesMap.computeIfAbsent(geneticCode, k -> new HashMap<>());

            // Increment the count for the state in the state map
            int count = stateMap.getOrDefault(state, 0);
            stateMap.put(state, count + 1);
        }

        // Notify listeners that the table data has changed
        fireTableDataChanged();
    }


	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		// TODO Auto-generated method stub
		 updateSpeciesMap(r.getAnimalsInfo());
	}
}
