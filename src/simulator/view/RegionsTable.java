package simulator.view;

import java.util.ArrayList;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.Animal;
import simulator.model.AnimalInfo;
import simulator.model.Diet;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.Region;
import simulator.model.RegionInfo;

class RegionsTableModel extends AbstractTableModel implements EcoSysObserver {
	private Controller _ctrl;
	private ArrayList<Region> _regions;
	private Diet[] _diets;
	private int numRows, numCols;

	RegionsTableModel(Controller ctrl) {
		_ctrl = ctrl;
		_regions = new ArrayList<Region>(); // Initialize as null
		_diets = Diet.values();
		_ctrl.addObserver(this);
		MapInfo info = ctrl.getSim().get_map_info();
		numRows = info.get_rows();
		numCols = info.get_cols();
	}

	@Override
	public int getRowCount() {
		return _regions != null ? _regions.size() : 0;
	}

	@Override
	public int getColumnCount() {
		// Number of columns: Row, Column, Description, Number of animals per diet
		return 3 + _diets.length;
	}

	private int countAnimalsWithDiet(List<AnimalInfo> animals, Diet diet) {
		int count = 0;
		for (AnimalInfo animal : animals) {
			if (animal.get_diet() == diet) {
				count++;
			}
		}
		return count;
	}
	
	@Override
    public String getColumnName(int column) {
        String result = "";
        
        switch (column) {
        	case 0:	
        	result = "Row";
        		break;
        	case 1:
        		result = "Col";
        		break;
        	case 2: 
        		result = "Desc";
        		break;
        	default:
        		if (column - 3 < _diets.length && column > 2)
        			result = _diets[column - 3].toString();
        		break;
        }
        
        return result;
    }


	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		// To be checked
		switch (columnIndex) {
		case 0:
			if (numCols > 0)
				return rowIndex < numCols ? 0 : rowIndex / numCols;
		case 1:
			return rowIndex < numCols ? rowIndex : rowIndex % numCols;
		case 2:
			return _regions.get(rowIndex).toString();
		default:
			if (columnIndex - 3 < _diets.length)
				return countAnimalsWithDiet(_regions.get(rowIndex).getAnimalsInfo(), _diets[columnIndex - 3]);
			
		}

		return "";
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		updateRegionsTable(map);
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		updateRegionsTable(map);
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		// Not used in this table
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		// Not used in this table
	}

	@Override
	public void onAdvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		updateRegionsTable(map);
		
	}
	
	private void updateRegionsTable(MapInfo map) {
		_regions.clear();
		for (Region[] r : map.getRegions()) {
			for (Region r2 : r) {
				_regions.add(r2);
			}
		}
		fireTableDataChanged();	
	}
}