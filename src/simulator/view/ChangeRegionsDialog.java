package simulator.view;

import java.awt.Frame;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JDialog;
import javax.swing.table.DefaultTableModel;

import org.json.JSONObject;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

class ChangeRegionsDialog extends JDialog implements EcoSysObserver {
	 private DefaultComboBoxModel<String> _regionsModel;
	 private DefaultComboBoxModel<String> _fromRowModel;
	 private DefaultComboBoxModel<String> _toRowModel;
	 private DefaultComboBoxModel<String> _fromColModel;
	 private DefaultComboBoxModel<String> _toColModel;
	private DefaultTableModel _dataTableModel;
	 private Controller _ctrl;
	 private List<JSONObject> _regionsInfo;
	 private String[] _headers = { "Key", "Value", "Description" };
	 // 
	
	 ChangeRegionsDialog(Controller ctrl) {
	 super((Frame)null, true);
	 _ctrl = ctrl;
	 initGUI();
	 // TODO registrar this como observer;
	 }
	 
	 private void initGUI() {
	 }

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAdvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		// TODO Auto-generated method stub
		
	}
	 
}
