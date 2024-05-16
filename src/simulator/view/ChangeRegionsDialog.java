package simulator.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;

import java.awt.*;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import java.awt.event.*;
import simulator.control.Controller;
import simulator.launcher.Main;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;
import simulator.view.ViewUtils;

public class ChangeRegionsDialog extends JDialog implements EcoSysObserver {
	private DefaultComboBoxModel<String> _regionsModel;
	private DefaultComboBoxModel<String> _fromRowModel;
	private DefaultComboBoxModel<String> _toRowModel;
	private DefaultComboBoxModel<String> _fromColModel;
	private DefaultComboBoxModel<String> _toColModel;
	private DefaultTableModel _dataTableModel;
	private Controller _ctrl;
	private List<JSONObject> _regionsInfo;
	private String[] _headers = { "Key", "Value", "Description" };
	private int _status;

	ChangeRegionsDialog(Controller ctrl) {
		super((Frame) null, true);
		_ctrl = ctrl;
		initGUI();
		_ctrl.addObserver(this);
	}

	private void initGUI() {
		setTitle("Change Regions");
		JPanel mainPanel = new JPanel();
		mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
		setContentPane(mainPanel);

		JLabel helpText = new JLabel(
				"Select a region type, the rows/cols interval, and provide values for the paramters in the VALUE column (default values \n are used for parameters with no value).");
		mainPanel.add(helpText);

		_regionsInfo = Main.regions_factory.get_info();
		_regionsModel = new DefaultComboBoxModel<>();
		_fromRowModel = new DefaultComboBoxModel<>();
		_toRowModel = new DefaultComboBoxModel<>();
		_fromColModel = new DefaultComboBoxModel<>();
		_toColModel = new DefaultComboBoxModel<>();

		for (JSONObject region : _regionsInfo) {
			String regionName = region.optString("type");
			_regionsModel.addElement(regionName);
		}

		JComboBox<String> regionComboBox = new JComboBox<>(_regionsModel);

		regionComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Add additional rows for "food" and "factor" for "dynamic" region type
				String regionType = regionComboBox.getSelectedItem().toString();
				for (JSONObject o : _regionsInfo) {
					// System.out.println(o.toString());
					String s = o.get("type").toString();
					System.out.println(regionType);
					if (s.equals(regionType)) {
						removeAllRows(_dataTableModel);
						JSONObject data = o.getJSONObject("data");
						for (String key : data.keySet()) {
							String value = data.getString(key);
							_dataTableModel.addRow(new Object[] { key, "", value });
						}
						// break;
					}

				}

			}
		});

		JComboBox<String> rowComboBox = new JComboBox<>(_fromRowModel);
		JComboBox<String> toRowComboBox = new JComboBox<>(_toRowModel);
		JComboBox<String> colComboBox = new JComboBox<>(_fromColModel);
		JComboBox<String> toColComboBox = new JComboBox<>(_toColModel);

		_dataTableModel = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == 1; // Allow editing only in the second column
			}
		};
		_dataTableModel.setColumnIdentifiers(_headers);
		
		JTable dataTable = new JTable(_dataTableModel);
		
		TableColumn column = dataTable.getColumnModel().getColumn(2);
        column.setPreferredWidth(360);

		JScrollPane tableScrollPane = new JScrollPane(dataTable);
		mainPanel.add(tableScrollPane);

		// Create and add other components like comboboxes for row and column selection
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Close the dialog
				_status = 0;
				setVisible(false);
			}
		});

		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// Implement the logic for OK button
				// Retrieve selected region, row, column, and table data
				String regionType = regionComboBox.getSelectedItem().toString();

				// Retrieve selected row and column ranges from the combo boxes
				int fromRow = Integer.parseInt(rowComboBox.getSelectedItem().toString());
				int toRow = Integer.parseInt(toRowComboBox.getSelectedItem().toString());
				int fromCol = Integer.parseInt(colComboBox.getSelectedItem().toString());
				int toCol = Integer.parseInt(toColComboBox.getSelectedItem().toString());
				// Construct JSON data and pass it to _ctrl.set_regions()
				JSONObject regionData = new JSONObject();
				regionData.put("row", new JSONArray().put(fromRow).put(toRow));
				regionData.put("col", new JSONArray().put(fromCol).put(toCol));
				regionData.put("spec",
						new JSONObject().put("type", regionType).put("data", constructRegionData(_dataTableModel))); // Construct
																														// this
																														// method
																														// to
																														// get
																														// region
																														// data
				JSONObject regionsJSON = new JSONObject();
				regionsJSON.put("regions", new JSONArray().put(regionData));
				_ctrl.set_regions(regionsJSON);
				// Handle exceptions and close the dialog
				_status = 1;
				setVisible(false);
			}
		});

		JPanel buttonPanel = new JPanel();
		JPanel buttonP2 = new JPanel();
		buttonPanel.setPreferredSize(new Dimension(200, 100));
		JLabel regionLabel = new JLabel("Region Type: ");
		buttonPanel.add(regionLabel);
		buttonPanel.add(regionComboBox);
		JLabel rowLabel = new JLabel("Row from/to: ");
		buttonPanel.add(rowLabel);
		buttonPanel.add(rowComboBox);
		buttonPanel.add(toRowComboBox);
		JLabel columnLabel = new JLabel("Column from/to: ");
		buttonPanel.add(columnLabel);
		buttonPanel.add(colComboBox);
		buttonPanel.add(toColComboBox);

		// CANCEL AND OK
		buttonP2.add(cancelButton, BorderLayout.SOUTH);
		buttonP2.add(okButton, BorderLayout.SOUTH);
		mainPanel.add(buttonPanel);
		mainPanel.add(buttonP2);

		setPreferredSize(new Dimension(700, 400));
		pack();
		setResizable(false);
		setVisible(false);
	}

	private JSONObject constructRegionData(DefaultTableModel t) {

		JSONObject j = new JSONObject();

		for (int k = 0; k < t.getRowCount(); k++) {
			Object f = t.getValueAt(k, 1);
			try {
				j.put(t.getValueAt(k, 0).toString(), f);
				// System.out.println(t.getValueAt(k, 0).toString() + " " + f.toString());
			} catch (NumberFormatException e) {
				ViewUtils.showErrorMsg("Error parsing values: " + e.getMessage());
				return null; // or handle the error as needed
			}

		}

		if (j.isEmpty()) {
			j.put("{}", "");
		}

		return j;

	}

	private void removeAllRows(DefaultTableModel t) {
		int rowCount = t.getRowCount();
		for (int i = rowCount - 1; i >= 0; i--) {
			t.removeRow(i);
		}
	}

	public void open(Frame parent) {
		setLocationRelativeTo(parent);
		setVisible(true);
	}

	// These methods will update the comboboxes and table data

	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Auto-generated method stub
		for (int k = 0; k < map.get_rows(); k++) {
			String str = "" + k;
			_fromRowModel.addElement(str);
			_toRowModel.addElement(str);
		}

		for (int k = 0; k < map.get_cols(); k++) {
			String str = "" + k;
			_fromColModel.addElement(str);
			_toColModel.addElement(str);
		}
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Auto-generated method stub
		removeAllRows(_dataTableModel);

	}

	@Override
	public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
		// TODO Auto-generated method stub
		// Not used
	}

	@Override
	public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
		// TODO Auto-generated method stub

		_dataTableModel.fireTableDataChanged();
	}

	@Override
	public void onAdvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
		// TODO Auto-generated method stub

	}

}
