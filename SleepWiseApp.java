import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.*;
import java.util.*;

public class SleepWiseApp extends JFrame {
    private JTextField sleepTimeField;
    private JTextField wakeTimeField;
    private JTextArea historyArea;
    private JLabel sleepScoreLabel;
    private JCheckBox[] checklistItems;
    private JTextArea suggestionsArea;
    private JButton completeChecklistButton;
    private JComboBox<String> wakeFeelingDropdown;

    private ArrayList<Double> sleepDurations = new ArrayList<>();
    private ArrayList<String> userFeelings = new ArrayList<>();
    private boolean checklistCompletedToday = false;

    public SleepWiseApp() {
        setTitle("SleepWise – Smart Sleep Coach");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(700, 700);
        setLayout(new BorderLayout());

        // Top Panel: Input fields
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(4, 2));

        inputPanel.add(new JLabel("Sleep Time (HH:MM):"));
        sleepTimeField = new JTextField();
        inputPanel.add(sleepTimeField);

        inputPanel.add(new JLabel("Wake Time (HH:MM):"));
        wakeTimeField = new JTextField();
        inputPanel.add(wakeTimeField);

        inputPanel.add(new JLabel("How did you feel after waking up?"));
        wakeFeelingDropdown = new JComboBox<>(new String[]{"Refreshed", "Okay", "Tired", "Very Tired"});
        inputPanel.add(wakeFeelingDropdown);

        JButton logButton = new JButton("Log Sleep");
        inputPanel.add(logButton);

        sleepScoreLabel = new JLabel("Sleep Score: N/A");
        inputPanel.add(sleepScoreLabel);

        add(inputPanel, BorderLayout.NORTH);

        // Center Panel: History and Suggestions
        JPanel centerPanel = new JPanel(new GridLayout(1, 2));

        historyArea = new JTextArea();
        historyArea.setEditable(false);
        centerPanel.add(new JScrollPane(historyArea));

        suggestionsArea = new JTextArea();
        suggestionsArea.setEditable(false);
        centerPanel.add(new JScrollPane(suggestionsArea));

        add(centerPanel, BorderLayout.CENTER);

        // Bottom Panel: Checklist
        JPanel checklistPanel = new JPanel();
        checklistPanel.setLayout(new BoxLayout(checklistPanel, BoxLayout.Y_AXIS));
        checklistPanel.setBorder(BorderFactory.createTitledBorder("Wind-Down Checklist"));

        checklistItems = new JCheckBox[]{
                new JCheckBox("Dim your lights 1 hour before bed"),
                new JCheckBox("Avoid screens 30 minutes before sleeping"),
                new JCheckBox("Avoid caffeine after 4:00 PM"),
                new JCheckBox("Do 5 minutes of deep breathing"),
                new JCheckBox("Avoid intense exercise in the evening")
        };

        for (JCheckBox item : checklistItems) {
            checklistPanel.add(item);
        }

        completeChecklistButton = new JButton("Complete Checklist");
        completeChecklistButton.addActionListener(e -> completeChecklist());
        checklistPanel.add(completeChecklistButton);

        add(checklistPanel, BorderLayout.SOUTH);

        logButton.addActionListener(e -> logSleep());

        updateSuggestions();
    }

    private void logSleep() {
        try {
            String sleepInput = sleepTimeField.getText().trim();
            String wakeInput = wakeTimeField.getText().trim();

            LocalTime sleepTime = LocalTime.parse(sleepInput);
            LocalTime wakeTime = LocalTime.parse(wakeInput);

            Duration duration = Duration.between(sleepTime, wakeTime);
            if (duration.isNegative()) duration = duration.plusHours(24);

            double hours = duration.toMinutes() / 60.0;
            sleepDurations.add(hours);
            String feeling = (String) wakeFeelingDropdown.getSelectedItem();
            userFeelings.add(feeling);

            historyArea.append("Slept for: " + String.format("%.2f", hours) + " hours, Felt: " + feeling + "\n");
            checklistCompletedToday = false;
            updateSleepScore();
            updateSuggestions();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Please enter times in HH:MM format.", "Input Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void updateSleepScore() {
        double avg = sleepDurations.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        int stars = (int) Math.min(5, Math.round(avg / 1.5));

        String lastFeeling = userFeelings.get(userFeelings.size() - 1);
        if ("Refreshed".equals(lastFeeling)) stars++;
        if ("Very Tired".equals(lastFeeling)) stars--;

        if (checklistCompletedToday) stars++;
        stars = Math.max(1, Math.min(5, stars));

        StringBuilder starRating = new StringBuilder();
        for (int i = 0; i < stars; i++) starRating.append("★");
        for (int i = stars; i < 5; i++) starRating.append("☆");

        sleepScoreLabel.setText("Sleep Score: " + starRating);
    }

    private void updateSuggestions() {
        double avg = sleepDurations.stream().mapToDouble(Double::doubleValue).average().orElse(0);
        String lastFeeling = userFeelings.isEmpty() ? "Okay" : userFeelings.get(userFeelings.size() - 1);

        StringBuilder tips = new StringBuilder("Tonight's Suggestions:\n");

        if (avg < 7.0) tips.append("• Try going to bed 30 minutes earlier\n");
        if ("Tired".equals(lastFeeling) || "Very Tired".equals(lastFeeling)) tips.append("• Avoid screens 1 hour before bed\n");
        if (!checklistCompletedToday) tips.append("• Complete your wind-down checklist\n");
        tips.append("• Maintain a consistent sleep/wake schedule\n");
        tips.append("• Do 5 minutes of deep breathing before bed\n");

        suggestionsArea.setText(tips.toString());
    }

    private void completeChecklist() {
        boolean allChecked = Arrays.stream(checklistItems).allMatch(JCheckBox::isSelected);
        if (allChecked) {
            checklistCompletedToday = true;
            JOptionPane.showMessageDialog(this, "Checklist completed successfully!");
            updateSleepScore();
            updateSuggestions();
        } else {
            JOptionPane.showMessageDialog(this, "Please complete all checklist items before submitting.");
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new SleepWiseApp().setVisible(true));
    }
}