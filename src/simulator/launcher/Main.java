package simulator.launcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import simulator.control.Controller;
import simulator.factories.*;
import simulator.misc.Utils;
import simulator.model.Animal;
import simulator.model.Region;
import simulator.model.SelectionStrategy;
import simulator.model.Simulator;
import simulator.view.MainWindow;

import javax.swing.*;
public class Main {
	private enum ExecMode {
		BATCH("batch", "Batch mode"), GUI("gui", "Graphical User Interface mode");

		private String _tag;
		private String _desc;

		private ExecMode(String modeTag, String modeDesc) {
			_tag = modeTag;
			_desc = modeDesc;
		}

		public String get_tag() {
			return _tag;
		}

		public String get_desc() {
			return _desc;
		}
	}

	// default values for some parameters
	//
	private final static Double _default_time = 10.0; // in seconds
	private final static Double _default_deltaTime = 0.03; // in seconds

	// some attributes to stores values corresponding to command-line parameters
	//
	public static Double _deltaTime = null;
	private static Double _time = null;
	private static String _in_file = null;
	private static String _out_file = null;
	private static boolean _simple_view = false;
	private static ExecMode _mode = ExecMode.GUI;
	public static Factory<Animal> animals_factory;
	public static Factory<Region> regions_factory;
	public static Factory<SelectionStrategy> strategyFactory;

	private static void parse_args(String[] args) {

		// define the valid command line options
		//
		Options cmdLineOptions = build_options();

		// parse the command line as provided in args
		//
		CommandLineParser parser = new DefaultParser();
		try {
			CommandLine line = parser.parse(cmdLineOptions, args);
			//System.out.println(line.getOptionValue('i'));//debuggin
			parse_help_option(line, cmdLineOptions);
			parse_in_file_option(line);
			parse_time_option(line);
			parse_output_option(line);
			parse_delta_time_option(line);
			parse_simple_viewer_option(line);
			parse_mode_option(line);
			// if there are some remaining arguments, then something wrong is
			// provided in the command line!
			//
			String[] remaining = line.getArgs();
			if (remaining.length > 0) {
				String error = "Illegal arguments:";
				for (String o : remaining) {
					System.out.println(o);
					error += (" " + o);
				}
				throw new ParseException(error);
			}

		} catch (ParseException e) {
			System.out.println("mainParse");
			System.err.println(e.getLocalizedMessage());
			System.exit(1);
		}

	}

	private static Options build_options() {
		Options cmdLineOptions = new Options();
		// delta
		cmdLineOptions.addOption(Option.builder("dt").longOpt("deltaTime").hasArg()
				.desc("A double representing actual time, in seconds, per simulation step. Default value: "
						+ _default_deltaTime + ".")
				.build());
		
		
		// help
		cmdLineOptions.addOption(Option.builder("h").longOpt("help").desc("Print this message.").build());

		// input file
		cmdLineOptions.addOption(Option.builder("i").longOpt("input").hasArg().desc("A configuration file.").build());

		// output file
		cmdLineOptions.addOption(Option.builder("o").longOpt("output").hasArg().desc("Output file, where output is written.").build());
		
		//simple viewer
		cmdLineOptions.addOption(Option.builder("sv").longOpt("simple-viewer").desc("Show the viewer window in console mode.").build());
		
		// steps
		cmdLineOptions.addOption(Option.builder("t").longOpt("time").hasArg()
				.desc("An real number representing the total simulation time in seconds. Default value: "
						+ _default_time + ".")
				.build());
		
		// mode
		cmdLineOptions.addOption(Option.builder("m").longOpt("mode")
	            .hasArg()
	            .desc("Execution Mode. Possible values: 'batch' (Batch mode), 'gui' (Graphical User Interface mode). Default value: 'gui'.")
	            .build());

		return cmdLineOptions;
	}

	private static void parse_help_option(CommandLine line, Options cmdLineOptions) {
		if (line.hasOption("h")) {
			HelpFormatter formatter = new HelpFormatter();
			formatter.printHelp(Main.class.getCanonicalName(), cmdLineOptions, true);
			System.exit(0);
		}
	}

