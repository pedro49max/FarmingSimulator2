package simulator.view;

import simulator.model.AnimalInfo;
import simulator.model.MapInfo;
import simulator.model.State;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

@SuppressWarnings("serial")
public class MapViewer extends AbstractMapViewer {

    private int _width = 800; // Default width
    private int _height = 600; // Default height

    private int _rows; // Number of rows in the simulation
    private int _cols; // Number of columns in the simulation

    private int _rwidth; // Width of a region
    private int _rheight; // Height of a region

    private State _currState = null; // Current state to display, null shows all

    volatile private Collection<AnimalInfo> _objs; // Animals to display
    volatile private Double _time; // Simulation time

    private Map<String, SpeciesInfo> _kindsInfo = new HashMap<>(); // Species information

    private Font _font = new Font("Arial", Font.BOLD, 12); // Font for text

    private boolean _showHelp = true; // Flag to show help text

    public MapViewer() {
        initGUI();
    }

    private void initGUI() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch (e.getKeyChar()) {
                case 'h':
                    _showHelp = !_showHelp;
                    repaint();
                    break;
                case 's':
                    cycleState();
                    repaint();
                    break;
                default:
                	repaint();
                	break;
                }
            }
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                requestFocus();
            }
        });

        setFocusable(true);
        requestFocusInWindow();
    }

    private void cycleState() {
        if (_currState == null) {
            _currState = State.values()[0];
        } else {
            int nextStateIndex = (_currState.ordinal() + 1) % State.values().length;
            _currState = State.values()[nextStateIndex];
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D gr = (Graphics2D) g;
        gr.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        gr.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        // Set the font for the text
        g.setFont(_font);

        // Set the background color to white and clear the area
        gr.setBackground(Color.WHITE);
        gr.clearRect(0, 0, getWidth(), getHeight());

        // Draw the grid lines
        drawGrid(gr);

        // If the help is to be displayed, draw the help text
        if (_showHelp) {
            drawHelpText(gr);
        }

        // If there are objects to be drawn, draw them
        if (_objs != null) {
            drawObjects(gr, _objs, _time);
        }
    }

    private void drawGrid(Graphics2D g) {
        // Set the color for the grid lines
        g.setColor(Color.LIGHT_GRAY);

        // Draw the horizontal grid lines
        for (int i = 1; i < _rows; i++) {
            int y = i * (_height / _rows);
            g.drawLine(0, y, _width, y);
        }

        // Draw the vertical grid lines
        for (int i = 1; i < _cols; i++) {
            int x = i * (_width / _cols);
            g.drawLine(x, 0, x, _height);
        }
    }


    private void drawHelpText(Graphics2D g) {
        g.setColor(Color.BLACK);
        g.drawString("h: toggle help", 10, 20);
        g.drawString("s: show animals of a specific state", 10, 40);
    }

    private boolean visible(AnimalInfo a) {
        return _currState == null || a.get_state() == _currState;
    }

    private void drawObjects(Graphics2D g, Collection<AnimalInfo> animals, Double time) {
        // Reset species info counts
        _kindsInfo.values().forEach(info -> info._count = 0);
        
        for (AnimalInfo a : animals) {
            if (!visible(a)) continue;
            
            SpeciesInfo info = _kindsInfo.computeIfAbsent(a.get_genetic_code(),
                    k -> new SpeciesInfo(ViewUtils.get_color(a.get_genetic_code())));
            info._count++;
            	
            // Calculate position
            int x = (int) (a.get_position().getX());
            int y = (int) (a.get_position().getY());

            int size = (int) (a.get_age() / 2 + 2);
            g.setColor(info._color);
            g.fillOval(x, y, size, size);
        }

        // Draw species info and time
        drawSpeciesInfo(g);
        if (_currState != null)
        	drawStateInfo(g, _currState);
        drawTimeInfo(g, time);
    }

    private void drawSpeciesInfo(Graphics2D g) {
        int y = 60;
        for (Entry<String, SpeciesInfo> entry : _kindsInfo.entrySet()) {
            g.setColor(entry.getValue()._color);
            String text = entry.getKey() + ": " + entry.getValue()._count;
            g.drawString(text, 10, y);
            y += 20;
        }
    }

    private void drawTimeInfo(Graphics2D g, double time) {
        g.setColor(Color.BLACK);
        String timeText = String.format("Time: %.2f", time);
        g.drawString(timeText, 10, _height - 10);
    }
    
    private void drawStateInfo(Graphics2D g, State s) {
        g.setColor(Color.RED);
        String timeText = "State: " +  s.toString();
        g.drawString(timeText, 10, _height - 37);
    }

    @Override
    public void update(List<AnimalInfo> objs, Double time) {
        _objs = objs;
        _time = time;
        repaint();
    }

    @Override
    public void reset(double time, MapInfo map, List<AnimalInfo> animals) {
        _width = map.get_width();
        _height = map.get_height();
        _rows = map.get_rows();
        _cols = map.get_cols();
        _rwidth = _width / _cols;
        _rheight = _height / _rows;
        setPreferredSize(new Dimension(_width, _height));
        update(animals, time);
    }

    private static class SpeciesInfo {
        Integer _count = 0;
        Color _color;
        SpeciesInfo(Color color) {
            _color = color;
        }
    }
}
