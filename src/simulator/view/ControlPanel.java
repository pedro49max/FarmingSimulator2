package simulator.view;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.*;

import simulator.control.Controller;
import simulator.launcher.Main;

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
        //_fileButton.addActionListener(this::);
        _toolBar.add(_fileButton);
        
     // Map Button
        _toolBar.add(Box.createGlue());
        _toolBar.addSeparator();
        _mapButton = new JButton();
        _mapButton.setToolTipText("Map Viewer");
        _mapButton.setIcon(new ImageIcon("resources/icons/viewer.png"));
        //_mapButton.addActionListener(this::);
        _toolBar.add(_mapButton);
        
        
     // Regions Button
        _toolBar.add(Box.createGlue());
        _toolBar.addSeparator();
        _regButton = new JButton();
        _regButton.setToolTipText("Map Viewer");
        _regButton.setIcon(new ImageIcon("resources/icons/regions.png"));
        //_regButton.addActionListener(this::);
        _toolBar.add(_regButton);
        // Initialize change regions dialog
        _changeRegionsDialog = new ChangeRegionsDialog(_ctrl);
     
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
        _stepsSpinner = new JSpinner(new SpinnerNumberModel(1, 1, Integer.MAX_VALUE, 1));
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

    private void runSimulation(ActionEvent e) {
        _stopped = false;
        _runButton.setEnabled(false);
        _quitButton.setEnabled(false);
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

    private void stopSimulation(ActionEvent e) {
        _stopped = true;
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
                _stopButton.setEnabled(false);
            }
        } else {
            _stopped = true;
            _runButton.setEnabled(true);
            _quitButton.setEnabled(true);
            _stopButton.setEnabled(false);
        }
    }
}