	private static void parse_in_file_option(CommandLine line) throws ParseException {
		_in_file = line.getOptionValue("i");
		if (_mode == ExecMode.BATCH && _in_file == null) {
			throw new ParseException("In batch mode an input configuration file is required");
		}
	}
	private static void parse_delta_time_option(CommandLine line) throws ParseException {
		String t = line.getOptionValue("dt", _default_deltaTime.toString());
		//System.out.println(t);
		try {
			_deltaTime = Double.parseDouble(t);
			assert (_deltaTime>= 0);
		} catch (Exception e) {
			System.out.println(t);
			throw new ParseException("Invalid value for time: " + t);
		}
	
    }
	private static void parse_output_option(CommandLine line) throws ParseException{
		_out_file = line.getOptionValue("o");
		if (_mode == ExecMode.BATCH && _out_file == null) {
			throw new ParseException("In batch mode an output configuration file is required");
		}
    }
	private static void parse_simple_viewer_option(CommandLine line) {
		if (line.hasOption("sv")) {
			_simple_view = true;
		}
		else
			_simple_view = false;
    }

	private static void parse_time_option(CommandLine line) throws ParseException {
		String t = line.getOptionValue("t", _default_time.toString());
		try {
			_time = Double.parseDouble(t);
			assert (_time >= 0);
		} catch (Exception e) {
			throw new ParseException("Invalid value for time: " + t);
		}
	}
	
	private static void parse_mode_option(CommandLine line) throws ParseException {
		if (line.hasOption("m")) {
            String modeInput = line.getOptionValue("m");
            if ("batch".equals(modeInput)) {
                _mode = ExecMode.BATCH;
            } else if ("gui".equals(modeInput)) {
                _mode = ExecMode.GUI;
            } else {
                throw new ParseException("Invalid mode selected: " + modeInput);
            }
        } else {
            _mode = ExecMode.GUI;  // Default mode
        }
		
		// Check required options for batch mode
        if (_mode == ExecMode.BATCH && (_in_file == null || _out_file == null)) {
            throw new ParseException("In batch mode, both input and output files are required");
        }
	}

	private static void init_factories() {
		// Initialize the selectionStrategy factory
	    List<Builder<SelectionStrategy>> strategyBuilders = new ArrayList<>();
	    // Add your strategy builders here...
	    strategyBuilders.add(new SelectClosestBuilder());
	    strategyBuilders.add(new SelectFirstBuilder());
	    strategyBuilders.add(new SelectYoungestBuilder());
	    strategyFactory = new BuilderBasedFactory<>(strategyBuilders);
		
		// Initialize the animal factory
	    List<Builder<Animal>> animalBuilders = new ArrayList<>();
	    // Add your animal builders here...
	    animalBuilders.add(new SheepBuilder(strategyFactory));
	    animalBuilders.add(new WolfBuilder(strategyFactory));
	    animals_factory = new BuilderBasedFactory<>(animalBuilders);

	    // Initialize the region factory
	    List<Builder<Region>> regionBuilders = new ArrayList<>();
	    // Add your region builders here...
	    regionBuilders.add(new DefaultRegionBuilder());
	    regionBuilders.add(new DynamicSupplyRegionBuilder());
	    regions_factory = new BuilderBasedFactory<>(regionBuilders);
	}

	private static JSONObject load_JSON_file(InputStream in) {
		return new JSONObject(new JSONTokener(in));
	}

