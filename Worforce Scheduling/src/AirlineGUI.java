import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import jade.core.Agent;


@SuppressWarnings("unused")
public class AirlineGUI extends JPanel implements WindowListener{

	private static final long serialVersionUID = 1L;
	
	private JFrame menu;
	private JLabel lblTitulo;
	private JLabel lblFOTotal;
	private JLabel lblFOFaltantes;
	private JLabel lblMaxDemand;
	private JLabel lblUnatendedDemand;
	private JLabel lblFOBienestar;
	private JLabel lblFOVariability;
	private JLabel lblRoutes;
	private JTextField txtFuncionObjetivo;
	private JTextField txtFuncionObjetivoFaltantes;
	private JTextField txtMaxDemand;
	private JTextField txtUnatendedDemand;
	private JTextField txtFuncionObjetivoBienestar;
	private JTextField txtVariability;
	private JTextField txtRoutes;
	private JScrollPane barraArrastre;
	private JTable tblAgentes;
	private JButton btnPeak;
	private Airline myAgent;
	private DefaultTableModel model;
	
	public AirlineGUI(Airline a, ArrayList<String[][]> schedule, double FO, double wellnessFO, double numRoutes, double maxDemand, double unatendedDemand) {
		
		myAgent = a;
		menu = new JFrame();
		menu.getContentPane().setBackground(Color.WHITE);
		menu.setSize(700, 650);
		menu.setTitle("Airline Scheduling System");
		menu.getContentPane().setLayout(new BorderLayout());
		menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		String[] dataHeader = {"Agent Id", "Tuesday", "Wendsday", "Thurdsday", "Friday", "Saturday", "Sunday", "Monday", "Tuesday"};
		Vector<String> header = new Vector<String>(Arrays.asList(dataHeader));
		Vector<String> view = new Vector<String>();
		Vector<List<String>> agentData = new Vector<List<String>>();
		
		DecimalFormat df = new DecimalFormat("#.##");
		
		setBackground(Color.BLACK);
		setSize(700, 600);
		setLayout(null);
		
		lblTitulo = new JLabel("Airline Scheduling System");
		lblTitulo.setForeground(Color.WHITE);
		lblTitulo.setFont(new Font("Tahoma", Font.PLAIN, 25));
		lblTitulo.setBounds(200, 0, 300, 62);
		add(lblTitulo);
		
		long totalFO =  (long) ((FO + wellnessFO) * 10000 + numRoutes);
		
		
		lblFOTotal = new JLabel("Total OF ");
		lblFOTotal.setFont(new Font("Tahoma", Font.PLAIN, 17));
		lblFOTotal.setForeground(Color.WHITE);
		lblFOTotal.setBounds(177, 73, 100, 26);
		add(lblFOTotal);
		
		txtFuncionObjetivo = new JTextField("" + df.format(totalFO));
		txtFuncionObjetivo.setBounds(336, 76, 167, 26);
		txtFuncionObjetivo.setColumns(10);
		txtFuncionObjetivo.setEditable(false);
		add(txtFuncionObjetivo);
		
		lblFOFaltantes = new JLabel("Demand OF ");
		lblFOFaltantes.setFont(new Font("Tahoma", Font.PLAIN, 17));
		lblFOFaltantes.setForeground(Color.WHITE);
		lblFOFaltantes.setBounds(177, 110, 150, 26);
		add(lblFOFaltantes);
		
		txtFuncionObjetivoFaltantes = new JTextField("" + FO);
		txtFuncionObjetivoFaltantes.setBounds(336, 110, 167, 26);
		txtFuncionObjetivoFaltantes.setColumns(10);
		txtFuncionObjetivoFaltantes.setEditable(false);
		add(txtFuncionObjetivoFaltantes);
		
		lblUnatendedDemand = new JLabel("Unatended Dem ");
		lblUnatendedDemand.setFont(new Font("Tahoma", Font.PLAIN, 17));
		lblUnatendedDemand.setForeground(Color.WHITE);
		lblUnatendedDemand.setBounds(177, 144, 150, 26);
		add(lblUnatendedDemand);
		
		txtUnatendedDemand = new JTextField("" + unatendedDemand);
		txtUnatendedDemand.setBounds(336, 144, 167, 26);
		txtUnatendedDemand.setColumns(10);
		txtUnatendedDemand.setEditable(false);
		add(txtUnatendedDemand);
		
		lblMaxDemand = new JLabel("Max Demand ");
		lblMaxDemand.setFont(new Font("Tahoma", Font.PLAIN, 17));
		lblMaxDemand.setForeground(Color.WHITE);
		lblMaxDemand.setBounds(177, 178, 150, 26);
		add(lblMaxDemand);
		
		txtMaxDemand = new JTextField("" + maxDemand);
		txtMaxDemand.setBounds(336, 178, 167, 26);
		txtMaxDemand.setColumns(10);
		txtMaxDemand.setEditable(false);
		add(txtMaxDemand);
		
		lblFOBienestar = new JLabel("Wellness OF ");
		lblFOBienestar.setFont(new Font("Tahoma", Font.PLAIN, 17));
		lblFOBienestar.setForeground(Color.WHITE);
		lblFOBienestar.setBounds(177, 212, 150, 26);
		add(lblFOBienestar);
		
		txtFuncionObjetivoBienestar = new JTextField();
		txtFuncionObjetivoBienestar.setText("" + df.format(wellnessFO));
		txtFuncionObjetivoBienestar.setBounds(336, 212, 167, 26);
		txtFuncionObjetivoBienestar.setColumns(10);
		txtFuncionObjetivoBienestar.setEditable(false);
		add(txtFuncionObjetivoBienestar);
		
		lblFOVariability = new JLabel("Variability OF ");
		lblFOVariability.setFont(new Font("Tahoma", Font.PLAIN, 17));
		lblFOVariability.setForeground(Color.WHITE);
		lblFOVariability.setBounds(177, 246, 150, 26);
		add(lblFOVariability);
		
		txtVariability = new JTextField();
		txtVariability.setText("" + 0);
		txtVariability.setBounds(336, 246, 167, 26);
		txtVariability.setColumns(10);
		txtVariability.setEditable(false);
		add(txtVariability);
		
		lblRoutes = new JLabel("Number of Routes ");
		lblRoutes .setFont(new Font("Tahoma", Font.PLAIN, 17));
		lblRoutes .setForeground(Color.WHITE);
		lblRoutes .setBounds(177, 280, 150, 26);
		add(lblRoutes );
		
		txtRoutes = new JTextField();
		txtRoutes.setText("" + numRoutes);
		txtRoutes.setBounds(336, 280, 167, 26);
		txtRoutes.setColumns(10);
		txtRoutes.setEditable(false);
		add(txtRoutes);
			
//		for(int i = 0; i < 75; i++) {
//			
//			view = new Vector<String>();
//			view.add("Agent " + (i + 1));
//			String[][] sch = schedule.get(i);
//			String hour = sch[0][0].split(" ")[1];
//			for(int j = 0; j < 8; j++) {
//				if(sch[j][1].equalsIgnoreCase("LLLL")) {
//					view.add("Libre");
//				}else {
//					view.add("" + hour + "-" + sch[j][1].toLowerCase());
//				}
//			}
//
//			agentData.add(view);
//		}
		
		barraArrastre = new JScrollPane();
		barraArrastre.setBounds(18, 320, 650, 270);
		add(barraArrastre);
		tblAgentes = new JTable();
		barraArrastre.setViewportView(tblAgentes);
		model = new DefaultTableModel();
		model.setColumnIdentifiers(header);
		for(List<String> actual:agentData) {
			model.addRow((Vector<?>) actual);
		}
		tblAgentes.setModel(model);
		menu.getContentPane().add(this);
		menu.setResizable(false);
		menu.setVisible(true);
		
		btnPeak = new JButton("Start Peak");
		btnPeak.setFont(new Font("Tahoma", Font.PLAIN, 17));
		btnPeak.setForeground(Color.RED);
		btnPeak.setBounds(270, 600, 150, 26);
		add(btnPeak);
		
		btnPeak.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				myAgent.peakDemand("Mar", "13:30", "A", 10);
			}
		});
		
	}

	@Override
	public void windowActivated(WindowEvent arg0) {
		
	}

	@Override
	public void windowClosed(WindowEvent arg0) {
		myAgent.doDelete();
		
	}

	@Override
	public void windowClosing(WindowEvent arg0) {
		
	}

	@Override
	public void windowDeactivated(WindowEvent arg0) {
		
	}

	@Override
	public void windowDeiconified(WindowEvent arg0) {
		
	}

	@Override
	public void windowIconified(WindowEvent arg0) {
		
	}

	@Override
	public void windowOpened(WindowEvent arg0) {
		
	}



}
