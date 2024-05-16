package simulator.view;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.Animal;
import simulator.model.AnimalInfo;
import simulator.model.Diet;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.MapInfo.RegionData;
import simulator.model.Region;
import simulator.model.RegionInfo;
import simulator.model.State;

class RegionsTableModel extends AbstractTableModel implements EcoSysObserver {
	private Controller _ctrl;
	private Diet[] _diets;
	private int numRows, numCols;
	private ArrayList<RegionData> _regionsA;

	public RegionsTableModel(Controller ctrl) {
		_ctrl = ctrl;
		_regionsA = new ArrayList<RegionData>(); // Initialize as null
		_diets = Diet.values();
		_ctrl.addObserver(this);
		//MapInfo info = ctrl.getSim().get_map_info();
		numRows = 1;
		numCols = 1;
	}

	@Override
	public int getRowCount() {
		return _regionsA != null ? _regionsA.size() : 0;
	}

	@Override
	public int getColumnCount() {
		// Number of columns: Row, Column, Description, Number of animals per diet
		return 3 + _diets.length;
	}

	private int countAnimalsWithDiet(List<AnimalInfo> animals, Diet diet) {
		int count = 0;
		for (AnimalInfo animal : animals) {
			if (animal.get_diet() == diet && animal.get_state() != State.DEAD) {
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
		
		int rowI = 0;
		if (rowIndex < getRowCount()) {
			rowI = rowIndex;
		}
		// To be checked
		switch (columnIndex) {
		case 0:
			if (numCols > 0)
				return rowI < numCols ? 0 : rowI / numCols;
		case 1:
			return rowI < numCols ? rowI : rowI % numCols;
		case 2:
			return _regionsA.get(rowI).getR().toString();
		default:
			if (columnIndex - 3 < _diets.length) {
				if (!_regionsA.isEmpty()) {
					RegionData r = _regionsA.get(rowI);
				
				return countAnimalsWithDiet(r.r().getAnimalsInfo(), _diets[columnIndex - 3]);
				}
			}
			else
				break;
			
		}

		return "";
	}

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		numCols = map.get_cols();
		numRows = map.get_rows();
		updateRegionsTable(map);
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		if (!_regionsA.isEmpty())
			_regionsA.clear();
		//_ctrl.reset(map.get_cols(), map.get_rows(), map.get_width(), map.get_height());
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		// Not used in this table
		
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		// Not used in this table
		numCols = map.get_cols();
		numRows = map.get_rows();
		updateRegionsTable(map);
	}

	@Override
	public void onAdvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		numCols = map.get_cols();
		numRows = map.get_rows();
		updateRegionsTable(map);
		
	}
	
	private void updateRegionsTable(MapInfo map) {
		if (!_regionsA.isEmpty())
			_regionsA.clear();
		Iterator<RegionData> it = map.iterator();
		while (it.hasNext()) {
			_regionsA.add(it.next());
			//System.out.println(r2.toString() + " " + r2.getAnimalsInfo().size());
			fireTableDataChanged();	
		}
		
	}
}