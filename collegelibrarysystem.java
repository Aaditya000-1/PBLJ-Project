import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class collegelibrarysystem extends JFrame {

    public collegelibrarysystem() {
        setTitle("college library system");
        setSize(600, 350);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout(FlowLayout.CENTER, 20, 30));

        // buttons
        JButton borrowbutton = new JButton("borrow book");
        JButton viewbutton = new JButton("view borrowed books");
        JButton returnbutton = new JButton("return book");
        JButton searchstudentbutton = new JButton("search student by uid");
        JButton exitbutton = new JButton("exit");

        add(borrowbutton);
        add(viewbutton);
        add(returnbutton);
        add(searchstudentbutton);
        add(exitbutton);

        borrowbutton.addActionListener(e -> openborrowdialog());
        viewbutton.addActionListener(e -> openviewdialog());
        returnbutton.addActionListener(e -> openreturndialog());
        searchstudentbutton.addActionListener(e -> openstudentsearchdialog());
        exitbutton.addActionListener(e -> System.exit(0));

        setVisible(true);
        createtable(); // create db table
    }

    private void openborrowdialog() {
        JDialog dialog = new JDialog(this, "borrow book", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 10, 5, 10);

        JTextField namefield = new JTextField(20);
        JTextField uidfield = new JTextField(20);
        JTextField bookfield = new JTextField(20);
        JComboBox<String> librarybox = new JComboBox<>(new String[]{"b1", "c3", "d6", "b2"});
        JButton submitbutton = new JButton("submit");

        gbc.gridx = 0; gbc.gridy = 0;
        dialog.add(new JLabel("student name:"), gbc);
        gbc.gridx = 1;
        dialog.add(namefield, gbc);

        gbc.gridx = 0; gbc.gridy = 1;
        dialog.add(new JLabel("uid:"), gbc);
        gbc.gridx = 1;
        dialog.add(uidfield, gbc);

        gbc.gridx = 0; gbc.gridy = 2;
        dialog.add(new JLabel("book title:"), gbc);
        gbc.gridx = 1;
        dialog.add(bookfield, gbc);

        gbc.gridx = 0; gbc.gridy = 3;
        dialog.add(new JLabel("library:"), gbc);
        gbc.gridx = 1;
        dialog.add(librarybox, gbc);

        gbc.gridx = 1; gbc.gridy = 4;
        dialog.add(submitbutton, gbc);

        submitbutton.addActionListener(e -> {
            String name = namefield.getText().trim();
            String uid = uidfield.getText().trim();
            String book = bookfield.getText().trim();
            String library = (String) librarybox.getSelectedItem();

            if (name.isEmpty() || uid.isEmpty() || book.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "please fill in all fields.", "error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String sql = "insert into borrowed_books (name, uid, book_title, library, borrowed_at, return_date) values (?, ?, ?, ?, ?, ?)";
            try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                String borrowdate = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_MONTH, 14); // 2 weeks return
                String returndate = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());

                pstmt.setString(1, name);
                pstmt.setString(2, uid);
                pstmt.setString(3, book);
                pstmt.setString(4, library);
                pstmt.setString(5, borrowdate);
                pstmt.setString(6, returndate);
                pstmt.executeUpdate();

                JOptionPane.showMessageDialog(dialog, "✅ book borrowed successfully!");
                dialog.dispose();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "error: " + ex.getMessage());
            }
        });

        dialog.setVisible(true);
    }

    private void openviewdialog() {
        JDialog dialog = new JDialog(this, "view borrowed books", true);
        dialog.setSize(700, 400);
        dialog.setLocationRelativeTo(this);
        dialog.setLayout(new BorderLayout());

        JPanel searchpanel = new JPanel();
        JTextField searchfield = new JTextField(15);
        JButton searchbutton = new JButton("search by uid");
        searchpanel.add(new JLabel("search uid:"));
        searchpanel.add(searchfield);
        searchpanel.add(searchbutton);

        JTextArea displayarea = new JTextArea();
        displayarea.setEditable(false);
        JScrollPane scrollpane = new JScrollPane(displayarea);
        dialog.add(searchpanel, BorderLayout.NORTH);
        dialog.add(scrollpane, BorderLayout.CENTER);

        searchbutton.addActionListener(e -> {
            String uid = searchfield.getText().trim();
            if (uid.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "uid cannot be empty!", "error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String sql = "select * from borrowed_books where uid = ? order by borrowed_at desc";
            try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, uid);
                ResultSet rs = pstmt.executeQuery();

                StringBuilder sb = new StringBuilder();
                sb.append("id | name | uid | book title | library | borrowed date | return date\n");
                sb.append("-------------------------------------------------------------\n");
                while (rs.next()) {
                    sb.append(rs.getInt("id")).append(" | ")
                            .append(rs.getString("name")).append(" | ")
                            .append(rs.getString("uid")).append(" | ")
                            .append(rs.getString("book_title")).append(" | ")
                            .append(rs.getString("library")).append(" | ")
                            .append(rs.getString("borrowed_at")).append(" | ")
                            .append(rs.getString("return_date")).append("\n");
                }
                displayarea.setText(sb.toString());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(dialog, "error: " + ex.getMessage());
            }
        });

        dialog.setVisible(true);
    }

    private void openreturndialog() {
        String uid = JOptionPane.showInputDialog(this, "enter uid to search for borrowed books:");
        if (uid == null || uid.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "uid cannot be empty!", "error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ArrayList<String> borrowedbooks = new ArrayList<>();
        String sql = "select id, book_title from borrowed_books where uid = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uid);
            ResultSet rs = pstmt.executeQuery();
            while (rs.next()) {
                borrowedbooks.add(rs.getInt("id") + ": " + rs.getString("book_title"));
            }

            if (borrowedbooks.isEmpty()) {
                JOptionPane.showMessageDialog(this, "no borrowed books found for uid: " + uid, "no books", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            String[] bookarray = borrowedbooks.toArray(new String[0]);
            String selectedbook = (String) JOptionPane.showInputDialog(this,
                    "select the book to return:",
                    "return book",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    bookarray,
                    bookarray[0]);

            if (selectedbook != null) {
                int bookid = Integer.parseInt(selectedbook.split(":")[0]);
                String deletesql = "delete from borrowed_books where id = ?";
                try (Connection connDel = connect(); PreparedStatement pstmtDel = connDel.prepareStatement(deletesql)) {
                    pstmtDel.setInt(1, bookid);
                    pstmtDel.executeUpdate();
                    JOptionPane.showMessageDialog(this, "book returned successfully!");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "error while returning book: " + ex.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "error fetching borrowed books: " + ex.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void openstudentsearchdialog() {
        String uid = JOptionPane.showInputDialog(this, "enter uid to search:");
        if (uid == null || uid.trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "uid cannot be empty!", "error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sql = "select name, book_title, borrowed_at, return_date from borrowed_books where uid = ?";
        try (Connection conn = connect(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, uid);
            ResultSet rs = pstmt.executeQuery();

            StringBuilder result = new StringBuilder();
            boolean hasresults = false;
            while (rs.next()) {
                hasresults = true;
                result.append("name: ").append(rs.getString("name")).append("\n")
                      .append("book: ").append(rs.getString("book_title")).append("\n")
                      .append("borrowed on: ").append(rs.getString("borrowed_at")).append("\n")
                      .append("return by: ").append(rs.getString("return_date")).append("\n")
                      .append("-----------------------------\n");
            }

            if (hasresults) {
                JTextArea textarea = new JTextArea(result.toString(), 15, 40);
                textarea.setEditable(false);
                JScrollPane scrollpane = new JScrollPane(textarea);
                JOptionPane.showMessageDialog(this, scrollpane, "student borrowed books", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "no borrowed books found for uid: " + uid, "not found", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "error fetching student data: " + ex.getMessage(), "error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private Connection connect() {
        try {
            Class.forName("org.sqlite.JDBC");
            return DriverManager.getConnection("jdbc:sqlite:college_libraries.db");
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "database connection error:\n" + e.getMessage());
            return null;
        }
    }

    private void createtable() {
        String sql = "create table if not exists borrowed_books (" +
                "id integer primary key autoincrement," +
                "name text not null," +
                "uid text not null," +
                "book_title text not null," +
                "library text not null," +
                "borrowed_at text," +
                "return_date text" +
                ");";
        try (Connection conn = connect(); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "failed to create table:\n" + e.getMessage());
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(collegelibrarysystem::new);
    }
}