	public static int width;
	public static int height;
	private static void start_batch_mode() throws IOException {
		try (InputStream is = new FileInputStream(new File(_in_file));
	            OutputStream os = new FileOutputStream(new File(_out_file))) {
	        // Load the input JSON file
	        JSONObject inputJSON = load_JSON_file(is);

	        // Extract parameters from the input JSON
	        width = inputJSON.getInt("width");
	        height= inputJSON.getInt("height");
	        int cols = inputJSON.getInt("cols");
	        int rows = inputJSON.getInt("rows");

	        JSONArray animalsArray = inputJSON.getJSONArray("animals");

	        // Create the simulator instance
	        Simulator simulator = new Simulator(cols, rows, width, height, animals_factory, regions_factory);

	        // Create animals based on the specifications provided in the input file
	        for (int i = 0; i < animalsArray.length()-i - 2; i++) {
	            JSONObject animalSpec = animalsArray.getJSONObject(i);
	            int amount = animalSpec.getInt("amount");
	            JSONObject animalData = animalSpec.getJSONObject("spec");
	            String animalType = animalData.getString("type");

	            // Create animals based on the type and amount specified
	            for (int j = 0; j < (amount); j++) {//Hay que arreglar que se crean demasiados bichos
	                switch (animalType) {
	                    case "sheep":
	                        simulator.add_animal(animalData);
	                        break;
	                    case "wolf":
	                        simulator.add_animal(animalData);
	                        break;
	                    default:
	                        throw new IllegalArgumentException("Invalid animal type: " + animalType);
	                }
	            }
	            
	        }

	        // Create the controller instance and run the simulation
	        Controller controller = new Controller(simulator);
	        controller.load_data(inputJSON);
	        //JFrame frame = new MainWindow(controller);
	        //frame.setVisible(true);
	        controller.run(_time, _deltaTime, _simple_view, os);
	        os.close();
	    }
		catch (IOException e) {
	        System.err.println("Error opening input file: " + e.getMessage());
	    }
	}

	private static void start_GUI_mode() throws Exception {
	    // Initialize the simulation environment with default values
	    width = 800; // Default width
	    height = 600; // Default height
	    int cols = 15; // Default number of columns
	    int rows = 20; // Default number of rows
	    
	    try (InputStream is = new FileInputStream(new File(_in_file));
	            OutputStream os = new FileOutputStream(new File(_out_file))) {
	        // Load the input JSON file
	        JSONObject inputJSON = load_JSON_file(is);

	        // Extract parameters from the input JSON
	        width = inputJSON.getInt("width");
	        height= inputJSON.getInt("height");
	        cols = inputJSON.getInt("cols");
	        rows = inputJSON.getInt("rows");

	        JSONArray animalsArray = inputJSON.getJSONArray("animals");

	        // Create the simulator instance
	        Simulator simulator = new Simulator(cols, rows, width, height, animals_factory, regions_factory);

	        // Create animals based on the specifications provided in the input file
	        for (int i = 0; i < animalsArray.length()-i - 2; i++) {
	            JSONObject animalSpec = animalsArray.getJSONObject(i);
	            int amount = animalSpec.getInt("amount");
	            JSONObject animalData = animalSpec.getJSONObject("spec");
	            String animalType = animalData.getString("type");

	            // Create animals based on the type and amount specified
	            for (int j = 0; j < (amount); j++) {//Hay que arreglar que se crean demasiados bichos
	                simulator.add_animal(inputJSON);
	            }
	        }

	        // Create the controller instance and run the simulation
	        Controller controller = new Controller(simulator);
	        controller.load_data(inputJSON);
	        //JFrame frame = new MainWindow(controller);
	        //frame.setVisible(true);
	     // Initialize the main window of the GUI
		    SwingUtilities.invokeAndWait(() -> {
		        MainWindow mainWindow = new MainWindow(controller);
		        mainWindow.setVisible(true);
		    });
		    
	        os.close();
	    }
		catch (IOException e) {
	        System.err.println("Error opening input file: " + e.getMessage());
	    }
	}


	private static void start(String[] args) throws Exception {
		init_factories();
		parse_args(args);
		switch (_mode) {
		case BATCH:
			start_batch_mode();
			break;
		case GUI:
			start_GUI_mode();
			break;
		}
	}

	public static void main(String[] args) {
		Utils._rand.setSeed(2147483647l);
		try {
			start(args);
		} catch (Exception e) {
			System.err.println("Something went wrong ...");
			System.err.println();
			e.printStackTrace();
		}
	}
}
