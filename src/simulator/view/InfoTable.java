package simulator.view;

import java.awt.*;

import javax.swing.*;
import javax.swing.table.TableModel;

public class InfoTable extends JPanel{
	 String _title;
	    TableModel _tableModel;
	    InfoTable(String title, TableModel tableModel) {
	        _title = title;
	        _tableModel = tableModel;
	        initGUI();
	    }
	    private void initGUI() {
	    	// Change panel layout to BorderLayout
	        this.setLayout(new BorderLayout());

	        // Add border with title
	        this.setBorder(BorderFactory.createTitledBorder(_title));

	        // Create JTable with vertical scroll bar using _tableModel
	        JTable table = new JTable(_tableModel);
	        JScrollPane scrollPane = new JScrollPane(table);
	        this.add(scrollPane, BorderLayout.CENTER);
	    }
}
