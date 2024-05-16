package simulator.view;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;

import org.json.JSONObject;
import org.json.JSONTokener;

import simulator.control.Controller;
import simulator.launcher.Main;
import simulator.model.AnimalInfo;
import simulator.model.MapInfo;
import simulator.model.Simulator;
import simulator.view.SimpleObjectViewer.ObjInfo;

public class ControlPanel extends JPanel {
	private Controller _ctrl;
	private ChangeRegionsDialog _changeRegionsDialog;
	private JToolBar _toolBar;
	private JFileChooser _fc;
	private boolean _stopped = true; // used in the run/stop buttons
	private JButton _fileButton;
	private JButton _regButton;
	private JButton _quitButton;
	private JButton _runButton;
	private JButton _stopButton;
	private JButton _mapButton;
	private JSpinner _stepsSpinner;
	private JTextField _deltaTimeField;

	ControlPanel(Controller ctrl) {
		_ctrl = ctrl;
		_changeRegionsDialog = new ChangeRegionsDialog(ctrl);
		initGUI();
	}

	private void initGUI() {
		setLayout(new BorderLayout());
		_toolBar = new JToolBar();
		add(_toolBar, BorderLayout.PAGE_START);

		// Initialize file chooser
		_fc = new JFileChooser();
		_fc.setCurrentDirectory(new File(System.getProperty("user.dir") + "/resources/examples"));

		// File Button
		_toolBar.add(Box.createGlue());
		_fileButton = new JButton();
		_fileButton.setToolTipText("Load an input file");
		_fileButton.setIcon(new ImageIcon("resources/icons/open.png"));
		_fileButton.addActionListener((ActionListener) new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				int result = _fc.showOpenDialog(ViewUtils.getWindow(ControlPanel.this));
				if (result == JFileChooser.APPROVE_OPTION) {
					File selectedFile = _fc.getSelectedFile();
					System.out.println("Selected file: " + selectedFile.getAbsolutePath());
					try (InputStream is = new FileInputStream(selectedFile);) {
						JSONObject jsonObject = ControlPanel.load_JSON_file(is);
						reset(jsonObject); // For ctrl
						load(jsonObject); // For ctrl

					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						ViewUtils.showErrorMsg("File does not exist in file button");
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						ViewUtils.showErrorMsg("Wrong input in file button");
					}

				}

			}
		});
		_toolBar.add(_fileButton);

		// Map Button
		_toolBar.add(Box.createGlue());
		_toolBar.addSeparator();
		_mapButton = new JButton();
		_mapButton.setToolTipText("Map Viewer");
		_mapButton.setIcon(new ImageIcon("resources/icons/viewer.png"));
		_mapButton.addActionListener(this::map_view);
		_toolBar.add(_mapButton);

		// Regions Button
		_toolBar.add(Box.createGlue());
		_toolBar.addSeparator();
		_regButton = new JButton();
		_regButton.setToolTipText("Change Regions");
		_regButton.setIcon(new ImageIcon("resources/icons/regions.png"));
		_regButton.addActionListener(this::openRegions);
		_toolBar.add(_regButton);

		// Run Button
		_toolBar.add(Box.createGlue());
		_toolBar.addSeparator();
		_runButton = new JButton();
		_runButton.setToolTipText("Run Simulation");
		_runButton.setIcon(new ImageIcon("resources/icons/run.png"));
		_runButton.addActionListener(this::runSimulation);
		_toolBar.add(_runButton);

		// Add separator
		_toolBar.addSeparator();
		// Stop Button
		_stopButton = new JButton();
		_stopButton.setToolTipText("Stop Simulation");
		_stopButton.setIcon(new ImageIcon("resources/icons/stop.png"));
		_stopButton.setEnabled(true);
		_stopButton.addActionListener(this::stopSimulation);
		_toolBar.add(_stopButton);

		// Steps Spinner
		_toolBar.addSeparator();
		_toolBar.add(new JLabel("Steps: "));
		_stepsSpinner = new JSpinner(new SpinnerNumberModel(10000, 1, Integer.MAX_VALUE, 1));
		_toolBar.add(_stepsSpinner);

		// Delta Time Field
		_toolBar.addSeparator();
		_toolBar.add(new JLabel("Delta Time: "));
		_deltaTimeField = new JTextField(String.valueOf(Main._deltaTime), 5);
		_toolBar.add(_deltaTimeField);

		// Add separator
		_toolBar.addSeparator();

		// Quit Button
		_toolBar.add(Box.createGlue()); // this aligns the button to the right
		_toolBar.addSeparator();
		_quitButton = new JButton();
		_quitButton.setToolTipText("Quit");
		_quitButton.setIcon(new ImageIcon("resources/icons/exit.png"));
		_quitButton.addActionListener((e) -> ViewUtils.quit(this));
		_toolBar.add(_quitButton);
	}

	private static JSONObject load_JSON_file(InputStream in) {
		return new JSONObject(new JSONTokener(in));
	}

	private void runSimulation(ActionEvent e) {
		_stopped = false;
		_runButton.setEnabled(false);
		_quitButton.setEnabled(false);
		_fileButton.setEnabled(false);
		_mapButton.setEnabled(false);
		_regButton.setEnabled(false);
		_stopButton.setEnabled(true);

		double deltaTime;
		int steps;
		try {
			deltaTime = Double.parseDouble(_deltaTimeField.getText());
			steps = (int) _stepsSpinner.getValue();
			if (steps <= 0 || deltaTime <= 0) {
				throw new NumberFormatException();
			}
		} catch (NumberFormatException ex) {
			ViewUtils.showErrorMsg("Invalid input for steps or delta time.");
			_stopped = true;
			_runButton.setEnabled(true);
			_quitButton.setEnabled(true);
			_stopButton.setEnabled(false);
			return;
		}

		run_sim(steps, deltaTime);
	}

	private void openRegions(ActionEvent e) {
		_changeRegionsDialog.open(ViewUtils.getWindow(this));
	}

	private void stopSimulation(ActionEvent e) {
		_stopped = true;
		_runButton.setEnabled(true);
		_stopButton.setEnabled(false);
		_quitButton.setEnabled(true);
		_fileButton.setEnabled(true);
		_mapButton.setEnabled(true);
		_regButton.setEnabled(true);
	}

	private void run_sim(int n, double dt) {
		if (n > 0 && !_stopped) {
			try {
				_ctrl.advance(dt);
				SwingUtilities.invokeLater(() -> run_sim(n - 1, dt));
			} catch (Exception e) {
				ViewUtils.showErrorMsg("An error occurred while running the simulation: " + e.getMessage());
				_stopped = true;
				_runButton.setEnabled(true);
				_quitButton.setEnabled(true);
				_stopButton.setEnabled(true);
			}
		} else {
			_stopped = false;
			//_runButton.setEnabled(false);
			_quitButton.setEnabled(false);
			_stopButton.setEnabled(true);
		}
	}

	private void reset(JSONObject ob) {
		int cols = ob.getInt("cols");
		int rows = ob.getInt("rows");
		int width = ob.getInt("width");
		int height = ob.getInt("height");

		_ctrl.reset(cols, rows, width, height);
	}

	private void load(JSONObject ob) {
		_ctrl.load_data(ob);
	}

	private void map_view(ActionEvent e) {
	    // Instantiate a new MapWindow
	    Frame parentFrame = (Frame) SwingUtilities.getWindowAncestor(this);
	    
	    // Create a new MapWindow instance with the parent frame and the controller
	    MapWindow mapWindow = new MapWindow(parentFrame, _ctrl);
	    
	    // Make the new MapWindow visible
	    mapWindow.setVisible(true);
	    
	    // Set the default close operation to dispose to close the window without exiting the application
	    mapWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

	}
}
