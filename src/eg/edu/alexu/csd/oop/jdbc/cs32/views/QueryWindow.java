package eg.edu.alexu.csd.oop.jdbc.cs32.views;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.util.Properties;
import java.util.Vector;
import java.sql.*;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpringLayout;
import javax.swing.table.DefaultTableModel;

import eg.edu.alexu.csd.oop.jdbc.cs32.model.ConcreteDriver;
public class QueryWindow {
	private static JFrame jFrame;
	
	private static JSeparator jSeparator;
	
	private static JPanel mainPanel;
	
	private static JTextField databasePath;
	
	private static JTextArea sQLCommandArea;
	private static JTable sQLResultsArea;
	
	private static JScrollPane resultsScrollPane;
	private static JScrollPane commandScrollPane;
	
	private static JLabel connectionL;
	private static JLabel dBInformationL;
	private static JLabel sQLCommandL;
	private static JLabel sQLExecutionL;
	private static JLabel databaseL;
	private static JLabel driverL;
	
	private static JButton executeButton;
	private static JButton clearCommandButton;
	private static JButton connectButton;
	private static JButton clearResultsButton;
	private static JButton disconnectButton;
	
	private static SpringLayout springLayout;
	
	private static Driver driver;
	private static Connection connection;
	private static ExecuteStatement executeStatement;
	
