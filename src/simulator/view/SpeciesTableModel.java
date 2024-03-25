package simulator.view;

import java.util.List;
import java.util.Map;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.Animal;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;
import simulator.model.State;
import java.util.ArrayList;

class SpeciesTableModel extends AbstractTableModel implements EcoSysObserver {
	private Controller _ctrl;
	private List<Animal> _speciesList;
	private State[] _states;

	SpeciesTableModel(Controller ctrl) {
		_ctrl = ctrl;
		_ctrl.addObserver(this);
		_speciesList = new ArrayList<>();
		_states = State.values();
		_ctrl.addObserver(this);
	}

	@Override
	public int getRowCount() {
		// TODO Auto-generated method stub
		return _speciesList.size();
	}

	@Override
	public int getColumnCount() {
		// TODO Auto-generated method stub
		return _states.length + 1;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		Animal species = _speciesList.get(rowIndex);
		if (columnIndex == 0) {
			return species.get_genetic_code();
		} 
		else if (rowIndex == 0){
			return _states[columnIndex];
		}
		else {
			State state = _states[columnIndex - 1];
			return state;
		}
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		updateSpeciesList(animals);
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		updateSpeciesList(animals);
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		updateSpeciesList(animals);
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		// Not used in this table
	}

	@Override
	public void onAdvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		updateSpeciesList(animals);
	}

	private void updateSpeciesList(List<AnimalInfo> animals) {
		_speciesList.clear();
		for (AnimalInfo a : animals) {
			_speciesList.add((Animal) a);
		}
		fireTableDataChanged();
	}

}
