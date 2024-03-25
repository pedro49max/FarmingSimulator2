package simulator.view;

import java.util.List;

import javax.swing.table.AbstractTableModel;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.Diet;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.Region;
import simulator.model.RegionInfo;

class RegionsTableModel extends AbstractTableModel implements EcoSysObserver {
    private Controller _ctrl;
    private List<Region> _regions;
    private Diet[] _diets;

    RegionsTableModel(Controller ctrl) {
        _ctrl = ctrl;
        _ctrl.addObserver(this);
        _regions = null; // Initialize as null
        _diets = Diet.values();
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

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
      return null;
    }

    @Override
    public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
        
        fireTableDataChanged();
    }

    @Override
    public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
        
        fireTableDataChanged();
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
        // Not used in this table
    }
}