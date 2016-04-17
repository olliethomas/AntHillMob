/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import UI.EnumHolder.ListMode;
import antgame.Cell;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JViewport;
import javax.swing.border.EmptyBorder;

public class GameView extends JFrame {

    JPanel mainPanel;
    JPanel mapContent;
    JFileChooser fileBrowser;
    GUIFactory factory;
    JPanel playerListPanel;
    JPanel worldListPanel;
    boolean tournament; //test to check adaptability with single player

    public GameView() {
        super("Setup the game");

        //game panel
        factory = new GUIFactory();
        fileBrowser = factory.createFileBrowser();

        mainPanel = new JPanel(new BorderLayout(5, 5));
        mainPanel.add(createStartPanel());
        
        
        /*
         mapContent = new JPanel(new BorderLayout(5,5)); //5,5 gap between panels
         mapContent.setPreferredSize(new Dimension( 1200,700)); //x,y
          
         mainPanel.add(createSidePanel(), BorderLayout.WEST);
         mainPanel.add(mapContent, BorderLayout.CENTER);
                
         */

        getContentPane().add(mainPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setMinimumSize(new Dimension(250, 150));
        pack();
        setVisible(true);

    }

    // Start Menu Panel
    public JPanel createStartPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 0, 5, 20));
        panel.setBorder(new EmptyBorder(20, 5, 20, 5));

        JButton button1 = new JButton("Start game");
        button1.addActionListener(new ActionListener() { //button 1

            @Override
            public void actionPerformed(ActionEvent e) {
                tournament = false;
                resetMainPanel();
                createSelectionPanel();
            }

        });
        JButton button2 = new JButton("Start tournament");
        button2.addActionListener(new ActionListener() { //button 2

            @Override
            public void actionPerformed(ActionEvent e) {
                tournament = true;
                resetMainPanel();
                createSelectionPanel();
            }
        });
        panel.add(button1);
        panel.add(button2);

        return panel;
    }

    // Player and World selection Panel
    public void createSelectionPanel() {

        mainPanel.add(factory.createLabel("Input players", 25, true), BorderLayout.NORTH);
        mainPanel.add(createPlayerList(), BorderLayout.CENTER);
        mainPanel.add(createWorldSelectionPanel(), BorderLayout.SOUTH);
        setMinimumSize(new Dimension(400, 500));
        refreshUI();
    }

    private JPanel createWorldSelectionPanel() {

        JPanel panel = new JPanel(new BorderLayout());

        panel.add(factory.createLabel("Select World", 25, true), BorderLayout.NORTH);
        panel.add(createWorldList(), BorderLayout.CENTER);
        panel.add(createTournamentStartButton(), BorderLayout.SOUTH);

        return panel;

    }

    private JPanel createPlayerList() {

        JPanel panel = new JPanel();

        playerListPanel = new JPanel();
        factory.setVerticalBoxLayout(playerListPanel); //box layout

        //check whether we are setting up a tournament or single game
        if (tournament) {
            playerListPanel.add(createAddButton(ListMode.PLAYER));
        } else {
            playerListPanel.add(createInputPanel(ListMode.PLAYER));
            playerListPanel.add(createInputPanel(ListMode.PLAYER));
        }

        JScrollPane pane = new JScrollPane(playerListPanel);
        pane.setPreferredSize(new Dimension(350, 150));
        panel.add(pane);

        return panel;

    }

    private JPanel createWorldList() {

        JPanel panel = new JPanel();

        worldListPanel = new JPanel();
        factory.setVerticalBoxLayout(worldListPanel);

        if (tournament) {
            worldListPanel.add(createAddButton(ListMode.WORLD));
        } else {
            worldListPanel.add(createInputPanel(ListMode.WORLD));
        }

        JScrollPane pane = new JScrollPane(worldListPanel);
        pane.setPreferredSize(new Dimension(350, 150));
        panel.add(pane);

        return panel;
    }

    private JButton createAddButton(final ListMode mode) {

        final JButton addButton = new JButton("+");
        addButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                JPanel parent = (JPanel) addButton.getParent();
                if (mode == ListMode.PLAYER) {
                    parent.add(createInputPanel(mode), parent.getComponentCount() - 1);
                } else {
                    parent.add(createInputPanel(mode), parent.getComponentCount() - 1);
                }
                parent.revalidate();
                parent.repaint();
            }
        });
        addButton.setAlignmentX(Component.CENTER_ALIGNMENT);

        return addButton;
    }

    private JPanel createInputPanel(final ListMode mode) {

        final JPanel panel = new JPanel();
        final JButton fileButton;

        if (mode == ListMode.PLAYER) {
            //layout for player panel
            panel.setLayout(new GridLayout(0, 2, 10, 5));
            JTextField playerName = new JTextField();
            fileButton = new JButton("Load Brain");
            panel.add(playerName);
        } else {
            //layout for list panel
            factory.setVerticalBoxLayout(panel);
            fileButton = new JButton("Load World");
            fileButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        }

        panel.add(fileButton);
        panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        panel.setMaximumSize(new Dimension(250, 35));

        fileButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                int returnVal = fileBrowser.showOpenDialog(panel);
                if (returnVal == JFileChooser.APPROVE_OPTION) {
                    File file = fileBrowser.getSelectedFile();

                    markInputPanel(file.getPath(), panel, mode);
                    System.out.println("Opening: " + file.getPath());
                } else {
                    System.out.println("Open command cancelled by user.");
                }
            }
        });
        return panel;
    }

    private void markInputPanel(String filepath, final JPanel parent, final ListMode mode) {

        JPanel panel = new JPanel(new GridLayout(0, 2, 10, 5));
        panel.setPreferredSize(new Dimension(150, 35));

        int removeIndex;
        if (mode == ListMode.PLAYER) {
            removeIndex = 1;
            panel.setBorder(new EmptyBorder(5, 5, 5, 5));
        } else {
            removeIndex = 0;
        }

        JButton deleteButton = new JButton("X");
        deleteButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JPanel mainParent = (JPanel) parent.getParent();

                //if single game format, handle panels differently
                if (!tournament) {
                    int index = 1;
                    if (mainParent.getComponent(0) instanceof JPanel && (JPanel) mainParent.getComponent(0) == parent) { //determine which box is being deleted for player
                        index = 0;
                    }
                    mainParent.remove(parent);
                    if (mode == ListMode.PLAYER) {
                        mainParent.add(createInputPanel(mode), index);
                    } else {
                        mainParent.add(createInputPanel(mode), 0);
                    }
                } else {
                    mainParent.remove(parent);
                }
                refreshPanel(mainParent);
            }
        });
        panel.add(factory.createLabel(filepath, false));
        panel.add(deleteButton);
        parent.remove(removeIndex);
        parent.add(panel, removeIndex);

        refreshPanel(panel);
    }

    private JPanel createTournamentStartButton() {

        String buttonText;
        final int playerReq;

        if (tournament) {
            buttonText = "Start Tournament";
            playerReq = 3;
        } else {
            buttonText = "Start Game";
            playerReq = 2;
        }

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JButton button = new JButton(buttonText); // add functionality

        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (checkPlayerList() && checkWorldList()) {
                    //create tournament
                    //next screen
                    //add check for file format here, can't do it in file chooser I think
                    resetMainPanel();
                    if (tournament) {
                        createTournamentPanel();
                    } else {
                        createPlaceholderPanel();
                    }

                } else {
                    if (playerListPanel.getComponents().length < playerReq) {
                        warningMessage("Need at least 2 players to start a tournament!");
                    } else {
                        warningMessage("Incorrect player and/or world information!");
                    }
                }
            }
        });
        panel.add(button);

        return panel;
    }

    //Tournament Menu Panel
    private void createTournamentPanel() {

        JPanel panel = new JPanel();
        factory.setHorizontalBoxLayout(panel);

        JPanel leftSide = new JPanel();
        factory.setVerticalBoxLayout(leftSide);
        leftSide.add(factory.createLabel("Leaderboard", 25, true));
        leftSide.add(createLeaderboardTable());

        JPanel rightSide = new JPanel();
        factory.setVerticalBoxLayout(rightSide);
        rightSide.setBorder(new EmptyBorder(0, 10, 0, 20));
        rightSide.add(factory.createLabel("Current Match", 25, true));
        rightSide.add(factory.createColourLabel("In progress", 16, true, Color.RED));
        rightSide.add(Box.createRigidArea(new Dimension(0, 15)));
        rightSide.add(createCurrentMatchPanel());
        rightSide.add(Box.createRigidArea(new Dimension(0, 40)));
        rightSide.add(createTournamentButtonsPanel());
        rightSide.add(Box.createVerticalGlue());

        panel.add(leftSide);
        panel.add(rightSide);

        mainPanel.add(panel);
        setMinimumSize(new Dimension(560, 350));
        refreshUI();
    }

    private JPanel createLeaderboardTable() {

        JPanel panel = new JPanel();
        String[] columnNames = {"Ranking", "Player Name", "Score"};
        String[][] sampleData = {{"1", "Player1", "20"}, {"2", "Player2", "10"}, {"3", "Player3", "5"}, {"1", "Player1", "20"}, {"2", "Player2", "10"}, {"3", "Player3", "5"},
        {"1", "Player1", "20"}, {"2", "Player2", "10"}, {"3", "Player3", "5"}, {"1", "Player1", "20"}, {"2", "Player2", "10"}, {"3", "Player3", "5"},
        {"1", "Player1", "20"}, {"2", "Player2", "10"}, {"3", "Player3", "5"}, {"1", "Player1", "20"}, {"2", "Player2", "10"}, {"3", "Player3", "5"},
        {"1", "Player1", "20"}, {"2", "Player2", "10"}, {"3", "Player3", "5"}, {"1", "Player1", "20"}, {"2", "Player2", "10"}, {"3", "Player3", "5"},
        {"1", "Player1", "20"}, {"2", "Player2", "10"}, {"3", "Player3", "5"}, {"1", "Player1", "20"}, {"2", "Player2", "10"}, {"3", "Player3", "5"},
        {"1", "Player1", "20"}, {"2", "Player2", "10"}, {"3", "Player3", "5"}, {"1", "Player1", "20"}, {"2", "Player2", "10"}, {"3", "Player3", "5"},
        {"1", "Player1", "20"}, {"2", "Player2", "10"}, {"3", "Player3", "5"}, {"1", "Player1", "20"}, {"2", "Player2", "10"}, {"3", "Player3", "5"}};
        JTable table = new JTable(sampleData, columnNames) {

            @Override
            public boolean isCellEditable(int row, int column) { //make all cells uneditable
                return false;
            }
        };
        table.setColumnSelectionAllowed(false);
        table.setRowSelectionAllowed(false);
        table.setCellSelectionEnabled(false);
        table.setShowHorizontalLines(false);
        table.getTableHeader().setReorderingAllowed(false);
        table.getTableHeader().setResizingAllowed(false);

        JScrollPane pane = new JScrollPane(table);
        pane.setPreferredSize(new Dimension(250, 250));
        panel.add(pane);

        return panel;
    }

    private JPanel createCurrentMatchPanel() {

        JPanel panel = new JPanel();
        factory.setVerticalBoxLayout(panel);

        panel.add(factory.createLabel("Map name", true));

        JPanel players = new JPanel();
        factory.setHorizontalBoxLayout(players);

        JPanel firstPlayer = new JPanel();
        factory.setVerticalBoxLayout(firstPlayer);
        firstPlayer.add(factory.createLabel("Player1", 20, true));
        firstPlayer.add(factory.createColourLabel("Red", 14, true, Color.RED));

        JPanel secondPlayer = new JPanel();
        factory.setVerticalBoxLayout(secondPlayer);
        secondPlayer.add(factory.createLabel("Player2", 20, true));
        secondPlayer.add(factory.createColourLabel("Black", 14, true, Color.BLACK));

        players.add(firstPlayer);
        players.add(Box.createHorizontalGlue());
        players.add(secondPlayer);

        panel.add(players);

        return panel;
    }

    private JPanel createTournamentButtonsPanel() {

        JPanel panel = new JPanel();
        factory.setVerticalBoxLayout(panel);
        //panel.setAlignmentX(Component.CENTER_ALIGNMENT);
        //panel.setBorder(new EmptyBorder(20,5,20,5));

        JButton button1 = new JButton("Watch game");
        //add action listener for buttons

        JButton button2 = new JButton("Next game");

        panel.add(button1);
        button1.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(Box.createRigidArea(new Dimension(0, 15)));
        panel.add(button2);
        button2.setAlignmentX(Component.CENTER_ALIGNMENT);

        return panel;
    }
    
    

    public void updateView(Cell[][] map) {
        //update score here, move score labels to main?
        mapContent.removeAll();

        HexagonMap testRect = new HexagonMap(map);
        //testRect.setPreferredSize(new Dimension(500,500));
        JScrollPane pane = new JScrollPane(testRect, JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
        pane.setPreferredSize(new Dimension(100, 100));
        pane.getVerticalScrollBar().setUnitIncrement(12);
        //pane.setWheelScrollingEnabled(false);
        JViewport port = pane.getViewport();
        port.setScrollMode(JViewport.SIMPLE_SCROLL_MODE); //use simple or backing for performance gains, backing takes more ram
        pane.setViewport(port);

        mapContent.add(pane);
        revalidate();
        repaint();
        pack();
    }
    
    

    // Game Window panels
    private JPanel createSidePanel() {

        //side panel for the game window
        JPanel panel = new JPanel(new BorderLayout(5, 5));
        panel.setPreferredSize(new Dimension(300, 900));
        panel.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, Color.BLACK));
        panel.setAlignmentX(Component.CENTER_ALIGNMENT);

        panel.add(createScorePanel(), BorderLayout.NORTH);
        if (tournament) {
            panel.add(createLeaderboardPanel(), BorderLayout.CENTER);
            panel.add(createScoreButtonPanel(), BorderLayout.SOUTH);
        } else {
            panel.add(createScoreButtonPanel(), BorderLayout.CENTER);
        }

        return panel;
    }

    private JPanel createScorePanel() {

        JPanel panel = new JPanel();
        factory.setVerticalBoxLayout(panel);

        JLabel p1Score = factory.createColourLabel("0", true, new Color(50, 205, 50)); //move to global?
        JLabel p2Score = factory.createColourLabel("0", true, new Color(50, 205, 50));

        panel.add(factory.createLabel("Current Game", 22, true));
        panel.add(factory.createBoxPadding(0, 20));
        panel.add(factory.createColourLabel("Player1", 16, true, Color.RED));
        panel.add(p1Score);
        panel.add(factory.createBoxPadding(0, 25));
        panel.add(factory.createLabel("Player2", 16, true));
        panel.add(p2Score);
        panel.add(factory.createBoxPadding(0, 20));
        panel.setBorder(factory.createBlackLine(0, 0, 1, 0));

        return panel;
    }

    private JPanel createLeaderboardPanel() {

        JPanel panel = new JPanel();
        factory.setVerticalBoxLayout(panel);

        panel.add(createLeaderboardTable());
        panel.setBorder(factory.createBlackLine(0, 0, 1, 0));

        return panel;
    }

    private JPanel createScoreButtonPanel() {

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 4, 1));
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        JButton button = new JButton("Fast-Forward");
        buttonPanel.add(button);

        return buttonPanel;
    }

    // Misc
    public void warningMessage(String s) {
        JOptionPane.showMessageDialog(null, s, "Warning",
                JOptionPane.WARNING_MESSAGE);
    }

    private boolean isValidName(JTextField field) {

        boolean valid = true;
        if (field.getText().trim().equals("") || field.getText().trim() == null) {
            valid = false;
        }
        return valid;
    }

    private boolean checkPlayerList() {

        boolean valid = true;
        int playerCount = 0;

        for (Component c : playerListPanel.getComponents()) {
            if (c instanceof JPanel) {
                JPanel panel = (JPanel) c;
                if (panel.getComponent(0) instanceof JTextField && isValidName((JTextField) panel.getComponent(0)) && panel.getComponent(1) instanceof JPanel) {
                    //valid, maybe process players here as pairs
                    playerCount++;
                    System.out.println("player check");
                } else {
                    valid = false;
                }
            }
        }
        if (playerCount < 2) {
            valid = false; //only add button?
        }
        return valid;
    }

    private boolean checkWorldList() {

        boolean valid = true;
        int worldCount = 0;

        for (Component c : worldListPanel.getComponents()) {
            if (c instanceof JPanel) {
                JPanel panel = (JPanel) c;
                if (panel.getComponent(0) instanceof JPanel) {
                    worldCount++;
                    System.out.println("player check");//valid, maybe process players here as pairs
                } else {
                    valid = false;
                }
            }
        }

        if (worldCount < 1) { //at least 1 world needed
            valid = false; //only add button?
        }
        return valid;

    }

    private void resetMainPanel() { //check this

        mainPanel.removeAll();

    }

    private void refreshUI() { //check this
        revalidate();
        repaint();
        pack();
    }

    private void createPlaceholderPanel() {
        JPanel panel = new JPanel();
        panel.add(factory.createLabel("Test", true));
        mainPanel.add(panel);
        refreshUI();

    }

    private void refreshPanel(JPanel panel) {

        panel.revalidate();
        panel.repaint();
    }

}