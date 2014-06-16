import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class Calendar{
	static JLabel monthLabel, yearLabel;
	static JButton previousButton, nextButton;
	static JTable calendarT;
	static JComboBox yearCombo;
	static JFrame mainFrame;
	static Container pane;
	static DefaultTableModel calendarTable;
	static JScrollPane calendarScroll;
	static JPanel calendarPanel;
	static int nowYear, nowMonth, nowDay, currentYear, currentMonth;

	public static void main (String args[]){
		try {UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());}
		catch (ClassNotFoundException e) {}
		catch (InstantiationException e) {}
		catch (IllegalAccessException e) {}
		catch (UnsupportedLookAndFeelException e) {}
        
		mainFrame = new JFrame ("Personal Organizer");
		mainFrame.setSize(326, 365); 
		pane = mainFrame.getContentPane();
		pane.setLayout(null);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		monthLabel = new JLabel ("");
		yearLabel = new JLabel ("Current Year:");
		yearCombo = new JComboBox();
		previousButton = new JButton ("<");
		nextButton = new JButton (">");
		calendarTable = new DefaultTableModel() {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
		calendarT = new JTable(calendarTable);
		calendarScroll = new JScrollPane(calendarT);
		calendarPanel = new JPanel(null);

		calendarPanel.setBorder(BorderFactory.createTitledBorder("Calendar"));

		previousButton.addActionListener(new previousButton_Action());
		nextButton.addActionListener(new nextButton_Action());
		yearCombo.addActionListener(new yearCombo_Action());

		pane.add(calendarPanel);
		calendarPanel.add(monthLabel);
		calendarPanel.add(yearLabel);
		calendarPanel.add(yearCombo);
		calendarPanel.add(previousButton);
		calendarPanel.add(nextButton);
		calendarPanel.add(calendarScroll);

		calendarPanel.setBounds(0, 0, 320, 335);
		monthLabel.setBounds(160-monthLabel.getPreferredSize().width/2, 25, 100, 25);
		yearLabel.setBounds(160, 305, 80, 20);
		yearCombo.setBounds(230, 305, 80, 20);
		previousButton.setBounds(10, 25, 50, 25);
		nextButton.setBounds(260, 25, 50, 25);
		calendarScroll.setBounds(10, 50, 300, 253);

		mainFrame.setResizable(false);
		mainFrame.setVisible(true);
		
		GregorianCalendar cal = new GregorianCalendar();
		nowDay = cal.get(GregorianCalendar.DAY_OF_MONTH); 
		nowMonth = cal.get(GregorianCalendar.MONTH);
		nowYear = cal.get(GregorianCalendar.YEAR);
		currentMonth = nowMonth;
		currentYear = nowYear;
		
		String[] headers = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
		for (int i=0; i<7; i++){
			calendarTable.addColumn(headers[i]);
		}
		
		calendarT.getParent().setBackground(calendarT.getBackground());
        
		calendarT.getTableHeader().setResizingAllowed(false);
		calendarT.getTableHeader().setReorderingAllowed(false);

		calendarT.setColumnSelectionAllowed(true);
		calendarT.setRowSelectionAllowed(true);
		calendarT.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		calendarT.setRowHeight(38);
		calendarTable.setColumnCount(7);
		calendarTable.setRowCount(6);
		
		for (int i=nowYear; i<=nowYear+100; i++){
			yearCombo.addItem(String.valueOf(i));
		}
		reloadCalendar (nowMonth, nowYear);
	}
	
	public static void reloadCalendar(int month, int year){
		String[] months =  {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
		int numberOfDays, startingDay;

		previousButton.setEnabled(true);
		nextButton.setEnabled(true);
		if (month == 0 && year < nowYear++){
            previousButton.setEnabled(false);
        } 
		if (month == 11 && year >= nowYear+100){
            nextButton.setEnabled(false);
        } 
		monthLabel.setText(months[month]);
		monthLabel.setBounds(160-monthLabel.getPreferredSize().width/2, 25, 180, 25);
		yearCombo.setSelectedItem(String.valueOf(year));
		
		for (int i=0; i<6; i++){
			for (int j=0; j<7; j++){
				calendarTable.setValueAt(null, i, j);
			}
		}
		
		GregorianCalendar cal = new GregorianCalendar(year, month, 1);
		numberOfDays = cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
		startingDay = cal.get(GregorianCalendar.DAY_OF_WEEK);
		
		for (int i=1; i<=numberOfDays; i++){
			int r = (i+startingDay-2)/7;
			int c = (i+startingDay-2)%7;
			calendarTable.setValueAt(i, r, c);
		}

		calendarT.setDefaultRenderer(calendarT.getColumnClass(0), new calendarRenderer());
	}

	static class calendarRenderer extends DefaultTableCellRenderer{
		public Component getTableCellRendererComponent (JTable table, Object value, boolean selected, boolean focused, int row, int column){
			super.getTableCellRendererComponent(table, value, selected, focused, row, column);
			if (column == 0 || column == 6){
				setBackground(new Color(176, 196, 222));
			}
			else{
				setBackground(new Color(255, 255, 255));
			}
			if (value != null){
				if (Integer.parseInt(value.toString()) == nowDay && currentMonth == nowMonth && currentYear == nowYear){ 
					setBackground(new Color(188, 143, 143));
				}
			}
			setBorder(null);
			setForeground(Color.black);
			return this;  
		}
	}

	static class previousButton_Action implements ActionListener{
		public void actionPerformed (ActionEvent e){
			if (currentMonth == 0){ 
				currentMonth = 11;
				currentYear -= 1;
			}
			else{
				currentMonth -= 1;
			}
			reloadCalendar(currentMonth, currentYear);
		}
	}
    
	static class nextButton_Action implements ActionListener{
		public void actionPerformed (ActionEvent e){
			if (currentMonth == 11){
				currentMonth = 0;
				currentYear += 1;
			}
			else{
				currentMonth += 1;
			}
			reloadCalendar(currentMonth, currentYear);
		}
	}
    
	static class yearCombo_Action implements ActionListener{
		public void actionPerformed (ActionEvent e){
			if (yearCombo.getSelectedItem() != null){
				String b = yearCombo.getSelectedItem().toString();
				currentYear = Integer.parseInt(b);
				reloadCalendar(currentMonth, currentYear);
			}
		}
	}
}