	public static void main(String[] args){
		Driver concreteDriver = new ConcreteDriver();
		driver = concreteDriver;
		InitWindow();
	}
	private static void InitWindow(){
		jFrame = new JFrame("Query Window");
		mainPanel = new JPanel();
		jFrame.setSize(750,450);
		
		jSeparator = new JSeparator();
        Dimension mDim = jSeparator.getPreferredSize();  
        mDim.height = 1;
        jSeparator.setPreferredSize(mDim);   
		
        mainPanel.add(jSeparator);
        
		InitLabels();
		InitTextFields();
		InitButtons();
		InitPlacement();
		
		jFrame.add(mainPanel);
		
		jFrame.addWindowListener(new WindowListener(){

			@Override
			public void windowActivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowClosed(WindowEvent arg0) {

			}

			@Override
			public void windowClosing(WindowEvent arg0) {
					try {
						if (connection!= null)
							connection.close();
					} catch (SQLException e) {
						System.out.println("Could not close connection, it's okay though application closing...");
						jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
					}
					jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			}

			@Override
			public void windowDeactivated(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowDeiconified(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowIconified(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void windowOpened(WindowEvent arg0) {
				// TODO Auto-generated method stub
				
			}});
		
		jFrame.setVisible(true);
	}
	private static void InitLabels(){
		connectionL = new JLabel("No Connection Now");
		dBInformationL = new JLabel("Enter Database Information");
		sQLCommandL = new JLabel("Enter SQL Command");
		sQLExecutionL = new JLabel("SQL Execution Results");
		driverL = new JLabel("JBDC Driver");
		databaseL = new JLabel("Database URL");
		
		mainPanel.add(connectionL);
		mainPanel.add(dBInformationL);
		mainPanel.add(sQLCommandL);
		mainPanel.add(sQLExecutionL);
		mainPanel.add(driverL);
		mainPanel.add(databaseL);
		
	}
	private static void InitTextFields(){
		
		sQLCommandArea = new JTextArea(5,30);
		databasePath = new JTextField(30);
		sQLResultsArea = new JTable();
		resultsScrollPane = new JScrollPane(sQLResultsArea);
		commandScrollPane = new JScrollPane(sQLCommandArea);
		mainPanel.add(commandScrollPane);
		mainPanel.add(resultsScrollPane);
		mainPanel.add(databasePath);
		
	}
	private static void InitButtons(){
		connectButton = new JButton("Connect");
		executeButton = new JButton("Execute SQL Command");
		executeButton.setEnabled(false);
		clearCommandButton = new JButton("Clear Command");
		clearResultsButton = new JButton("Clear Results");
		disconnectButton = new JButton("Disconnect");
		
		connectButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
					Properties info = new Properties();
					try {
						if (databasePath.getText() == null) {
							File dbDir = new File("Databases");
							info.put("path", dbDir.getAbsoluteFile());
						} else {
							File dbDir = new File(databasePath.getText());
							info.put("path", dbDir.getAbsoluteFile());
						}
						connection = driver.connect("jdbc:xmldb://localhost", info);
					} catch (SQLException e) {
						JOptionPane.showMessageDialog(mainPanel, "Could not connect! try again!");
					}
					if(connection != null){
						connectionL.setText("jdbc:mysql://localhost");
						executeButton.setEnabled(true);
					}
			}
		});
		disconnectButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
					try {
						connection.close();
						connection = null;
					} catch (SQLException e) {
						JOptionPane.showMessageDialog(mainPanel, "Could not close connection");
					}
					if(connection == null){
						connectionL.setText("");
						executeButton.setEnabled(false);
					}
			}
		});
		executeButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent arg0) {
				String query = sQLCommandArea.getText();
				executeStatement = new ExecuteStatement(connection);
				Vector<String> mColumns = new Vector<String>();
				Vector<Vector<String>> mResults = new Vector<Vector<String>>();
				String[] splittedQueries = query.split("\n");
				if (splittedQueries.length > 1) {
					try {
						executeStatement.takeQueries(splittedQueries);
						return;
					} catch(Exception e) {
						JOptionPane.showMessageDialog(mainPanel, "Could not execute queries!");
					}
				}

				if(query.toLowerCase().startsWith("select")){
					try {
						mResults = executeStatement.runQuery(query);
						mColumns = executeStatement.getColumns();
						sQLResultsArea.setModel(new DefaultTableModel(mResults,mColumns));
					} catch (SQLException e) {
						JOptionPane.showMessageDialog(mainPanel, "Could not execute query!");
						//e.printStackTrace();
					}
				} else if (query.toLowerCase().startsWith("create")) {
					try {
						executeStatement.runCreate(query);
					} catch (SQLException e) {
						JOptionPane.showMessageDialog(mainPanel, "Could not execute query!");
						//e.printStackTrace();
					}
				}
				else{
					try {
						executeStatement.runUpdate(query);
						sQLResultsArea.setModel(new DefaultTableModel(new String[][]{new String[]{"Row Updated!"}},new String[]{""}));
					} catch (SQLException e) {
						JOptionPane.showMessageDialog(mainPanel, "Could not execute query!");
						//e.printStackTrace();
					}
				}
				
			}
			
		});
		clearCommandButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent arg0) {
				sQLCommandArea.setText("");
				
			}});
		clearResultsButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				sQLResultsArea.setModel(new DefaultTableModel(new String[][]{new String[]{""}},new String[]{""}));
				
			}});
		mainPanel.add(connectButton);
		mainPanel.add(disconnectButton);
		mainPanel.add(executeButton);
		mainPanel.add(clearCommandButton);
		mainPanel.add(clearResultsButton);
	}
	private static void InitPlacement(){
		springLayout = new SpringLayout();
		
		springLayout.putConstraint(SpringLayout.NORTH, dBInformationL, 10, SpringLayout.NORTH, mainPanel);
		springLayout.putConstraint(SpringLayout.WEST, dBInformationL, 5, SpringLayout.WEST, mainPanel);
		
		springLayout.putConstraint(SpringLayout.NORTH, driverL,20, SpringLayout.SOUTH,dBInformationL);
		springLayout.putConstraint(SpringLayout.WEST, driverL,5, SpringLayout.WEST,mainPanel);
		
		springLayout.putConstraint(SpringLayout.NORTH, databasePath, 40, SpringLayout.SOUTH, driverL);
		springLayout.putConstraint(SpringLayout.WEST, databasePath,5, SpringLayout.WEST, driverL);
		
		springLayout.putConstraint(SpringLayout.NORTH, databaseL,18, SpringLayout.SOUTH,driverL);
		springLayout.putConstraint(SpringLayout.WEST, databaseL,5, SpringLayout.WEST,mainPanel);
		
		
		springLayout.putConstraint(SpringLayout.SOUTH, connectionL, -10, SpringLayout.VERTICAL_CENTER, mainPanel);
		springLayout.putConstraint(SpringLayout.WEST, connectionL, 10, SpringLayout.WEST, mainPanel);
		
		springLayout.putConstraint(SpringLayout.SOUTH, connectButton, -10, SpringLayout.VERTICAL_CENTER, mainPanel);
		springLayout.putConstraint(SpringLayout.EAST, connectButton, -150, SpringLayout.HORIZONTAL_CENTER, mainPanel);
		
		springLayout.putConstraint(SpringLayout.SOUTH, disconnectButton, -10, SpringLayout.VERTICAL_CENTER, mainPanel);
		springLayout.putConstraint(SpringLayout.EAST, disconnectButton, -50, SpringLayout.HORIZONTAL_CENTER, mainPanel);
		
		springLayout.putConstraint(SpringLayout.WEST, sQLCommandL, 160, SpringLayout.EAST, dBInformationL);
		springLayout.putConstraint(SpringLayout.NORTH, sQLCommandL, 10, SpringLayout.NORTH, mainPanel);
		springLayout.putConstraint(SpringLayout.WEST, sQLCommandL, 10, SpringLayout.HORIZONTAL_CENTER, mainPanel);
		
		springLayout.putConstraint(SpringLayout.WEST, commandScrollPane, 10, SpringLayout.HORIZONTAL_CENTER, mainPanel);
		springLayout.putConstraint(SpringLayout.NORTH, commandScrollPane, 10, SpringLayout.SOUTH, sQLCommandL);
		springLayout.putConstraint(SpringLayout.EAST, commandScrollPane, -10, SpringLayout.EAST, mainPanel);
		springLayout.putConstraint(SpringLayout.SOUTH, commandScrollPane, -10, SpringLayout.NORTH, clearCommandButton);
		
		springLayout.putConstraint(SpringLayout.EAST, clearCommandButton, -10, SpringLayout.EAST, mainPanel);
		springLayout.putConstraint(SpringLayout.SOUTH, clearCommandButton, -10, SpringLayout.VERTICAL_CENTER, mainPanel);
		
		springLayout.putConstraint(SpringLayout.WEST, executeButton, 0, SpringLayout.WEST, commandScrollPane);
		springLayout.putConstraint(SpringLayout.SOUTH, executeButton, -10, SpringLayout.VERTICAL_CENTER, mainPanel);
		
		
		springLayout.putConstraint(SpringLayout.SOUTH, resultsScrollPane, -10, SpringLayout.SOUTH, mainPanel);
		springLayout.putConstraint(SpringLayout.WEST, resultsScrollPane, 10, SpringLayout.WEST, mainPanel);
		springLayout.putConstraint(SpringLayout.NORTH, resultsScrollPane, 10, SpringLayout.SOUTH, clearResultsButton);
		springLayout.putConstraint(SpringLayout.EAST, resultsScrollPane, -10, SpringLayout.EAST, mainPanel);

		springLayout.putConstraint(SpringLayout.NORTH, clearResultsButton, 10, SpringLayout.VERTICAL_CENTER, mainPanel);
		springLayout.putConstraint(SpringLayout.EAST, clearResultsButton, 0, SpringLayout.EAST, resultsScrollPane);
		
		springLayout.putConstraint(SpringLayout.NORTH, sQLExecutionL, 10, SpringLayout.VERTICAL_CENTER, mainPanel);
		springLayout.putConstraint(SpringLayout.WEST, sQLExecutionL, 5, SpringLayout.WEST, mainPanel);
		
		springLayout.putConstraint(SpringLayout.HORIZONTAL_CENTER, jSeparator, 0, SpringLayout.HORIZONTAL_CENTER, mainPanel);
		springLayout.putConstraint(SpringLayout.VERTICAL_CENTER, jSeparator, 0, SpringLayout.VERTICAL_CENTER, mainPanel);
		springLayout.putConstraint(SpringLayout.EAST, jSeparator, 0, SpringLayout.EAST, mainPanel);
		springLayout.putConstraint(SpringLayout.WEST, jSeparator, 0, SpringLayout.WEST, mainPanel);
		
		mainPanel.setLayout(springLayout);
	}
}
