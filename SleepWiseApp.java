import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.util.*;
import com.formdev.flatlaf.FlatLightLaf;

public class SleepWiseApp extends JFrame {
    private JTextField sleepTimeField;
    private JTextField wakeTimeField;
    private JComboBox<String> wakeFeelingDropdown, screenUseDropdown, caffeineUseDropdown, exerciseDropdown, bedtimeConsistencyDropdown;
    private JTextArea chatbotArea;
    private JButton submitButton;
    private JButton infoButton;

    private ArrayList<Double> sleepDurations = new ArrayList<>();
    private ImageIcon logoIcon;
    private Image logoImage;

    public SleepWiseApp() {
        setTitle("SleepWise");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(760, 880);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        Color background = Color.decode("#E6F0F3");
        Color primary = Color.decode("#2E7DAB");
        Color accent = Color.decode("#A3D5E0");
        Color fontColor = Color.decode("#1B1B1B");
        Color white = Color.white;

        Font font = new Font("Segoe UI", Font.PLAIN, 15);
        Font fontBold = new Font("Segoe UI", Font.BOLD, 16);

        JPanel container = new JPanel();
        container.setLayout(new BorderLayout(10, 10));
        container.setBackground(background);
        container.setBorder(BorderFactory.createEmptyBorder(30, 30, 0, 30));

        JPanel inputPanel = new JPanel(new GridBagLayout());
        inputPanel.setBackground(white);
        inputPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(primary, 2), "Sleep Input", 0, 0, fontBold, primary));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(6, 6, 6, 6);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        JLabel[] labels = new JLabel[]{
            new JLabel("Sleep Time (HH:MM):"),
            new JLabel("Wake Time (HH:MM):"),
            new JLabel("How did you feel after waking up?"),
            new JLabel("Used screens before bed?"),
            new JLabel("Drank caffeine after 4 PM?"),
            new JLabel("Exercised within 2 hours of bed?"),
            new JLabel("Was bedtime consistent this week?")
        };

        Component[] inputs = new Component[]{
            sleepTimeField = new JTextField(),
            wakeTimeField = new JTextField(),
            wakeFeelingDropdown = createStyledDropdown(new String[]{"Choose an option", "Refreshed", "Okay", "Tired", "Very Tired"}, font),
            screenUseDropdown = createStyledDropdown(new String[]{"Choose an option", "No", "Yes, under 30 mins", "Yes, over 30 mins"}, font),
            caffeineUseDropdown = createStyledDropdown(new String[]{"Choose an option", "No", "Yes"}, font),
            exerciseDropdown = createStyledDropdown(new String[]{"Choose an option", "No", "Yes"}, font),
            bedtimeConsistencyDropdown = createStyledDropdown(new String[]{"Choose an option", "Yes", "No"}, font)
        };

        for (int i = 0; i < labels.length; i++) {
            labels[i].setFont(fontBold);
            labels[i].setForeground(primary);
            gbc.gridx = 0;
            gbc.gridy = i;
            inputPanel.add(labels[i], gbc);

            gbc.gridx = 1;
            gbc.weightx = 1;
            inputPanel.add(inputs[i], gbc);
        }

        container.add(inputPanel, BorderLayout.NORTH);

        chatbotArea = new JTextArea();
        chatbotArea.setFont(font);
        chatbotArea.setEditable(false);
        chatbotArea.setLineWrap(true);
        chatbotArea.setWrapStyleWord(true);
        chatbotArea.setBackground(white);
        chatbotArea.setForeground(fontColor);
        chatbotArea.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(primary, 2), "Sleep Coach Feedback", 0, 0, fontBold, primary));

        JScrollPane scrollPane = new JScrollPane(chatbotArea);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        scrollPane.setPreferredSize(new Dimension(720, 240));
        container.add(scrollPane, BorderLayout.CENTER);

        submitButton = new JButton("Submit Sleep Log");
        submitButton.setFont(fontBold);
        submitButton.setForeground(Color.white);
        submitButton.setBackground(primary);
        submitButton.setFocusPainted(false);
        submitButton.setBorder(BorderFactory.createEmptyBorder(10, 30, 10, 30));
        submitButton.setCursor(new Cursor(Cursor.HAND_CURSOR));

        submitButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                submitButton.setBackground(accent);
            }
            public void mouseExited(java.awt.event.MouseEvent evt) {
                submitButton.setBackground(primary);
            }
        });

        submitButton.addActionListener(e -> evaluateSleep());

        logoImage = new ImageIcon("assets/logo.png").getImage().getScaledInstance(40, 40, Image.SCALE_SMOOTH);
        logoIcon = new ImageIcon(logoImage);
        infoButton = new JButton(logoIcon);
        infoButton.setPreferredSize(new Dimension(50, 50));
        infoButton.setBorder(BorderFactory.createEmptyBorder());
        infoButton.setContentAreaFilled(false);
        infoButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        infoButton.setToolTipText("About SleepWise");

        infoButton.addActionListener(e -> showInfoDialog());
        infoButton.addMouseListener(new MouseAdapter() {
            public void mouseEntered(MouseEvent e) {
                infoButton.setIcon(new ImageIcon(logoImage.getScaledInstance(50, 50, Image.SCALE_SMOOTH)));
            }
            public void mouseExited(MouseEvent e) {
                infoButton.setIcon(logoIcon);
            }
        });

        JPanel bottomPanel = new JPanel();
        bottomPanel.setBackground(background);
        bottomPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        bottomPanel.add(submitButton);
        bottomPanel.add(Box.createHorizontalStrut(10));
        bottomPanel.add(infoButton);

        add(container, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void showInfoDialog() {
        String info = "SleepWise is a guided sleep log and coaching tool built for students and anyone looking to improve sleep hygiene.\n\n"
            + "Who it's for:\n"
            + "- Students with inconsistent sleep schedules\n"
            + "- Individuals trying to build better pre-sleep habits\n\n"
            + "How to use:\n"
            + "1. Enter your sleep and wake time.\n"
            + "2. Answer questions about your habits and how you felt.\n"
            + "3. Click 'Submit' to receive personalized suggestions.\n\n"
            + "SleepWise helps you track patterns and improve the quality and consistency of your rest.";

        JOptionPane.showMessageDialog(this, info, "About SleepWise", JOptionPane.INFORMATION_MESSAGE);
    }

    private JComboBox<String> createStyledDropdown(String[] options, Font font) {
        JComboBox<String> box = new JComboBox<>(options);
        box.setFont(font);
        box.setBackground(Color.white);
        box.setForeground(Color.decode("#1B1B1B"));
        box.setBorder(BorderFactory.createLineBorder(Color.decode("#CCCCCC")));
        return box;
    }

    private void evaluateSleep() {
        try {
            if (wakeFeelingDropdown.getSelectedIndex() == 0 || screenUseDropdown.getSelectedIndex() == 0 ||
                caffeineUseDropdown.getSelectedIndex() == 0 || exerciseDropdown.getSelectedIndex() == 0 ||
                bedtimeConsistencyDropdown.getSelectedIndex() == 0) {
                JOptionPane.showMessageDialog(this, "Please answer all dropdown questions before submitting.", "Incomplete Input", JOptionPane.WARNING_MESSAGE);
                return;
            }

            LocalTime sleepTime = LocalTime.parse(sleepTimeField.getText().trim());
            LocalTime wakeTime = LocalTime.parse(wakeTimeField.getText().trim());
            Duration duration = Duration.between(sleepTime, wakeTime);
            if (duration.isNegative()) duration = duration.plusHours(24);
            double hoursSlept = duration.toMinutes() / 60.0;
            sleepDurations.add(hoursSlept);

            String feeling = (String) wakeFeelingDropdown.getSelectedItem();
            String screens = (String) screenUseDropdown.getSelectedItem();
            String caffeine = (String) caffeineUseDropdown.getSelectedItem();
            String exercise = (String) exerciseDropdown.getSelectedItem();
            String consistency = (String) bedtimeConsistencyDropdown.getSelectedItem();

            int score = 0;
            if (hoursSlept >= 8) score += 2;
            else if (hoursSlept >= 7) score += 1;
            if ("Refreshed".equals(feeling)) score += 2;
            else if ("Okay".equals(feeling)) score += 1;
            if ("No".equals(caffeine)) score += 1;
            if ("No".equals(exercise)) score += 1;
            if ("Yes".equals(consistency)) score += 1;
            if ("No".equals(screens)) score += 1;
            else if ("under 30 mins".equals(screens)) score += 0;
            else score -= 1;

            StringBuilder chat = new StringBuilder();
            chat.append("\uD83D\uDC4B Hello! Let's review your sleep...\n\n");
            chat.append("You slept for " + String.format("%.1f", hoursSlept) + " hours and felt \"" + feeling + "\" after waking up.\n");

            if (score >= 7) {
                chat.append("Great job! Your sleep habits look strong.\n");
            } else if (score >= 5) {
                chat.append("You're doing okay, but there's room for improvement.\n");
            } else {
                chat.append("Let's work on your sleep. Here are some tips!\n");
            }

            chat.append("\n✅ Suggested checklist for tonight:\n");
            if (!"No".equals(screens)) chat.append("- Avoid screens at least 1 hour before bed\n");
            if (!"No".equals(caffeine)) chat.append("- Skip caffeine after 4 PM\n");
            if (!"No".equals(exercise)) chat.append("- Avoid working out close to bedtime\n");
            if ("No".equals(consistency)) chat.append("- Try to stick to a consistent bedtime\n");
            if (hoursSlept < 7.5) chat.append("- Aim for at least 8 hours of sleep tonight\n");
            chat.append("- Consider breathing exercises or reading before sleep\n");

            chatbotArea.setText(chat.toString());

        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Invalid input. Please check your time format.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatLightLaf());
        } catch (Exception e) {
            System.err.println("Failed to initialize FlatLaf.");
        }
        SwingUtilities.invokeLater(() -> new SleepWiseApp().setVisible(true));
    }
}