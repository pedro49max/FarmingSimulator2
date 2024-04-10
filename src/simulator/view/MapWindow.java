package simulator.view;

import java.awt.BorderLayout;
import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

public class MapWindow extends JFrame implements EcoSysObserver {
    
    private Controller _ctrl;
    private AbstractMapViewer _viewer;
    private Frame _parent;
    
    public MapWindow(Frame parent, Controller ctrl) {
        super("[MAP VIEWER]");
        _ctrl = ctrl;
        _parent = parent;
        initGUI();
        _ctrl.addObserver(this); // Register this as an observer to the controller
    }
    
    private void initGUI() {
        JPanel mainPanel = new JPanel(new BorderLayout());
        this.setContentPane(mainPanel);
        
        // Create the viewer and add it to mainPanel in the center
        _viewer = new MapViewer();
        mainPanel.add(_viewer, BorderLayout.CENTER);
        
        // Handling the window closing event to remove 'MapWindow.this' from the observers
        this.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent windowEvent) {
                _ctrl.removeObserver(MapWindow.this); // Remove this window from the observer list
                dispose();
            }
        });
        
        pack();
        if (_parent != null) {
            setLocationRelativeTo(_parent);
        }
        setResizable(false);
        setVisible(true);
    }
    
    // Implemented EcoSysObserver methods
    
    @Override
    public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
        // Reset the viewer with the current map and animals information
        _viewer.reset(time, map, animals);
        pack(); // Adjust the window size based on its content
    }
    
    @Override
    public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
        // Same as onRegister for this context
        _viewer.reset(time, map, animals);
        pack();
    }
    
    @Override
    public void onAnimalAdded(double time, MapInfo map, List<AnimalInfo> animals, AnimalInfo a) {
        // Update the viewer with the current map and animals information
        _viewer.update(animals, time);
    }
    
    // PENDING
    @Override
    public void onRegionSet(int row, int col, MapInfo map, RegionInfo r) {
        List<AnimalInfo> allAnimals = new ArrayList<>();
        for (MapInfo.RegionData regionData : map) { // Assuming MapInfo can be iterated to get RegionData
            RegionInfo regionInfo = (RegionInfo) regionData.getR(); // Cast is necessary to call getAnimalsInfo
            allAnimals.addAll(regionInfo.getAnimalsInfo()); // Aggregate AnimalInfo from each region
        }
        _viewer.update(allAnimals, _ctrl.getTime()); // Update the viewer with the list of animals
    }

    
    
    @Override
    public void onAdvanced(double time, MapInfo map, List<AnimalInfo> animals, double dt) {
        // Update the viewer with the advanced simulation step
        _viewer.update(animals, time);
    }
}
