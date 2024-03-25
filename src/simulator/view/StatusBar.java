package simulator.view;

import java.util.List;

import javax.swing.*;
import java.awt.*;

import simulator.control.Controller;
import simulator.model.AnimalInfo;
import simulator.model.EcoSysObserver;
import simulator.model.MapInfo;
import simulator.model.RegionInfo;

public class StatusBar extends JPanel implements EcoSysObserver{
 // TODO Add any necessary attributes.
	private JLabel timeLabel;
    private JLabel animalsLabel;
    private JLabel dimensionsLabel;

    
    StatusBar(Controller ctrl){
    initGUI();
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

    public void updateTime(int time) {
        timeLabel.setText("Time: " + time);
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
