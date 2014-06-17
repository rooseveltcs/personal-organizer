import javax.swing.*;
import javax.swing.event.*;
import javax.swing.table.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

/**
 * Calendar.java
 * Assignment: Final Project
 * Purpose: Creates a viewable calendar that 
 * contains a range of 200 years.
 *  
 * @version 06/17/2014
 * @author David Chou
 */

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
		
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } // Will use default look and feel and catch the necessary exceptions
		catch (ClassNotFoundException e) {
        }
		catch (InstantiationException e) {
        }
		catch (IllegalAccessException e) {
        }
		catch (UnsupportedLookAndFeelException e) {
        }
        
        //Creates the frame
		mainFrame = new JFrame ("Personal Organizer");
		mainFrame.setSize(326, 365); 
		pane = mainFrame.getContentPane();
		pane.setLayout(null);
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Initializes the components
		monthLabel = new JLabel ("January");
		yearLabel = new JLabel ("Current Year:");
		yearCombo = new JComboBox();
		previousButton = new JButton ("<");
		nextButton = new JButton (">");
		// Makes the calendar uneditable
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
        
        // Adds the components to the panel
		pane.add(calendarPanel);
		calendarPanel.add(monthLabel);
		calendarPanel.add(yearLabel);
		calendarPanel.add(yearCombo);
		calendarPanel.add(previousButton);
		calendarPanel.add(nextButton);
		calendarPanel.add(calendarScroll);

        // Positions the components of the application
		calendarPanel.setBounds(0, 5, 320, 335);
		monthLabel.setBounds(160 - monthLabel.getPreferredSize().width / 2, 25, 100, 25);
		yearLabel.setBounds(160, 307, 80, 20);
		yearCombo.setBounds(230, 307, 80, 20);
		previousButton.setBounds(10, 25, 50, 25);
		nextButton.setBounds(260, 25, 50, 25);
		calendarScroll.setBounds(10, 55, 300, 251);

		mainFrame.setResizable(false);
		mainFrame.setVisible(true);
		
        // Retrieves the data from the current time in the default time zone with the default locale
		GregorianCalendar cal = new GregorianCalendar();
		nowDay = cal.get(GregorianCalendar.DAY_OF_MONTH); 
		nowMonth = cal.get(GregorianCalendar.MONTH);
		nowYear = cal.get(GregorianCalendar.YEAR);
        
		currentMonth = nowMonth;
		currentYear = nowYear;
        
        // Sets the headers of the table
		String[] headers = {"Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat"};
		for (int i=0; i<7; i++){
			calendarTable.addColumn(headers[i]);
		}
		
		calendarT.getParent().setBackground(calendarT.getBackground());
		calendarT.getTableHeader().setResizingAllowed(false);
		calendarT.getTableHeader().setReorderingAllowed(false);

		// Sets the table properties
        calendarT.setColumnSelectionAllowed(true);
		calendarT.setRowSelectionAllowed(true);
		calendarT.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		calendarT.setRowHeight(38);
		calendarTable.setColumnCount(7);
		calendarTable.setRowCount(6);
		
        // Creates the years inside the ComboBox
		for (int i = nowYear-100; i <= nowYear+100; i++){
			yearCombo.addItem(String.valueOf(i));
		}
        
        // Reloads the calendar based on the current time
		reloadCalendar (nowMonth, nowYear);
	}
    /**
     * Reloads the calendar based on the passed in month and year. 
     * First it loads the corresponding string to the month,
     * then it checks the status of the buttons. After loading the
     * years in the ComboBox, the method positions the components 
     * and then clear the values of the table then setting them to 
     * the correct date.
	 *
     * @param  month	an absolute URL giving the base location of the image
     * @param  year		the location of the image, relative to the url argument
     */
    
	public static void reloadCalendar(int month, int year){
		String[] months =  {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
		
        // Declares the number of days and the starting day of the current month
        int numberOfDays;
        int startingDay;
        
        // Checks if button should be available
		previousButton.setEnabled(true);
		nextButton.setEnabled(true);
        // If the year surpasses the limits, it sets the restriction
		if (month == 0 && year < nowYear - 99){
            previousButton.setEnabled(false);
        } 
		if (month == 11 && year >= nowYear + 100){
            nextButton.setEnabled(false);
        }
        // Sets the non-static components when the calendar is refreshed 
		monthLabel.setText(months[month]);
		monthLabel.setBounds(160 - monthLabel.getPreferredSize().width / 2, 25, 180, 25);
		yearCombo.setSelectedItem(String.valueOf(year));
		
        // Clears the table of all values by setting them to null
		for (int i = 0; i < 6; i++){
			for (int j = 0; j < 7; j++){
				calendarTable.setValueAt(null, i, j);
			}
		}
		
        // Retrieves the data for the current month and year on day 1
		GregorianCalendar cal = new GregorianCalendar(year, month, 1);
		numberOfDays = cal.getActualMaximum(GregorianCalendar.DAY_OF_MONTH);
		startingDay = cal.get(GregorianCalendar.DAY_OF_WEEK);
		
        // Sets the value of the table in the calendar as the days
		for (int i = 1; i <= numberOfDays; i++){
			int r = (i + startingDay - 2) / 7;
			int c = (i + startingDay - 2) % 7;
			calendarTable.setValueAt(i, r, c);
		}
        
		calendarT.setDefaultRenderer(calendarT.getColumnClass(0), new calendarRenderer());
	}

	static class calendarRenderer extends DefaultTableCellRenderer{
		// Overrides the getTableCellRendererComponent method by calling the
		// method through super then adding to it.
		public Component getTableCellRendererComponent (JTable table, Object o, boolean a, boolean b, int r, int c){
			super.getTableCellRendererComponent(table, o, a, b, r, c);
			// Changes the color depending on if it's a week day or the week ends
            if (c == 0 || c == 6){
				setBackground(new Color(176, 196, 222));
			}
			else{
				setBackground(new Color(255, 255, 255));
			}
         // Finds the current day and then sets the square as a different color to indicate the day
			if (o != null){
				if (Integer.parseInt(o.toString()) == nowDay && currentMonth == nowMonth && currentYear == nowYear){ 
					setBackground(new Color(188, 143, 143));
				}
			}
			setBorder(null);
			setForeground(Color.black);
			return this;
		}
	}

    //Listens for the previous button and checks to reload the calendar
	static class previousButton_Action implements ActionListener{
		public void actionPerformed (ActionEvent e){
		   // Subtracts the year if it goes past the start of the year
         if (currentMonth == 0){ 
				currentMonth = 11;
				currentYear -= 1;
			} // If it is not trying to pass boundaries, just subtract one
			else{
				currentMonth -= 1;
			}
         // Reloads the calendar when a button is pressed
			reloadCalendar(currentMonth, currentYear);
		}
	}
    
    // Listens for the next button and checks to reload the calendar
	static class nextButton_Action implements ActionListener{
		public void actionPerformed (ActionEvent e){
			// If month is going past the boundary then add a year
         if (currentMonth == 11){
				currentMonth = 0;
				currentYear += 1;
			}
			// Otherwise add one to month
			else{
				currentMonth += 1;
			}
			// Calls the refresh method
			reloadCalendar(currentMonth, currentYear);
		}
	}
    
    // Listens for the ComboBox for a change of year to reload the calendar
	static class yearCombo_Action implements ActionListener{
		public void actionPerformed (ActionEvent e) {
         if(yearCombo.getSelectedItem() != null){
				currentYear = Integer.parseInt(yearCombo.getSelectedItem().toString());
				reloadCalendar(currentMonth, currentYear);
			}
		}
	}
}