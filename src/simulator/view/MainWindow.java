package simulator.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;

import simulator.control.Controller;

public class MainWindow extends JFrame{
	private Controller _ctrl;
	
	public MainWindow(Controller ctrl) {
		   super("[ECOSYSTEM SIMULATOR]");
		   _ctrl = ctrl;
		   initGUI();
		  }
	
	private void initGUI() {
	    JPanel mainPanel = new JPanel(new BorderLayout());
	    setContentPane(mainPanel);
	    
	    // Create ControlPanel and add it to the PAGE_START section of mainPanel
	    ControlPanel controlPanel = new ControlPanel(_ctrl);
	    mainPanel.add(controlPanel, BorderLayout.PAGE_START);

	    // Create StatusBar and add it to the PAGE_END section of mainPanel
	    StatusBar statusBar = new StatusBar(_ctrl);
	    mainPanel.add(statusBar, BorderLayout.PAGE_END);
	    
	    // Definition of the tables panel (use a vertical BoxLayout)
	    JPanel contentPanel = new JPanel();
	    contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
	    mainPanel.add(contentPanel, BorderLayout.CENTER);
	    
	    // Create the species table and add it to the contentPanel.
	    // Use setPreferredSize(new Dimension(500, 250)) to fix its size
	    InfoTable speciesTable = new InfoTable("Species", new SpeciesTableModel(_ctrl));
	    speciesTable.setPreferredSize(new Dimension(500, 250));
	    contentPanel.add(speciesTable);
	    
	    // Create the regions table.
	    // Use setPreferredSize(new Dimension(500, 250)) to fix its size
	    InfoTable regionsTable = new InfoTable("Regions", new RegionsTableModel(_ctrl));
	    regionsTable.setPreferredSize(new Dimension(500, 250));
	    contentPanel.add(regionsTable);
	    
	    // Handle window closing event
	    addWindowListener(new WindowAdapter() {
	        @Override
	        public void windowClosing(WindowEvent e) {
	            ViewUtils.quit(MainWindow.this);
	        }
	    });

	    setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
	    pack();
	    setVisible(true);
	}

}
