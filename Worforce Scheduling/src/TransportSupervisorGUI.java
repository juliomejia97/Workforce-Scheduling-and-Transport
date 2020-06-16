import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import jade.core.Agent;

public class TransportSupervisorGUI extends JPanel {

	private static final long serialVersionUID = 1L;
	
	private JFrame menu;
	private JLabel lblTitulo;
	private JLabel lblFOTotal;
	private JLabel lblPenalization;
	private JLabel lblAditionalKm;
	private JLabel lblIdealDistance;
	private JLabel lblIda;
	private JLabel lblVuelta;
	private JTextField txtFuncionObjetivo;
	private JTextField txtPenalization;
	private JTextField txtAditionalKm;
	private JTextField txtIdealDistance;
	private JScrollPane barraArrastreIda;
	private JTable tblIda;
	private JScrollPane barraArrastreVuelta;
	private JTable tblVuelta;
	private Agent myAgent;
	
	public TransportSupervisorGUI(Agent a) {
		
		myAgent = a;
		menu = new JFrame();
		menu.getContentPane().setBackground(Color.WHITE);
		menu.setSize(700, 650);
		menu.setTitle("Airline Scheduling System");
		menu.getContentPane().setLayout(new BorderLayout());
		menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		String[] dataHeader = {"Vehicle Id", "Day", "Hour", "Passenger 1 Id", "Passenger 2 Id", "Passenger 3 Id", "Passenger 4 Id"};
		Vector<String> header = new Vector<String>(Arrays.asList(dataHeader));
		
		Vector<String> viewIda = new Vector<String>();
		Vector<List<String>> agentDataIda = new Vector<List<String>>();
		
		Vector<String> viewVuelta = new Vector<String>();
		Vector<List<String>> agentDataVuelta = new Vector<List<String>>();
		
		
		setBackground(Color.BLACK);
		setSize(700, 700);
		setLayout(null);
		
		lblTitulo = new JLabel("Transport Routing System");
		lblTitulo.setForeground(Color.WHITE);
		lblTitulo.setFont(new Font("Tahoma", Font.PLAIN, 25));
		lblTitulo.setBounds(200, 0, 300, 62);
		add(lblTitulo);
		
		
		lblFOTotal = new JLabel("Wellness FO ");
		lblFOTotal.setFont(new Font("Tahoma", Font.PLAIN, 17));
		lblFOTotal.setForeground(Color.WHITE);
		lblFOTotal.setBounds(177, 73, 100, 26);
		add(lblFOTotal);
		
		txtFuncionObjetivo = new JTextField();
		txtFuncionObjetivo.setBounds(336, 76, 167, 26);
		txtFuncionObjetivo.setColumns(10);
		txtFuncionObjetivo.setEditable(false);
		add(txtFuncionObjetivo);
		
		lblPenalization = new JLabel("Penalization % ");
		lblPenalization.setFont(new Font("Tahoma", Font.PLAIN, 17));
		lblPenalization.setForeground(Color.WHITE);
		lblPenalization.setBounds(177, 110, 150, 26);
		add(lblPenalization);
		
		txtPenalization = new JTextField();
		txtPenalization.setBounds(336, 110, 167, 26);
		txtPenalization.setColumns(10);
		txtPenalization.setEditable(false);
		add(txtPenalization);
		
		lblAditionalKm = new JLabel("Additional Km ");
		lblAditionalKm.setFont(new Font("Tahoma", Font.PLAIN, 17));
		lblAditionalKm.setForeground(Color.WHITE);
		lblAditionalKm.setBounds(177, 144, 150, 26);
		add(lblAditionalKm);
		
		txtAditionalKm = new JTextField();
		txtAditionalKm.setBounds(336, 144, 167, 26);
		txtAditionalKm.setColumns(10);
		txtAditionalKm.setEditable(false);
		add(txtAditionalKm);
		
		lblIdealDistance = new JLabel("Ideal Distance ");
		lblIdealDistance.setFont(new Font("Tahoma", Font.PLAIN, 17));
		lblIdealDistance.setForeground(Color.WHITE);
		lblIdealDistance.setBounds(177, 178, 150, 26);
		add(lblIdealDistance);
		
		txtIdealDistance = new JTextField();
		txtIdealDistance.setBounds(336, 178, 167, 26);
		txtIdealDistance.setColumns(10);
		txtIdealDistance.setEditable(false);
		add(txtIdealDistance);
		
		lblIda = new JLabel("Transportation from households to airport ");
		lblIda.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblIda.setForeground(Color.WHITE);
		lblIda.setBounds(18, 220, 300, 26);
		add(lblIda);
		
				
		//PRUEBA
//		for(int i = 0; i < 75; i++) {
//			
//			viewIda = new Vector<String>();
//			viewIda.add("Agent " + (i + 1));
//			String[][] sch = schedule.get(i);
//			String hour = sch[0][0].split(" ")[1];
//			for(int j = 0; j < 8; j++) {
//				if(sch[j][1].equalsIgnoreCase("LLLL")) {
//					viewIda.add("Libre");
//				}else {
//					viewIda.add("" + hour + "-" + sch[j][1].toLowerCase());
//				}
//			}
//
//			agentDataIda.add(viewIda);
//		}
		
		barraArrastreIda = new JScrollPane();
		barraArrastreIda.setBounds(18, 240, 650, 150);
		add(barraArrastreIda);
		tblIda = new JTable();
		barraArrastreIda.setViewportView(tblIda);
		tblIda.setModel(new DefaultTableModel(agentDataIda, header));
		
		lblVuelta = new JLabel("Transportation from airport to households ");
		lblVuelta.setFont(new Font("Tahoma", Font.PLAIN, 13));
		lblVuelta.setForeground(Color.WHITE);
		lblVuelta.setBounds(18, 430, 300, 26);
		add(lblVuelta);
		
		barraArrastreVuelta = new JScrollPane();
		barraArrastreVuelta.setBounds(18, 450, 650, 150);
		add(barraArrastreVuelta);
		tblVuelta = new JTable();
		barraArrastreVuelta.setViewportView(tblVuelta);
		tblVuelta.setModel(new DefaultTableModel(agentDataVuelta, header));
		
		menu.getContentPane().add(this);
		menu.setResizable(false);
		menu.setVisible(true);
		
	}
}