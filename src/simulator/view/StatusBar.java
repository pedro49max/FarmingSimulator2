package simulator.view;

import java.util.List;
import java.util.Locale;

import javax.swing.*;
import java.awt.*;

import simulator.control.Controller;
import simulator.launcher.Main;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

public class StatusBar extends JPanel implements EcoSysObserver{
 // TODO Add any necessary attributes.
	private JLabel timeLabel;
    private JLabel animalsLabel;
    private JLabel dimensionsLabel;
    private Controller _ctrl;
    
    StatusBar(Controller ctrl){
    initGUI();
    _ctrl = ctrl;
    _ctrl.addObserver(this);
    }
    
    private void initGUI() {
        this.setLayout(new FlowLayout(FlowLayout.LEFT));
        this.setBorder(BorderFactory.createBevelBorder(1));

        // Time label
        timeLabel = new JLabel("Time: ");
        this.add(timeLabel);
        
        // Separator
        JSeparator separator1 = new JSeparator(JSeparator.VERTICAL);
        separator1.setPreferredSize(new Dimension(10, 20));
        this.add(separator1);

        // Animals label
        animalsLabel = new JLabel("Animals: ");
        this.add(animalsLabel);

        // Separator
        JSeparator separator2 = new JSeparator(JSeparator.VERTICAL);
        separator2.setPreferredSize(new Dimension(10, 20));
        this.add(separator2);

        // Dimensions label
        dimensionsLabel = new JLabel("Dimensions: ");
        this.add(dimensionsLabel);
    }

    public void updateTime(double time) {
    	String formattedValue = String.format(Locale.getDefault(), "%.3f", time).replace(',', '.');
        timeLabel.setText("Time: " + formattedValue);
    }

    public void updateAnimalCount(int count) {
        animalsLabel.setText("Animals: " + count);
    }

    public void updateDimensions(int width, int height, int rows, int cols) {
        dimensionsLabel.setText("Dimensions: " + width + "x" + height + ", Rows: " + rows + ", Cols: " + cols);
    }
	@Override
	public void onRegister(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onReset(double time, MapInfo map, List<AnimalInfo> animals) {
		// TODO Auto-generated method stub
		updateTime(time);
		updateAnimalCount(animals.size());
		updateDimensions(map.get_width(), map.get_height(), map.get_rows(), map.get_cols());
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
		updateTime(time);
		updateAnimalCount(animals.size());
		updateDimensions(map.get_width(), map.get_height(), map.get_rows(), map.get_cols());
	}
	
}
