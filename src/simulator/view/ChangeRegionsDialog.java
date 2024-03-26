package simulator.view;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
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

    ChangeRegionsDialog(Controller ctrl) {
        super((Frame)null, true);
        _ctrl = ctrl;
        initGUI();
        _ctrl.addObserver(this);
    }

    private void initGUI() {
        setTitle("Change Regions");
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        setContentPane(mainPanel);

        JLabel helpText = new JLabel("Select a region type, the rows/cols interval, and provide values for the paramters in the VALUE column (default values \n are used for parameters with no value).");
        mainPanel.add(helpText);

        _regionsInfo = Main.regions_factory.get_info();
        _regionsModel = new DefaultComboBoxModel<>();
        _fromRowModel = new DefaultComboBoxModel<>();
        _toRowModel = new DefaultComboBoxModel<>();
        _fromColModel = new DefaultComboBoxModel<>();
        _toColModel = new DefaultComboBoxModel<>();
        
        for (JSONObject region : _regionsInfo) {
            _regionsModel.addElement(region.optString("desc", "Unknown"));
        }
        
        for (int k = 0 ; k < _ctrl.getSim().get_map_info().get_rows(); k++) {
        	String str = "" + k;
        	_fromRowModel.addElement(str);
        	_toRowModel.addElement(str);
        }
        
        for (int k = 0; k < _ctrl.getSim().get_map_info().get_cols(); k++) {
        	String str = "" + k;
        	_fromColModel.addElement(str);
        	_toColModel.addElement(str);
        }
        
        JComboBox<String> regionComboBox = new JComboBox<>(_regionsModel);
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
        JScrollPane tableScrollPane = new JScrollPane(dataTable);
        mainPanel.add(tableScrollPane);
        
        
        // Create and add other components like comboboxes for row and column selection
        JButton cancelButton = new JButton("Cancel");
        cancelButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Close the dialog
                setVisible(false);
            }
        });
        
        JButton okButton = new JButton("OK");
        okButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                // Implement the logic for OK button
                // Retrieve selected region, row, column, and table data
                // Construct JSON data and pass it to _ctrl.set_regions()
                // Handle exceptions and close the dialog
            	setVisible(false);
            }
        });
        
        JPanel buttonPanel = new JPanel();
        buttonPanel.setPreferredSize(new Dimension(200, 200));
        buttonPanel.add(regionComboBox, BorderLayout.NORTH);
        buttonPanel.add(rowComboBox, BorderLayout.NORTH);
        buttonPanel.add(toRowComboBox, BorderLayout.NORTH);
        buttonPanel.add(colComboBox, BorderLayout.NORTH);
        buttonPanel.add(toColComboBox, BorderLayout.NORTH);
        //CANCEL AND OK
        buttonPanel.add(cancelButton, BorderLayout.SOUTH);
        buttonPanel.add(okButton, BorderLayout.SOUTH);
        mainPanel.add(buttonPanel);
       
        setPreferredSize(new Dimension(700, 400));
        pack();
        setResizable(false);
        setVisible(false);
    }

    public void open(Frame parent) {
        setLocationRelativeTo(parent);
        setVisible(true);
    }

    // Implement other methods required by EcoSysObserver interface
    // These methods will update the comboboxes and table data

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
