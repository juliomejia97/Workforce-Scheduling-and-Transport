import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.ArrayList;
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


@SuppressWarnings("unused")
public class AirlineGUI extends JPanel implements WindowListener{

	private static final long serialVersionUID = 1L;
	
	private JFrame menu;
	private JLabel lblTitulo;
	private JLabel lblFOTotal;
	private JLabel lblFOFaltantes;
	private JLabel lblFOBienestar;
	private JTextField txtFuncionObjetivo;
	private JTextField txtFuncionObjetivoFaltantes;
	private JTextField txtFuncionObjetivoBienestar;
	private JScrollPane barraArrastre;
	private JTable tblAgentes;
	private Agent myAgent;
	
	public AirlineGUI(Agent a, ArrayList<String[][]> schedule, double FO) {
		
		myAgent = a;
		menu = new JFrame();
		menu.getContentPane().setBackground(Color.WHITE);
		menu.setSize(700, 600);
		menu.setTitle("Airline Scheduling System");
		menu.getContentPane().setLayout(new BorderLayout());
		menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		String[] dataHeader = {"Agent Id", "Tuesday", "Wendsday", "Thurdsday", "Friday", "Saturday", "Sunday", "Monday", "Tuesday"};
		Vector<String> header = new Vector<String>(Arrays.asList(dataHeader));
		Vector<String> view = new Vector<String>();
		Vector<List<String>> agentData = new Vector<List<String>>();
		
		
		setBackground(Color.BLACK);
		setSize(700, 600);
		setLayout(null);
		
		lblTitulo = new JLabel("Airline Scheduling System");
		lblTitulo.setForeground(Color.WHITE);
		lblTitulo.setFont(new Font("Tahoma", Font.PLAIN, 25));
		lblTitulo.setBounds(200, 0, 300, 62);
		add(lblTitulo);
		
		
		lblFOTotal = new JLabel("Total OF ");
		lblFOTotal.setFont(new Font("Tahoma", Font.PLAIN, 17));
		lblFOTotal.setForeground(Color.WHITE);
		lblFOTotal.setBounds(177, 73, 100, 26);
		add(lblFOTotal);
		
		txtFuncionObjetivo = new JTextField("" + FO);
		txtFuncionObjetivo.setBounds(336, 76, 167, 26);
		txtFuncionObjetivo.setColumns(10);
		txtFuncionObjetivo.setEditable(false);
		add(txtFuncionObjetivo);
		
		lblFOFaltantes = new JLabel("Unatended FO ");
		lblFOFaltantes.setFont(new Font("Tahoma", Font.PLAIN, 17));
		lblFOFaltantes.setForeground(Color.WHITE);
		lblFOFaltantes.setBounds(177, 110, 150, 26);
		add(lblFOFaltantes);
		
		txtFuncionObjetivoFaltantes = new JTextField("" + FO);
		txtFuncionObjetivoFaltantes.setBounds(336, 110, 167, 26);
		txtFuncionObjetivoFaltantes.setColumns(10);
		txtFuncionObjetivoFaltantes.setEditable(false);
		add(txtFuncionObjetivoFaltantes);
		
		lblFOBienestar = new JLabel("Wellness FO ");
		lblFOBienestar.setFont(new Font("Tahoma", Font.PLAIN, 17));
		lblFOBienestar.setForeground(Color.WHITE);
		lblFOBienestar.setBounds(177, 144, 150, 26);
		add(lblFOBienestar);
		
		txtFuncionObjetivoBienestar = new JTextField();
		txtFuncionObjetivoBienestar.setBounds(336, 144, 167, 26);
		txtFuncionObjetivoBienestar.setColumns(10);
		txtFuncionObjetivoBienestar.setEditable(false);
		add(txtFuncionObjetivoBienestar);
		
				
		//PRUEBA
		for(int i = 0; i < 75; i++) {
			
			view = new Vector<String>();
			view.add("Agent " + (i + 1));
			String[][] sch = schedule.get(i);
			String hour = sch[0][0].split(" ")[1];
			for(int j = 0; j < 8; j++) {
				if(sch[j][1].equalsIgnoreCase("LLLL")) {
					view.add("Libre");
				}else {
					view.add("" + hour + "-" + sch[j][1].toLowerCase());
				}
			}

			agentData.add(view);
		}
		
		barraArrastre = new JScrollPane();
		barraArrastre.setBounds(18, 217, 650, 280);
		add(barraArrastre);
		tblAgentes = new JTable();
		barraArrastre.setViewportView(tblAgentes);
		tblAgentes.setModel(new DefaultTableModel(agentData, header));
		
		menu.getContentPane().add(this);
		menu.setResizable(false);
		menu.setVisible(true);
		
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
