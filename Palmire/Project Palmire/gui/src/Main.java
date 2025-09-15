import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;

public class Main {
    // Color scheme
    private static final Color DARK_BLUE = new Color(10, 25, 73);
    private static final Color ORANGE = new Color(255, 140, 0);
    private static final Color BLACK = new Color(20, 20, 20);
    private static final Color WHITE = Color.WHITE;
    private static final Font TITLE_FONT = new Font("Segoe UI", Font.BOLD, 28);
    private static final Font SUBTITLE_FONT = new Font("Segoe UI", Font.BOLD, 20);
    private static final Font TEXT_FONT = new Font("Segoe UI", Font.PLAIN, 16);
    private static final String LOGIN_FILE = "login.txt";
    private static final String PHYSICAL_FILE = "physicalProducts.txt";
    private static final String DIGITAL_FILE = "digitalProducts.txt";
    private static final String WISHLIST_FILE = "wishList.txt";
    private static final String ORDER_HISTORY_FILE = "orderHistory.txt";
    private static final String IMAGE_DIR = "gui/src/images/";
    private static final String PLACEHOLDER_IMAGE = "placeholder.jpg";
    private static final int PRODUCT_IMG_WIDTH = 220;
    private static final int PRODUCT_IMG_HEIGHT = 160;

    // State
    private static String currentUser = null;
    private static String currentUserType = null; // admin, superadmin, or customer
    private static JFrame frame;
    private static JPanel mainPanel;
    private static CardLayout cardLayout;
    private static Map<String, Object> session = new HashMap<>();

    // Cart: Map<tag, quantity> where tag = "P-ProductName" or "D-ProductName"
    private static Map<String, Integer> cart = new LinkedHashMap<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Main::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        frame = new JFrame("Palmire E-Commerce");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(900, 600);
        frame.setLocationRelativeTo(null);
        frame.setResizable(false);

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        mainPanel.add(loginPanel(), "login");
        mainPanel.add(signupPanel(), "signup");
        mainPanel.add(mainMenuPanel(), "mainMenu");
        mainPanel.add(categoryPanel(), "category");
        mainPanel.add(cartPanel(), "cart");
        mainPanel.add(wishlistPanel(), "wishlist");
        mainPanel.add(orderHistoryPanel(), "orderHistory");
        // Admin panels will be added dynamically when needed

        frame.setContentPane(mainPanel);
        frame.setVisible(true);
    }

    // ---------------------- LOGIN PANEL ----------------------
    private static JPanel loginPanel() {
        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    Image bg = ImageIO.read(new File(IMAGE_DIR + "loginbg.jpg"));
                    g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    setBackground(DARK_BLUE);
                }
            }
        };
        bgPanel.setLayout(new GridBagLayout());

        JPanel overlay = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(20, 20, 20, 200)); // semi-transparent black
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
            }
        };
        overlay.setOpaque(false);
        overlay.setLayout(new GridBagLayout());
        overlay.setBorder(new EmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Palmire Login", JLabel.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(ORANGE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        overlay.add(title, gbc);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setFont(TEXT_FONT);
        emailLabel.setForeground(WHITE);
        gbc.gridy = 1; gbc.gridwidth = 1;
        overlay.add(emailLabel, gbc);

        JTextField emailField = new JTextField(20);
        gbc.gridx = 1;
        overlay.add(emailField, gbc);

        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(TEXT_FONT);
        passLabel.setForeground(WHITE);
        gbc.gridx = 0; gbc.gridy = 2;
        overlay.add(passLabel, gbc);

        JPasswordField passField = new JPasswordField(20);
        gbc.gridx = 1;
        overlay.add(passField, gbc);

        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(TEXT_FONT);
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        overlay.add(errorLabel, gbc);

        JButton loginBtn = new JButton("Login");
        styleButton(loginBtn, ORANGE);
        gbc.gridy = 4; gbc.gridwidth = 1; gbc.gridx = 0;
        overlay.add(loginBtn, gbc);

        JButton signupBtn = new JButton("Signup");
        styleButton(signupBtn, WHITE);
        signupBtn.setForeground(BLACK);
        gbc.gridx = 1;
        overlay.add(signupBtn, gbc);

        JButton exitBtn = new JButton("Exit");
        styleButton(exitBtn, BLACK);
        gbc.gridy = 5; gbc.gridx = 0; gbc.gridwidth = 2;
        overlay.add(exitBtn, gbc);

        loginBtn.addActionListener(e -> {
            String email = emailField.getText().trim();
            String password = new String(passField.getPassword());
            String[] result = authenticate(email, password);
            if (result != null) {
                currentUser = email;
                currentUserType = result[0];
                session.put("address", result.length > 1 ? result[1] : "");
                session.put("phone", result.length > 2 ? result[2] : "");
                errorLabel.setText(" ");
                
                // Redirect based on user type
                if (currentUserType.equals("superadmin")) {
                    // Create superadmin dashboard if not exists
                    if (Arrays.asList(mainPanel.getComponents()).stream().noneMatch(c -> c.getName() != null && c.getName().equals("superAdminDashboard"))) {
                        mainPanel.add(superAdminDashboardPanel(), "superAdminDashboard");
                    }
                    cardLayout.show(mainPanel, "superAdminDashboard");
                } else if (currentUserType.equals("admin")) {
                    // Create admin dashboard if not exists
                    if (Arrays.asList(mainPanel.getComponents()).stream().noneMatch(c -> c.getName() != null && c.getName().equals("adminDashboard"))) {
                        mainPanel.add(adminDashboardPanel(), "adminDashboard");
                    }
                    cardLayout.show(mainPanel, "adminDashboard");
                } else {
                    cardLayout.show(mainPanel, "mainMenu");
                }
            } else {
                errorLabel.setText("Invalid email or password.");
            }
        });
        
        signupBtn.addActionListener(e -> {
            cardLayout.show(mainPanel, "signup");
        });
        
        exitBtn.addActionListener(e -> System.exit(0));

        bgPanel.add(overlay);
        return bgPanel;
    }

    // ---------------------- SIGNUP PANEL ----------------------
    private static JPanel signupPanel() {
        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    Image bg = ImageIO.read(new File(IMAGE_DIR + "loginbg.jpg"));
                    g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    setBackground(DARK_BLUE);
                }
            }
        };
        bgPanel.setLayout(new GridBagLayout());

        JPanel overlay = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(20, 20, 20, 200)); // semi-transparent black
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
            }
        };
        overlay.setOpaque(false);
        overlay.setLayout(new GridBagLayout());
        overlay.setBorder(new EmptyBorder(30, 30, 30, 30));
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel title = new JLabel("Palmire Signup", JLabel.CENTER);
        title.setFont(TITLE_FONT);
        title.setForeground(ORANGE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        overlay.add(title, gbc);

        // User type selection
        JLabel typeLabel = new JLabel("Signup as:");
        typeLabel.setFont(TEXT_FONT);
        typeLabel.setForeground(WHITE);
        gbc.gridy = 1; gbc.gridwidth = 1;
        overlay.add(typeLabel, gbc);

        JComboBox<String> userTypeCombo = new JComboBox<>(new String[]{"Customer", "Admin"});
        userTypeCombo.setFont(TEXT_FONT);
        gbc.gridx = 1;
        overlay.add(userTypeCombo, gbc);

        // Email field
        JLabel emailLabel = new JLabel("Email (Gmail):");
        emailLabel.setFont(TEXT_FONT);
        emailLabel.setForeground(WHITE);
        gbc.gridx = 0; gbc.gridy = 2;
        overlay.add(emailLabel, gbc);

        JTextField emailField = new JTextField(20);
        gbc.gridx = 1;
        overlay.add(emailField, gbc);

        // Username field
        JLabel userLabel = new JLabel("Username:");
        userLabel.setFont(TEXT_FONT);
        userLabel.setForeground(WHITE);
        gbc.gridx = 0; gbc.gridy = 3;
        overlay.add(userLabel, gbc);

        JTextField userField = new JTextField(20);
        gbc.gridx = 1;
        overlay.add(userField, gbc);

        // Password field
        JLabel passLabel = new JLabel("Password:");
        passLabel.setFont(TEXT_FONT);
        passLabel.setForeground(WHITE);
        gbc.gridx = 0; gbc.gridy = 4;
        overlay.add(passLabel, gbc);

        JPasswordField passField = new JPasswordField(20);
        gbc.gridx = 1;
        overlay.add(passField, gbc);

        // Address field (for customers)
        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setFont(TEXT_FONT);
        addressLabel.setForeground(WHITE);
        gbc.gridx = 0; gbc.gridy = 5;
        overlay.add(addressLabel, gbc);

        JTextField addressField = new JTextField(20);
        gbc.gridx = 1;
        overlay.add(addressField, gbc);

        // Phone field (for customers)
        JLabel phoneLabel = new JLabel("Phone:");
        phoneLabel.setFont(TEXT_FONT);
        phoneLabel.setForeground(WHITE);
        gbc.gridx = 0; gbc.gridy = 6;
        overlay.add(phoneLabel, gbc);

        JTextField phoneField = new JTextField(20);
        gbc.gridx = 1;
        overlay.add(phoneField, gbc);

        // Role field (for admins)
        JLabel roleLabel = new JLabel("Role:");
        roleLabel.setFont(TEXT_FONT);
        roleLabel.setForeground(WHITE);
        gbc.gridx = 0; gbc.gridy = 7;
        overlay.add(roleLabel, gbc);

        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"admin", "superadmin"});
        roleCombo.setFont(TEXT_FONT);
        gbc.gridx = 1;
        overlay.add(roleCombo, gbc);

        // Error label
        JLabel errorLabel = new JLabel(" ");
        errorLabel.setForeground(Color.RED);
        errorLabel.setFont(TEXT_FONT);
        gbc.gridx = 0; gbc.gridy = 8; gbc.gridwidth = 2;
        overlay.add(errorLabel, gbc);

        // Buttons
        JButton signupBtn = new JButton("Signup");
        styleButton(signupBtn, ORANGE);
        gbc.gridy = 9; gbc.gridwidth = 1; gbc.gridx = 0;
        overlay.add(signupBtn, gbc);

        JButton backBtn = new JButton("Back to Login");
        styleButton(backBtn, BLACK);
        gbc.gridx = 1;
        overlay.add(backBtn, gbc);

        // Show/hide fields based on user type
        userTypeCombo.addActionListener(e -> {
            boolean isCustomer = userTypeCombo.getSelectedItem().equals("Customer");
            addressLabel.setVisible(isCustomer);
            addressField.setVisible(isCustomer);
            phoneLabel.setVisible(isCustomer);
            phoneField.setVisible(isCustomer);
            roleLabel.setVisible(!isCustomer);
            roleCombo.setVisible(!isCustomer);
        });

        // Initial state
        userTypeCombo.setSelectedItem("Customer");

        signupBtn.addActionListener(e -> {
            String userType = (String) userTypeCombo.getSelectedItem();
            String email = emailField.getText().trim();
            String username = userField.getText().trim();
            String password = new String(passField.getPassword());
            String address = addressField.getText().trim();
            String phone = phoneField.getText().trim();
            String role = (String) roleCombo.getSelectedItem();

            // Validation
            if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                errorLabel.setText("All fields are required.");
                return;
            }

            if (!email.toLowerCase().endsWith("@gmail.com")) {
                errorLabel.setText("Email must be a Gmail address.");
                return;
            }

            if (isEmailRegistered(email)) {
                errorLabel.setText("Email already registered.");
                return;
            }

            if (userType.equals("Customer") && (address.isEmpty() || phone.isEmpty())) {
                errorLabel.setText("Address and phone are required for customers.");
                return;
            }

            // Save to file
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(LOGIN_FILE, true))) {
                if (userType.equals("Customer")) {
                    String line = String.join(",", email, username, password, address, phone);
                    bw.write(line);
                    bw.newLine();
                } else {
                    String line = String.join(",", email, username, password, role);
                    bw.write(line);
                    bw.newLine();
                }
                errorLabel.setText("Signup successful! Please login.");
                errorLabel.setForeground(Color.GREEN);
                
                // Clear fields
                emailField.setText("");
                userField.setText("");
                passField.setText("");
                addressField.setText("");
                phoneField.setText("");
                
            } catch (IOException ex) {
                errorLabel.setText("Error saving account: " + ex.getMessage());
            }
        });

        backBtn.addActionListener(e -> {
            cardLayout.show(mainPanel, "login");
        });

        bgPanel.add(overlay);
        return bgPanel;
    }

    // ---------------------- ADMIN DASHBOARD PANEL ----------------------
    private static JPanel adminDashboardPanel() {
        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    Image bg = ImageIO.read(new File(IMAGE_DIR + "background.jpg"));
                    g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    setBackground(DARK_BLUE);
                }
            }
        };
        bgPanel.setLayout(new BorderLayout());

        // HEADER BAR
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 0, 0));
    
        JPanel navPanel = new JPanel();
        navPanel.setOpaque(false);
        navPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 24, 10));
        String[] navs = {"Dashboard", "Products", "Coupons", "Logout"};
        for (String nav : navs) {
            JButton navBtn = new JButton(nav);
            styleButton(navBtn, nav.equals("Dashboard") ? ORANGE : (nav.equals("Logout") ? BLACK : WHITE));
            navBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            navBtn.setForeground(nav.equals("Dashboard") ? WHITE : (nav.equals("Logout") ? ORANGE : BLACK));
            navBtn.setFocusPainted(false);
            navBtn.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));
            navBtn.setOpaque(true);
            navBtn.setBackground(new Color(255,255,255, nav.equals("Dashboard") ? 80 : 180));
            navBtn.addActionListener(e -> {
                switch (nav) {
                    case "Dashboard": cardLayout.show(mainPanel, "adminDashboard"); break;
                    case "Products": 
                        if (Arrays.asList(mainPanel.getComponents()).stream().noneMatch(c -> c.getName() != null && c.getName().equals("productManagement"))) {
                            mainPanel.add(productManagementPanel(), "productManagement");
                        }
                        cardLayout.show(mainPanel, "productManagement"); 
                        break;
                    case "Coupons": 
                        if (Arrays.asList(mainPanel.getComponents()).stream().noneMatch(c -> c.getName() != null && c.getName().equals("couponManagement"))) {
                            mainPanel.add(couponManagementPanel(), "couponManagement");
                        }
                        cardLayout.show(mainPanel, "couponManagement"); 
                        break;
                    case "Logout": currentUser = null; currentUserType = null; cart.clear(); cardLayout.show(mainPanel, "login"); break;
                }
            });
            navPanel.add(navBtn);
        }
        header.add(navPanel, BorderLayout.EAST);
        header.setBackground(new Color(0,0,0,120));
        bgPanel.add(header, BorderLayout.NORTH);

        // MAIN CONTENT
        JPanel content = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(10, 25, 73, 180)); // semi-transparent dark blue
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
            }
        };
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(40, 60, 40, 60));

        // Welcome section
        JLabel welcome = new JLabel("Admin Dashboard");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 32));
        welcome.setForeground(ORANGE);
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(welcome);
        content.add(Box.createVerticalStrut(20));

        JLabel desc = new JLabel("Welcome, " + currentUser + "! Manage your e-commerce platform.");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        desc.setForeground(Color.WHITE);
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(desc);
        content.add(Box.createVerticalStrut(40));

        // Quick stats
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        statsPanel.setOpaque(false);
        
        // Physical products count
        JPanel statCard1 = new JPanel();
        statCard1.setLayout(new BoxLayout(statCard1, BoxLayout.Y_AXIS));
        statCard1.setBackground(new Color(255, 140, 0, 100));
        statCard1.setBorder(BorderFactory.createLineBorder(ORANGE, 2));
        JLabel stat1Title = new JLabel("Physical Products");
        stat1Title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        stat1Title.setForeground(WHITE);
        stat1Title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel stat1Count = new JLabel(String.valueOf(loadProducts("physical", null).size()));
        stat1Count.setFont(new Font("Segoe UI", Font.BOLD, 24));
        stat1Count.setForeground(ORANGE);
        stat1Count.setAlignmentX(Component.CENTER_ALIGNMENT);
        statCard1.add(stat1Title);
        statCard1.add(Box.createVerticalStrut(10));
        statCard1.add(stat1Count);
        statsPanel.add(statCard1);

        // Digital products count
        JPanel statCard2 = new JPanel();
        statCard2.setLayout(new BoxLayout(statCard2, BoxLayout.Y_AXIS));
        statCard2.setBackground(new Color(255, 140, 0, 100));
        statCard2.setBorder(BorderFactory.createLineBorder(ORANGE, 2));
        JLabel stat2Title = new JLabel("Digital Products");
        stat2Title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        stat2Title.setForeground(WHITE);
        stat2Title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel stat2Count = new JLabel(String.valueOf(loadProducts("digital", null).size()));
        stat2Count.setFont(new Font("Segoe UI", Font.BOLD, 24));
        stat2Count.setForeground(ORANGE);
        stat2Count.setAlignmentX(Component.CENTER_ALIGNMENT);
        statCard2.add(stat2Title);
        statCard2.add(Box.createVerticalStrut(10));
        statCard2.add(stat2Count);
        statsPanel.add(statCard2);

        // Coupons count
        JPanel statCard3 = new JPanel();
        statCard3.setLayout(new BoxLayout(statCard3, BoxLayout.Y_AXIS));
        statCard3.setBackground(new Color(255, 140, 0, 100));
        statCard3.setBorder(BorderFactory.createLineBorder(ORANGE, 2));
        JLabel stat3Title = new JLabel("Active Coupons");
        stat3Title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        stat3Title.setForeground(WHITE);
        stat3Title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel stat3Count = new JLabel(String.valueOf(loadCoupons().size()));
        stat3Count.setFont(new Font("Segoe UI", Font.BOLD, 24));
        stat3Count.setForeground(ORANGE);
        stat3Count.setAlignmentX(Component.CENTER_ALIGNMENT);
        statCard3.add(stat3Title);
        statCard3.add(Box.createVerticalStrut(10));
        statCard3.add(stat3Count);
        statsPanel.add(statCard3);

        content.add(statsPanel);
        content.add(Box.createVerticalStrut(40));

        // Action buttons
        JPanel actionPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        actionPanel.setOpaque(false);

        JButton manageProductsBtn = new JButton("Manage Products");
        styleButton(manageProductsBtn, ORANGE);
        manageProductsBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        manageProductsBtn.addActionListener(e -> cardLayout.show(mainPanel, "productManagement"));
        actionPanel.add(manageProductsBtn);

        JButton manageCouponsBtn = new JButton("Manage Coupons");
        styleButton(manageCouponsBtn, ORANGE);
        manageCouponsBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        manageCouponsBtn.addActionListener(e -> cardLayout.show(mainPanel, "couponManagement"));
        actionPanel.add(manageCouponsBtn);

        content.add(actionPanel);

        bgPanel.add(content, BorderLayout.CENTER);
        return bgPanel;
    }

    // ---------------------- SUPERADMIN DASHBOARD PANEL ----------------------
    private static JPanel superAdminDashboardPanel() {
        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    Image bg = ImageIO.read(new File(IMAGE_DIR + "background.jpg"));
                    g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    setBackground(DARK_BLUE);
                }
            }
        };
        bgPanel.setLayout(new BorderLayout());

        // HEADER BAR
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 0, 0));
    
        JPanel navPanel = new JPanel();
        navPanel.setOpaque(false);
        navPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 24, 10));
        String[] navs = {"Dashboard", "Products", "Coupons", "Logout"};
        for (String nav : navs) {
            JButton navBtn = new JButton(nav);
            styleButton(navBtn, nav.equals("Dashboard") ? ORANGE : (nav.equals("Logout") ? BLACK : WHITE));
            navBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            navBtn.setForeground(nav.equals("Dashboard") ? WHITE : (nav.equals("Logout") ? ORANGE : BLACK));
            navBtn.setFocusPainted(false);
            navBtn.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));
            navBtn.setOpaque(true);
            navBtn.setBackground(new Color(255,255,255, nav.equals("Dashboard") ? 80 : 180));
            navBtn.addActionListener(e -> {
                switch (nav) {
                    case "Dashboard": cardLayout.show(mainPanel, "superAdminDashboard"); break;
                    case "Products": 
                        if (Arrays.asList(mainPanel.getComponents()).stream().noneMatch(c -> c.getName() != null && c.getName().equals("productManagement"))) {
                            mainPanel.add(productManagementPanel(), "productManagement");
                        }
                        cardLayout.show(mainPanel, "productManagement"); 
                        break;
                    case "Coupons": 
                        if (Arrays.asList(mainPanel.getComponents()).stream().noneMatch(c -> c.getName() != null && c.getName().equals("couponManagement"))) {
                            mainPanel.add(couponManagementPanel(), "couponManagement");
                        }
                        cardLayout.show(mainPanel, "couponManagement"); 
                        break;
                    case "Logout": currentUser = null; currentUserType = null; cart.clear(); cardLayout.show(mainPanel, "login"); break;
                }
            });
            navPanel.add(navBtn);
        }
        header.add(navPanel, BorderLayout.EAST);
        header.setBackground(new Color(0,0,0,120));
        bgPanel.add(header, BorderLayout.NORTH);

        // MAIN CONTENT
        JPanel content = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(10, 25, 73, 180)); // semi-transparent dark blue
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
            }
        };
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(40, 60, 40, 60));

        // Welcome section
        JLabel welcome = new JLabel("SuperAdmin Dashboard");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 32));
        welcome.setForeground(ORANGE);
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(welcome);
        content.add(Box.createVerticalStrut(20));

        JLabel desc = new JLabel("Welcome, " + currentUser + "! Full control over the e-commerce platform.");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        desc.setForeground(Color.WHITE);
        desc.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(desc);
        content.add(Box.createVerticalStrut(40));

        // Quick stats
        JPanel statsPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        statsPanel.setOpaque(false);
        
        // Physical products count
        JPanel statCard1 = new JPanel();
        statCard1.setLayout(new BoxLayout(statCard1, BoxLayout.Y_AXIS));
        statCard1.setBackground(new Color(255, 140, 0, 100));
        statCard1.setBorder(BorderFactory.createLineBorder(ORANGE, 2));
        JLabel stat1Title = new JLabel("Physical Products");
        stat1Title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        stat1Title.setForeground(WHITE);
        stat1Title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel stat1Count = new JLabel(String.valueOf(loadProducts("physical", null).size()));
        stat1Count.setFont(new Font("Segoe UI", Font.BOLD, 24));
        stat1Count.setForeground(ORANGE);
        stat1Count.setAlignmentX(Component.CENTER_ALIGNMENT);
        statCard1.add(stat1Title);
        statCard1.add(Box.createVerticalStrut(10));
        statCard1.add(stat1Count);
        statsPanel.add(statCard1);

        // Digital products count
        JPanel statCard2 = new JPanel();
        statCard2.setLayout(new BoxLayout(statCard2, BoxLayout.Y_AXIS));
        statCard2.setBackground(new Color(255, 140, 0, 100));
        statCard2.setBorder(BorderFactory.createLineBorder(ORANGE, 2));
        JLabel stat2Title = new JLabel("Digital Products");
        stat2Title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        stat2Title.setForeground(WHITE);
        stat2Title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel stat2Count = new JLabel(String.valueOf(loadProducts("digital", null).size()));
        stat2Count.setFont(new Font("Segoe UI", Font.BOLD, 24));
        stat2Count.setForeground(ORANGE);
        stat2Count.setAlignmentX(Component.CENTER_ALIGNMENT);
        statCard2.add(stat2Title);
        statCard2.add(Box.createVerticalStrut(10));
        statCard2.add(stat2Count);
        statsPanel.add(statCard2);

        // Coupons count
        JPanel statCard3 = new JPanel();
        statCard3.setLayout(new BoxLayout(statCard3, BoxLayout.Y_AXIS));
        statCard3.setBackground(new Color(255, 140, 0, 100));
        statCard3.setBorder(BorderFactory.createLineBorder(ORANGE, 2));
        JLabel stat3Title = new JLabel("Active Coupons");
        stat3Title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        stat3Title.setForeground(WHITE);
        stat3Title.setAlignmentX(Component.CENTER_ALIGNMENT);
        JLabel stat3Count = new JLabel(String.valueOf(loadCoupons().size()));
        stat3Count.setFont(new Font("Segoe UI", Font.BOLD, 24));
        stat3Count.setForeground(ORANGE);
        stat3Count.setAlignmentX(Component.CENTER_ALIGNMENT);
        statCard3.add(stat3Title);
        statCard3.add(Box.createVerticalStrut(10));
        statCard3.add(stat3Count);
        statsPanel.add(statCard3);

        content.add(statsPanel);
        content.add(Box.createVerticalStrut(40));

        // Action buttons
        JPanel actionPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        actionPanel.setOpaque(false);

        JButton manageProductsBtn = new JButton("Manage Products");
        styleButton(manageProductsBtn, ORANGE);
        manageProductsBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        manageProductsBtn.addActionListener(e -> cardLayout.show(mainPanel, "productManagement"));
        actionPanel.add(manageProductsBtn);

        JButton manageCouponsBtn = new JButton("Manage Coupons");
        styleButton(manageCouponsBtn, ORANGE);
        manageCouponsBtn.setFont(new Font("Segoe UI", Font.BOLD, 18));
        manageCouponsBtn.addActionListener(e -> cardLayout.show(mainPanel, "couponManagement"));
        actionPanel.add(manageCouponsBtn);

        content.add(actionPanel);

        bgPanel.add(content, BorderLayout.CENTER);
        return bgPanel;
    }

    // ---------------------- PRODUCT MANAGEMENT PANEL ----------------------
    private static JPanel productManagementPanel() {
        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    Image bg = ImageIO.read(new File(IMAGE_DIR + "background.jpg"));
                    g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    setBackground(DARK_BLUE);
                }
            }
        };
        bgPanel.setLayout(new BorderLayout());

        // HEADER BAR
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 0, 0));
    
        JPanel navPanel = new JPanel();
        navPanel.setOpaque(false);
        navPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 24, 10));
        String[] navs = (currentUserType != null && currentUserType.equals("superadmin")) ? 
            new String[]{"Dashboard", "Products", "Coupons", "Logout"} :
            new String[]{"Dashboard", "Products", "Coupons", "Logout"};
        for (String nav : navs) {
            JButton navBtn = new JButton(nav);
            styleButton(navBtn, nav.equals("Products") ? ORANGE : (nav.equals("Logout") ? BLACK : WHITE));
            navBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            navBtn.setForeground(nav.equals("Products") ? WHITE : (nav.equals("Logout") ? ORANGE : BLACK));
            navBtn.setFocusPainted(false);
            navBtn.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));
            navBtn.setOpaque(true);
            navBtn.setBackground(new Color(255,255,255, nav.equals("Products") ? 80 : 180));
            navBtn.addActionListener(e -> {
                switch (nav) {
                    case "Dashboard": 
                        if (currentUserType != null && currentUserType.equals("superadmin")) {
                            if (Arrays.asList(mainPanel.getComponents()).stream().noneMatch(c -> c.getName() != null && c.getName().equals("superAdminDashboard"))) {
                                mainPanel.add(superAdminDashboardPanel(), "superAdminDashboard");
                            }
                            cardLayout.show(mainPanel, "superAdminDashboard");
                        } else {
                            if (Arrays.asList(mainPanel.getComponents()).stream().noneMatch(c -> c.getName() != null && c.getName().equals("adminDashboard"))) {
                                mainPanel.add(adminDashboardPanel(), "adminDashboard");
                            }
                            cardLayout.show(mainPanel, "adminDashboard");
                        }
                        break;
                    case "Products": cardLayout.show(mainPanel, "productManagement"); break;
                    case "Coupons": 
                        if (Arrays.asList(mainPanel.getComponents()).stream().noneMatch(c -> c.getName() != null && c.getName().equals("couponManagement"))) {
                            mainPanel.add(couponManagementPanel(), "couponManagement");
                        }
                        cardLayout.show(mainPanel, "couponManagement"); 
                        break;
                    case "Logout": currentUser = null; currentUserType = null; cart.clear(); cardLayout.show(mainPanel, "login"); break;
                }
            });
            navPanel.add(navBtn);
        }
        header.add(navPanel, BorderLayout.EAST);
        header.setBackground(new Color(0,0,0,120));
        bgPanel.add(header, BorderLayout.NORTH);

        // MAIN CONTENT
        JPanel content = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(10, 25, 73, 180)); // semi-transparent dark blue
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
            }
        };
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(40, 60, 40, 60));

        // Title
        JLabel title = new JLabel("Product Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(ORANGE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(30));

        // Action buttons
        JPanel actionPanel = new JPanel(new GridLayout(2, 2, 20, 20));
        actionPanel.setOpaque(false);

        JButton addProductBtn = new JButton("Add Product");
        styleButton(addProductBtn, ORANGE);
        addProductBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        addProductBtn.addActionListener(e -> showAddProductDialog());
        actionPanel.add(addProductBtn);

        JButton updateProductBtn = new JButton("Update Product");
        styleButton(updateProductBtn, ORANGE);
        updateProductBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        updateProductBtn.addActionListener(e -> showUpdateProductDialog());
        actionPanel.add(updateProductBtn);

        // Only superadmin can remove products
        if (currentUserType != null && currentUserType.equals("superadmin")) {
            JButton removeProductBtn = new JButton("Remove Product");
            styleButton(removeProductBtn, ORANGE);
            removeProductBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            removeProductBtn.addActionListener(e -> showRemoveProductDialog());
            actionPanel.add(removeProductBtn);
        }

        JButton viewProductsBtn = new JButton("View Products");
        styleButton(viewProductsBtn, ORANGE);
        viewProductsBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        viewProductsBtn.addActionListener(e -> showViewProductsDialog());
        actionPanel.add(viewProductsBtn);

        content.add(actionPanel);
        content.add(Box.createVerticalStrut(30));

        // Product lists
        JPanel listsPanel = new JPanel(new GridLayout(1, 2, 20, 20));
        listsPanel.setOpaque(false);

        // Physical Products List
        JPanel physicalPanel = new JPanel(new BorderLayout());
        physicalPanel.setBackground(new Color(255, 140, 0, 100));
        physicalPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(ORANGE, 2), "Physical Products", 0, 0, new Font("Segoe UI", Font.BOLD, 16), ORANGE));
        
        java.util.List<ProductRow> physicalProducts = loadProducts("physical", null);
        DefaultListModel<String> physicalModel = new DefaultListModel<>();
        for (ProductRow p : physicalProducts) {
            physicalModel.addElement(p.name + " - Rs " + p.price + " (Qty: " + p.qty + ")");
        }
        JList<String> physicalList = new JList<>(physicalModel);
        physicalList.setBackground(new Color(255, 255, 255, 150));
        physicalList.setFont(TEXT_FONT);
        JScrollPane physicalScroll = new JScrollPane(physicalList);
        physicalPanel.add(physicalScroll, BorderLayout.CENTER);
        listsPanel.add(physicalPanel);

        // Digital Products List
        JPanel digitalPanel = new JPanel(new BorderLayout());
        digitalPanel.setBackground(new Color(255, 140, 0, 100));
        digitalPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(ORANGE, 2), "Digital Products", 0, 0, new Font("Segoe UI", Font.BOLD, 16), ORANGE));
        
        java.util.List<ProductRow> digitalProducts = loadProducts("digital", null);
        DefaultListModel<String> digitalModel = new DefaultListModel<>();
        for (ProductRow p : digitalProducts) {
            digitalModel.addElement(p.name + " - Rs " + p.price + " (Qty: " + p.qty + ")");
        }
        JList<String> digitalList = new JList<>(digitalModel);
        digitalList.setBackground(new Color(255, 255, 255, 150));
        digitalList.setFont(TEXT_FONT);
        JScrollPane digitalScroll = new JScrollPane(digitalList);
        digitalPanel.add(digitalScroll, BorderLayout.CENTER);
        listsPanel.add(digitalPanel);

        content.add(listsPanel);

        bgPanel.add(content, BorderLayout.CENTER);
        return bgPanel;
    }

    // ---------------------- COUPON MANAGEMENT PANEL ----------------------
    private static JPanel couponManagementPanel() {
        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    Image bg = ImageIO.read(new File(IMAGE_DIR + "background.jpg"));
                    g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    setBackground(DARK_BLUE);
                }
            }
        };
        bgPanel.setLayout(new BorderLayout());

        // HEADER BAR
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 0, 0));
    
        JPanel navPanel = new JPanel();
        navPanel.setOpaque(false);
        navPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 24, 10));
        String[] navs = {"Dashboard", "Products", "Coupons", "Logout"};
        for (String nav : navs) {
            JButton navBtn = new JButton(nav);
            styleButton(navBtn, nav.equals("Coupons") ? ORANGE : (nav.equals("Logout") ? BLACK : WHITE));
            navBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            navBtn.setForeground(nav.equals("Coupons") ? WHITE : (nav.equals("Logout") ? ORANGE : BLACK));
            navBtn.setFocusPainted(false);
            navBtn.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));
            navBtn.setOpaque(true);
            navBtn.setBackground(new Color(255,255,255, nav.equals("Coupons") ? 80 : 180));
            navBtn.addActionListener(e -> {
                switch (nav) {
                    case "Dashboard": 
                        if (currentUserType != null && currentUserType.equals("superadmin")) {
                            cardLayout.show(mainPanel, "superAdminDashboard");
                        } else {
                            cardLayout.show(mainPanel, "adminDashboard");
                        }
                        break;
                    case "Products": 
                        if (Arrays.asList(mainPanel.getComponents()).stream().noneMatch(c -> c.getName() != null && c.getName().equals("productManagement"))) {
                            mainPanel.add(productManagementPanel(), "productManagement");
                        }
                        cardLayout.show(mainPanel, "productManagement"); 
                        break;
                    case "Coupons": cardLayout.show(mainPanel, "couponManagement"); break;
                    case "Logout": currentUser = null; currentUserType = null; cart.clear(); cardLayout.show(mainPanel, "login"); break;
                }
            });
            navPanel.add(navBtn);
        }
        header.add(navPanel, BorderLayout.EAST);
        header.setBackground(new Color(0,0,0,120));
        bgPanel.add(header, BorderLayout.NORTH);

        // MAIN CONTENT
        JPanel content = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(10, 25, 73, 180)); // semi-transparent dark blue
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
            }
        };
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setBorder(new EmptyBorder(40, 60, 40, 60));

        // Title
        JLabel title = new JLabel("Coupon Management");
        title.setFont(new Font("Segoe UI", Font.BOLD, 32));
        title.setForeground(ORANGE);
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(30));

        // Action buttons
        JPanel actionPanel = new JPanel(new GridLayout(1, 3, 20, 20));
        actionPanel.setOpaque(false);

        JButton addCouponBtn = new JButton("Add Coupon");
        styleButton(addCouponBtn, ORANGE);
        addCouponBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        addCouponBtn.addActionListener(e -> showAddCouponDialog());
        actionPanel.add(addCouponBtn);

        JButton viewCouponsBtn = new JButton("View Coupons");
        styleButton(viewCouponsBtn, ORANGE);
        viewCouponsBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        viewCouponsBtn.addActionListener(e -> showViewCouponsDialog());
        actionPanel.add(viewCouponsBtn);

        JButton removeCouponBtn = new JButton("Remove Coupon");
        styleButton(removeCouponBtn, ORANGE);
        removeCouponBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        removeCouponBtn.addActionListener(e -> showRemoveCouponDialog());
        actionPanel.add(removeCouponBtn);

        content.add(actionPanel);
        content.add(Box.createVerticalStrut(30));

        // Coupons list
        JPanel couponsPanel = new JPanel(new BorderLayout());
        couponsPanel.setBackground(new Color(255, 140, 0, 100));
        couponsPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(ORANGE, 2), "Active Coupons", 0, 0, new Font("Segoe UI", Font.BOLD, 16), ORANGE));
        
        java.util.List<CouponRow> coupons = loadCoupons();
        DefaultListModel<String> couponModel = new DefaultListModel<>();
        for (CouponRow c : coupons) {
            couponModel.addElement(c.code + " - " + c.discount);
        }
        JList<String> couponList = new JList<>(couponModel);
        couponList.setBackground(new Color(255, 255, 255, 150));
        couponList.setFont(TEXT_FONT);
        JScrollPane couponScroll = new JScrollPane(couponList);
        couponsPanel.add(couponScroll, BorderLayout.CENTER);
        content.add(couponsPanel);

        bgPanel.add(content, BorderLayout.CENTER);
        return bgPanel;
    }

    // ---------------------- MAIN MENU PANEL (HOMEPAGE) ----------------------
    private static JPanel mainMenuPanel() {
        JPanel bgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                try {
                    Image bg = ImageIO.read(new File(IMAGE_DIR + "background.jpg"));
                    g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
                } catch (Exception e) {
                    setBackground(DARK_BLUE);
                }
            }
        };
        bgPanel.setLayout(new BorderLayout());

        // HEADER BAR
        JPanel header = new JPanel(new BorderLayout());
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(0, 0, 0, 0));
    
        JPanel navPanel = new JPanel();
        navPanel.setOpaque(false);
        navPanel.setLayout(new FlowLayout(FlowLayout.RIGHT, 24, 10));
        String[] navs = {"Home", "Products", "Cart", "Order History", "Wishlist", "Logout"};
        for (String nav : navs) {
            JButton navBtn = new JButton(nav);
            styleButton(navBtn, nav.equals("Home") ? ORANGE : (nav.equals("Logout") ? BLACK : WHITE));
            navBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
            navBtn.setForeground(nav.equals("Home") ? WHITE : (nav.equals("Logout") ? ORANGE : BLACK));
            navBtn.setFocusPainted(false);
            navBtn.setBorder(BorderFactory.createEmptyBorder(6, 18, 6, 18));
            navBtn.setOpaque(true);
            navBtn.setBackground(new Color(255,255,255, nav.equals("Home") ? 80 : 180));
            navBtn.addActionListener(e -> {
                switch (nav) {
                    case "Home": cardLayout.show(mainPanel, "mainMenu"); break;
                    case "Products": cardLayout.show(mainPanel, "category"); break;
                    case "Cart": refreshCartPanel(); cardLayout.show(mainPanel, "cart"); break;
                    case "Order History": refreshOrderHistoryPanel(); cardLayout.show(mainPanel, "orderHistory"); break;
                    case "Wishlist": refreshWishlistPanel(); cardLayout.show(mainPanel, "wishlist"); break;
                    case "Logout": currentUser = null; currentUserType = null; cart.clear(); cardLayout.show(mainPanel, "login"); break;
                }
            });
            navPanel.add(navBtn);
        }
        header.add(navPanel, BorderLayout.EAST);
        header.setBackground(new Color(0,0,0,120));
        bgPanel.add(header, BorderLayout.NORTH);

        // HERO SECTION
        JPanel hero = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.setColor(new Color(10, 25, 73, 180)); // semi-transparent dark blue
                g.fillRoundRect(0, 0, getWidth(), getHeight(), 40, 40);
            }
        };
        hero.setOpaque(false);
        hero.setLayout(new BoxLayout(hero, BoxLayout.X_AXIS));
        hero.setBorder(new EmptyBorder(40, 60, 40, 60));
        // Left: Welcome text
        JPanel heroText = new JPanel();
        heroText.setOpaque(false);
        heroText.setLayout(new BoxLayout(heroText, BoxLayout.Y_AXIS));
        JLabel welcome = new JLabel("Welcome to Palmire");
        welcome.setFont(new Font("Segoe UI", Font.BOLD, 32));
        welcome.setForeground(ORANGE);
        welcome.setAlignmentX(Component.LEFT_ALIGNMENT);
        heroText.add(welcome);
        heroText.add(Box.createVerticalStrut(10));
        JLabel desc = new JLabel("Your one-stop shop for digital and physical products. Enjoy fast delivery, great deals, and a seamless shopping experience!");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        desc.setForeground(Color.WHITE);
        desc.setAlignmentX(Component.LEFT_ALIGNMENT);
        heroText.add(desc);
        heroText.add(Box.createVerticalStrut(20));
        JButton shopNow = new JButton("Shop Now");
        styleButton(shopNow, ORANGE);
        shopNow.setFont(new Font("Segoe UI", Font.BOLD, 20));
        shopNow.setAlignmentX(Component.LEFT_ALIGNMENT);
        shopNow.addActionListener(e -> cardLayout.show(mainPanel, "category"));
        heroText.add(shopNow);
        hero.add(heroText);
        hero.add(Box.createHorizontalGlue());
        // Right: Featured product image (first featured product)
        java.util.List<ProductRow> featured = loadProducts("physical", null);
        if (!featured.isEmpty()) {
            ProductRow p = featured.get(0);
            JLabel img = new JLabel(loadProductImageForName(p.name));
            img.setBorder(new EmptyBorder(0, 40, 0, 0));
            hero.add(img);
        }
        bgPanel.add(hero, BorderLayout.CENTER);

        // PRODUCT CAROUSEL/ROW
        JPanel carousel = new JPanel();
        carousel.setOpaque(false);
        carousel.setLayout(new BoxLayout(carousel, BoxLayout.X_AXIS));
        carousel.setBorder(new EmptyBorder(20, 60, 10, 60));
        java.util.List<ProductRow> carouselProducts = loadProducts("physical", null);
        int count = 0;
        for (ProductRow p : carouselProducts) {
            if (count++ >= 4) break;
            JPanel prodCard = new JPanel();
            prodCard.setOpaque(false);
            prodCard.setLayout(new BoxLayout(prodCard, BoxLayout.Y_AXIS));
            JLabel img = new JLabel(loadProductImageForName(p.name));
            img.setAlignmentX(Component.CENTER_ALIGNMENT);
            prodCard.add(img);
            JLabel name = new JLabel(p.name);
            name.setFont(new Font("Segoe UI", Font.BOLD, 16));
            name.setForeground(ORANGE);
            name.setAlignmentX(Component.CENTER_ALIGNMENT);
            prodCard.add(name);
            JButton shopBtn = new JButton("Shop");
            styleButton(shopBtn, ORANGE);
            shopBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            shopBtn.addActionListener(e -> cardLayout.show(mainPanel, "category"));
            prodCard.add(shopBtn);
            prodCard.setBorder(new EmptyBorder(0, 20, 0, 20));
            carousel.add(prodCard);
        }
        bgPanel.add(carousel, BorderLayout.SOUTH);

        // FEATURED PRODUCTS GRID (with background)
        JPanel gridBgPanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(new Color(20, 20, 20, 180)); // semi-transparent dark
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30);
                g2.dispose();
            }
        };
        gridBgPanel.setOpaque(false);
        gridBgPanel.setLayout(new BorderLayout());
        JPanel featuredGrid = new JPanel(new GridLayout(1, 4, 24, 24));
        featuredGrid.setOpaque(false);
        featuredGrid.setBorder(new EmptyBorder(10, 60, 10, 60));
        java.util.List<ProductRow> gridProducts = loadProducts("digital", null);
        count = 0;
        for (ProductRow p : gridProducts) {
            if (count++ >= 4) break;
            JPanel card = new JPanel();
            card.setOpaque(false);
            card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
            JLabel img = new JLabel(loadProductImageForName(p.name));
            img.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(img);
            JLabel name = new JLabel(p.name);
            name.setFont(new Font("Segoe UI", Font.BOLD, 16));
            name.setForeground(ORANGE);
            name.setAlignmentX(Component.CENTER_ALIGNMENT);
            card.add(name);
            JButton shopBtn = new JButton("Shop");
            styleButton(shopBtn, ORANGE);
            shopBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
            shopBtn.addActionListener(e -> cardLayout.show(mainPanel, "category"));
            card.add(shopBtn);
            card.setBorder(new EmptyBorder(0, 20, 0, 20));
            featuredGrid.add(card);
        }
        gridBgPanel.add(featuredGrid, BorderLayout.CENTER);
        // Place the grid below the hero section
        JPanel centerPanel = new JPanel();
        centerPanel.setOpaque(false);
        centerPanel.setLayout(new BoxLayout(centerPanel, BoxLayout.Y_AXIS));
        centerPanel.add(hero);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(gridBgPanel);
        centerPanel.add(Box.createVerticalStrut(10));
        centerPanel.add(carousel);
        bgPanel.add(centerPanel, BorderLayout.CENTER);
        return bgPanel;
    }

    

    private static JPanel categoryTabPanel(String type) {
        JPanel panel = new JPanel();
        panel.setOpaque(false);
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        JLabel label = new JLabel(type.equals("physical") ? "Shop Physical Products" : "Shop Digital Products");
        label.setFont(SUBTITLE_FONT);
        label.setForeground(WHITE);
        label.setAlignmentX(Component.CENTER_ALIGNMENT);
        panel.add(label);
        panel.add(Box.createVerticalStrut(10));
        JButton browseBtn = new JButton("Browse " + (type.equals("physical") ? "Physical" : "Digital"));
        styleButton(browseBtn, ORANGE);
        browseBtn.setFont(new Font("Segoe UI", Font.BOLD, 16));
        browseBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        browseBtn.addActionListener(e -> showProductPanel(type));
        panel.add(browseBtn);
        return panel;
    }

    // ---------------------- CATEGORY PANEL ----------------------
    private static JPanel categoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DARK_BLUE);
        JLabel title = new JLabel("Select Product Category", JLabel.CENTER);
        title.setFont(SUBTITLE_FONT);
        title.setForeground(ORANGE);
        title.setBorder(new EmptyBorder(30, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);

        JPanel btnPanel = new JPanel(new GridLayout(1, 2, 40, 40));
        btnPanel.setBackground(DARK_BLUE);
        btnPanel.setBorder(new EmptyBorder(60, 120, 60, 120));

        JButton physicalBtn = new JButton("Physical Products");
        styleButton(physicalBtn, ORANGE);
        physicalBtn.addActionListener(e -> showProductPanel("physical"));
        btnPanel.add(physicalBtn);

        JButton digitalBtn = new JButton("Digital Products");
        styleButton(digitalBtn, BLACK);
        digitalBtn.addActionListener(e -> showProductPanel("digital"));
        btnPanel.add(digitalBtn);

        panel.add(btnPanel, BorderLayout.CENTER);

        JButton backBtn = new JButton("Back");
        styleButton(backBtn, BLACK);
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "mainMenu"));
        JPanel south = new JPanel();
        south.setBackground(DARK_BLUE);
        south.add(backBtn);
        panel.add(south, BorderLayout.SOUTH);
        return panel;
    }

    // ---------------------- PRODUCT PANEL (DYNAMIC) ----------------------
    private static void showProductPanel(String type) {
        String panelName = type + "Products";
        if (Arrays.asList(mainPanel.getComponents()).stream().noneMatch(c -> c.getName() != null && c.getName().equals(panelName))) {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setName(panelName);
            panel.setBackground(DARK_BLUE);
            JLabel title = new JLabel(type.equals("physical") ? "Physical Products" : "Digital Products", JLabel.CENTER);
            title.setFont(SUBTITLE_FONT);
            title.setForeground(ORANGE);
            title.setBorder(new EmptyBorder(30, 0, 10, 0));
            panel.add(title, BorderLayout.NORTH);

            // Subcategories
            JPanel subcatPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 40, 10));
            subcatPanel.setBackground(DARK_BLUE);
            if (type.equals("physical")) {
                JButton skincareBtn = new JButton("Skincare");
                styleButton(skincareBtn, ORANGE);
                skincareBtn.addActionListener(e -> showProductList(type, "Skincare"));
                subcatPanel.add(skincareBtn);
                JButton tvBtn = new JButton("TV");
                styleButton(tvBtn, BLACK);
                tvBtn.addActionListener(e -> showProductList(type, "TV"));
                subcatPanel.add(tvBtn);
                JButton allBtn = new JButton("All");
                styleButton(allBtn, ORANGE);
                allBtn.addActionListener(e -> showProductList(type, null));
                subcatPanel.add(allBtn);
            } else {
                JButton coursesBtn = new JButton("Courses");
                styleButton(coursesBtn, ORANGE);
                coursesBtn.addActionListener(e -> showProductList(type, "courses"));
                subcatPanel.add(coursesBtn);
                JButton allBtn = new JButton("All");
                styleButton(allBtn, BLACK);
                allBtn.addActionListener(e -> showProductList(type, null));
                subcatPanel.add(allBtn);
            }
            panel.add(subcatPanel, BorderLayout.CENTER);

            JButton backBtn = new JButton("Back");
            styleButton(backBtn, BLACK);
            backBtn.addActionListener(e -> cardLayout.show(mainPanel, "category"));
            JPanel south = new JPanel();
            south.setBackground(DARK_BLUE);
            south.add(backBtn);
            panel.add(south, BorderLayout.SOUTH);

            mainPanel.add(panel, panelName);
        }
        cardLayout.show(mainPanel, panelName);
    }

    // ---------------------- PRODUCT LIST PANEL (DYNAMIC) ----------------------
    private static void showProductList(String type, String subcategory) {
        String panelName = type + "_" + (subcategory == null ? "all" : subcategory);
        if (Arrays.asList(mainPanel.getComponents()).stream().noneMatch(c -> c.getName() != null && c.getName().equals(panelName))) {
            JPanel panel = new JPanel(new BorderLayout());
            panel.setName(panelName);
            panel.setBackground(DARK_BLUE);
            JLabel title = new JLabel((subcategory == null ? "All " : subcategory + " ") + (type.equals("physical") ? "Physical Products" : "Digital Products"), JLabel.CENTER);
            title.setFont(SUBTITLE_FONT);
            title.setForeground(ORANGE);
            title.setBorder(new EmptyBorder(30, 0, 10, 0));
            panel.add(title, BorderLayout.NORTH);

            java.util.List<ProductRow> products = loadProducts(type, subcategory);
            JPanel gridPanel = new JPanel(new GridLayout(0, 3, 24, 24));
            gridPanel.setBackground(DARK_BLUE);
            gridPanel.setBorder(new EmptyBorder(20, 40, 20, 40));
            for (ProductRow p : products) {
                JPanel card = new JPanel();
                card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
                card.setBackground(BLACK);
                card.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(ORANGE, 2),
                    new EmptyBorder(10, 10, 10, 10)));
                // Image
                JLabel imgLabel = new JLabel();
                imgLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                imgLabel.setPreferredSize(new Dimension(PRODUCT_IMG_WIDTH, PRODUCT_IMG_HEIGHT));
                imgLabel.setMaximumSize(new Dimension(PRODUCT_IMG_WIDTH, PRODUCT_IMG_HEIGHT));
                imgLabel.setIcon(loadProductImageForName(p.name));
                card.add(imgLabel);
                card.add(Box.createVerticalStrut(8));
                // Name
                JLabel name = new JLabel(p.name);
                name.setFont(new Font("Segoe UI", Font.BOLD, 16));
                name.setForeground(ORANGE);
                name.setAlignmentX(Component.CENTER_ALIGNMENT);
                card.add(name);
                // Type
                JLabel typeLabel = new JLabel(p.type);
                typeLabel.setFont(TEXT_FONT);
                typeLabel.setForeground(WHITE);
                typeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
                card.add(typeLabel);
                // Price
                JLabel price = new JLabel("Rs " + p.price);
                price.setFont(TEXT_FONT);
                price.setForeground(ORANGE);
                price.setAlignmentX(Component.CENTER_ALIGNMENT);
                card.add(price);
                // Quantity
                JLabel qty = new JLabel("Qty: " + p.qty);
                qty.setFont(TEXT_FONT);
                qty.setForeground(WHITE);
                qty.setAlignmentX(Component.CENTER_ALIGNMENT);
                card.add(qty);
                card.add(Box.createVerticalStrut(8));
                // Buttons
                JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 8, 0));
                btns.setBackground(BLACK);
                JButton addBtn = new JButton("Add to Cart");
                styleButton(addBtn, ORANGE);
                addBtn.addActionListener(e -> {
                    String tag = (type.equals("physical") ? "P-" : "D-") + p.name;
                    cart.put(tag, cart.getOrDefault(tag, 0) + 1);
                    JOptionPane.showMessageDialog(frame, "Added to cart!");
                });
                JButton wishBtn = new JButton("Wishlist");
                styleButton(wishBtn, BLACK);
                wishBtn.addActionListener(e -> {
                    addToWishlist(type, p.name);
                    JOptionPane.showMessageDialog(frame, "Added to wishlist!");
                });
                btns.add(addBtn);
                btns.add(wishBtn);
                card.add(btns);
                gridPanel.add(card);
            }
            JScrollPane scroll = new JScrollPane(gridPanel);
            scroll.setBorder(null);
            panel.add(scroll, BorderLayout.CENTER);

            JButton backBtn = new JButton("Back");
            styleButton(backBtn, BLACK);
            backBtn.addActionListener(e -> showProductPanel(type));
            JPanel south = new JPanel();
            south.setBackground(DARK_BLUE);
            south.add(backBtn);
            panel.add(south, BorderLayout.SOUTH);

            mainPanel.add(panel, panelName);
        }
        cardLayout.show(mainPanel, panelName);
    }

    // Improved image loader to match actual filenames
    private static ImageIcon loadProductImageForName(String productName) {
        String normalized = productName.toLowerCase().replaceAll("[^a-z0-9]", "");
        File dir = new File(IMAGE_DIR);
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                String fname = f.getName().toLowerCase().replaceAll("[^a-z0-9]", "");
                if (fname.contains(normalized)) {
                    try {
                        Image img = ImageIO.read(f);
                        Image scaled = img.getScaledInstance(PRODUCT_IMG_WIDTH, PRODUCT_IMG_HEIGHT, Image.SCALE_SMOOTH);
                        return new ImageIcon(scaled);
                    } catch (Exception e) { break; }
                }
            }
        }
        // fallback to placeholder
        File imgFile = new File(IMAGE_DIR + PLACEHOLDER_IMAGE);
        try {
            Image img = ImageIO.read(imgFile);
            Image scaled = img.getScaledInstance(PRODUCT_IMG_WIDTH, PRODUCT_IMG_HEIGHT, Image.SCALE_SMOOTH);
            return new ImageIcon(scaled);
        } catch (Exception e) {
            return new ImageIcon();
        }
    }

    // ---------------------- CART PANEL ----------------------
    private static JPanel cartPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DARK_BLUE);
        panel.setName("cartPanel");
        JLabel title = new JLabel("Your Cart", JLabel.CENTER);
        title.setFont(SUBTITLE_FONT);
        title.setForeground(ORANGE);
        title.setBorder(new EmptyBorder(30, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);
        // Content will be refreshed dynamically
        return panel;
    }
    private static void refreshCartPanel() {
        JPanel panel = (JPanel) Arrays.stream(mainPanel.getComponents())
                .filter(c -> c.getName() != null && c.getName().equals("cartPanel"))
                .findFirst().orElse(null);
        if (panel == null) return;
        panel.removeAll();
        JLabel title = new JLabel("Your Cart", JLabel.CENTER);
        title.setFont(SUBTITLE_FONT);
        title.setForeground(ORANGE);
        title.setBorder(new EmptyBorder(30, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(DARK_BLUE);
        double total = 0;
        for (Map.Entry<String, Integer> entry : cart.entrySet()) {
            String tag = entry.getKey();
            int qty = entry.getValue();
            ProductRow p = findProductByTag(tag);
            if (p == null) continue;
            JPanel prodPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 10));
            prodPanel.setBackground(DARK_BLUE);
            JLabel name = new JLabel(p.name + " (" + p.type + ")");
            name.setFont(TEXT_FONT);
            name.setForeground(WHITE);
            JLabel price = new JLabel("Rs " + p.price);
            price.setFont(TEXT_FONT);
            price.setForeground(ORANGE);
            JLabel q = new JLabel("Qty: " + qty);
            q.setFont(TEXT_FONT);
            q.setForeground(WHITE);
            JButton removeBtn = new JButton("Remove");
            styleButton(removeBtn, BLACK);
            removeBtn.addActionListener(e -> {
                cart.remove(tag);
                refreshCartPanel();
            });
            prodPanel.add(name);
            prodPanel.add(price);
            prodPanel.add(q);
            prodPanel.add(removeBtn);
            listPanel.add(prodPanel);
            total += p.price * qty;
        }
        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);
        JLabel totalLabel = new JLabel("Total: Rs " + total, JLabel.CENTER);
        totalLabel.setFont(TEXT_FONT);
        totalLabel.setForeground(ORANGE);
        JButton checkoutBtn = new JButton("Checkout");
        styleButton(checkoutBtn, ORANGE);
        checkoutBtn.addActionListener(e -> {
            if (!cart.isEmpty()) {
                saveOrderHistory();
                JOptionPane.showMessageDialog(frame, "Thank you for your purchase!");
                cart.clear();
                refreshCartPanel();
            }
        });
        JButton backBtn = new JButton("Back");
        styleButton(backBtn, BLACK);
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "mainMenu"));
        JPanel south = new JPanel();
        south.setBackground(DARK_BLUE);
        south.add(totalLabel);
        south.add(checkoutBtn);
        south.add(backBtn);
        panel.add(south, BorderLayout.SOUTH);
        panel.revalidate();
        panel.repaint();
    }

    // ---------------------- WISHLIST PANEL ----------------------
    private static JPanel wishlistPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DARK_BLUE);
        panel.setName("wishlistPanel");
        JLabel title = new JLabel("Your Wishlist", JLabel.CENTER);
        title.setFont(SUBTITLE_FONT);
        title.setForeground(ORANGE);
        title.setBorder(new EmptyBorder(30, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);
        // Content will be refreshed dynamically
        return panel;
    }
    private static void refreshWishlistPanel() {
        JPanel panel = (JPanel) Arrays.stream(mainPanel.getComponents())
                .filter(c -> c.getName() != null && c.getName().equals("wishlistPanel"))
                .findFirst().orElse(null);
        if (panel == null) return;
        panel.removeAll();
        JLabel title = new JLabel("Your Wishlist", JLabel.CENTER);
        title.setFont(SUBTITLE_FONT);
        title.setForeground(ORANGE);
        title.setBorder(new EmptyBorder(30, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);
        java.util.List<WishRow> wishes = loadWishlist();
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(DARK_BLUE);
        for (WishRow w : wishes) {
            JPanel wishPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 30, 10));
            wishPanel.setBackground(DARK_BLUE);
            JLabel name = new JLabel(w.productName + " (" + (w.type.equals("P") ? "Physical" : "Digital") + ")");
            name.setFont(TEXT_FONT);
            name.setForeground(WHITE);
            JButton removeBtn = new JButton("Remove");
            styleButton(removeBtn, BLACK);
            removeBtn.addActionListener(e -> {
                removeFromWishlist(w.type, w.productName);
                refreshWishlistPanel();
            });
            wishPanel.add(name);
            wishPanel.add(removeBtn);
            listPanel.add(wishPanel);
        }
        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);
        JButton backBtn = new JButton("Back");
        styleButton(backBtn, BLACK);
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "mainMenu"));
        JPanel south = new JPanel();
        south.setBackground(DARK_BLUE);
        south.add(backBtn);
        panel.add(south, BorderLayout.SOUTH);
        panel.revalidate();
        panel.repaint();
    }

    // ---------------------- ORDER HISTORY PANEL ----------------------
    private static JPanel orderHistoryPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DARK_BLUE);
        panel.setName("orderHistoryPanel");
        JLabel title = new JLabel("Order History", JLabel.CENTER);
        title.setFont(SUBTITLE_FONT);
        title.setForeground(ORANGE);
        title.setBorder(new EmptyBorder(30, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);
        // Content will be refreshed dynamically
        return panel;
    }
    private static void refreshOrderHistoryPanel() {
        JPanel panel = (JPanel) Arrays.stream(mainPanel.getComponents())
                .filter(c -> c.getName() != null && c.getName().equals("orderHistoryPanel"))
                .findFirst().orElse(null);
        if (panel == null) return;
        panel.removeAll();
        JLabel title = new JLabel("Order History", JLabel.CENTER);
        title.setFont(SUBTITLE_FONT);
        title.setForeground(ORANGE);
        title.setBorder(new EmptyBorder(30, 0, 10, 0));
        panel.add(title, BorderLayout.NORTH);
        java.util.List<String> orders = loadOrderHistory();
        JPanel listPanel = new JPanel();
        listPanel.setLayout(new BoxLayout(listPanel, BoxLayout.Y_AXIS));
        listPanel.setBackground(DARK_BLUE);
        if (orders.isEmpty()) {
            JLabel empty = new JLabel("No orders yet.");
            empty.setFont(TEXT_FONT);
            empty.setForeground(WHITE);
            listPanel.add(empty);
        } else {
            for (String order : orders) {
                JLabel orderLabel = new JLabel(order);
                orderLabel.setFont(TEXT_FONT);
                orderLabel.setForeground(WHITE);
                listPanel.add(orderLabel);
            }
        }
        JScrollPane scroll = new JScrollPane(listPanel);
        scroll.setBorder(null);
        panel.add(scroll, BorderLayout.CENTER);
        JButton backBtn = new JButton("Back");
        styleButton(backBtn, BLACK);
        backBtn.addActionListener(e -> cardLayout.show(mainPanel, "mainMenu"));
        JPanel south = new JPanel();
        south.setBackground(DARK_BLUE);
        south.add(backBtn);
        panel.add(south, BorderLayout.SOUTH);
        panel.revalidate();
        panel.repaint();
    }
    private static java.util.List<String> loadOrderHistory() {
        java.util.List<String> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(ORDER_HISTORY_FILE))) {
            String ln;
            while ((ln = br.readLine()) != null) {
                if (ln.startsWith(currentUser + ",")) {
                    list.add(ln.substring(ln.indexOf(",") + 1));
                }
            }
        } catch (IOException ignored) {}
        return list;
    }

    // ---------------------- UTILITY & DATA ----------------------
    private static void styleButton(JButton btn, Color bg) {
        btn.setBackground(bg);
        btn.setForeground(WHITE);
        btn.setFocusPainted(false);
        btn.setFont(TEXT_FONT);
        btn.setBorder(BorderFactory.createEmptyBorder(8, 18, 8, 18));
    }

    private static String[] authenticate(String email, String password) {
        try (BufferedReader br = new BufferedReader(new FileReader(LOGIN_FILE))) {
            String ln;
            while ((ln = br.readLine()) != null) {
                String[] parts = ln.split(",");
                if (parts.length >= 3 && parts[0].equalsIgnoreCase(email) && parts[2].equals(password)) {
                    if (parts.length == 4 && (parts[3].equals("admin") || parts[3].equals("superadmin")))
                        return new String[]{parts[3]};
                    else if (parts.length >= 5)
                        return new String[]{"customer", parts[3], parts[4]};
                }
            }
        } catch (IOException ignored) {}
        return null;
    }

    private static boolean isEmailRegistered(String email) {
        try (BufferedReader br = new BufferedReader(new FileReader(LOGIN_FILE))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.split(",", -1)[0].equalsIgnoreCase(email)) {
                    return true;
                }
            }
        } catch (IOException e) {
            System.out.println("❌ Error reading login file: " + e.getMessage());
        }
        return false;
    }

    private static java.util.List<ProductRow> loadProducts(String type, String subcategory) {
        java.util.List<ProductRow> list = new ArrayList<>();
        String file = type.equals("physical") ? PHYSICAL_FILE : DIGITAL_FILE;
        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String ln;
            while ((ln = br.readLine()) != null) {
                if (ln.trim().isEmpty()) continue;
                String[] p = ln.split(",");
                if (type.equals("physical") && p.length >= 5) {
                    // Format: Name,Type,Price,Qty,Weight
                    if (subcategory == null || p[1].equalsIgnoreCase(subcategory))
                        list.add(new ProductRow(p[0], p[1], Double.parseDouble(p[2]), Integer.parseInt(p[3])));
                } else if (type.equals("digital") && p.length >= 5) {
                    // Format: Name,Type,Price,Qty,Link
                    if (subcategory == null || p[1].equalsIgnoreCase(subcategory))
                        list.add(new ProductRow(p[0], p[1], Double.parseDouble(p[2]), Integer.parseInt(p[3])));
                }
            }
        } catch (IOException ignored) {}
        return list;
    }

    private static ProductRow findProductByTag(String tag) {
        if (tag.startsWith("P-")) {
            for (ProductRow p : loadProducts("physical", null))
                if (("P-" + p.name).equals(tag)) return p;
        } else if (tag.startsWith("D-")) {
            for (ProductRow p : loadProducts("digital", null))
                if (("D-" + p.name).equals(tag)) return p;
        }
        return null;
    }

    private static void addToWishlist(String type, String productName) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(WISHLIST_FILE, true))) {
            bw.write(currentUser + "," + (type.equals("physical") ? "P" : "D") + "," + productName);
            bw.newLine();
        } catch (IOException ignored) {}
    }
    private static java.util.List<WishRow> loadWishlist() {
        java.util.List<WishRow> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(WISHLIST_FILE))) {
            String ln;
            while ((ln = br.readLine()) != null) {
                String[] p = ln.split(",");
                if (p.length == 3 && p[0].equals(currentUser))
                    list.add(new WishRow(p[1], p[2]));
            }
        } catch (IOException ignored) {}
        return list;
    }
    private static void removeFromWishlist(String type, String productName) {
        java.util.List<String> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(WISHLIST_FILE))) {
            String ln;
            while ((ln = br.readLine()) != null) {
                String[] p = ln.split(",");
                if (p.length == 3 && p[0].equals(currentUser) && p[1].equals(type) && p[2].equals(productName))
                    continue;
                lines.add(ln);
            }
        } catch (IOException ignored) {}
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(WISHLIST_FILE))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
        } catch (IOException ignored) {}
    }

    private static void saveOrderHistory() {
        StringBuilder sb = new StringBuilder();
        sb.append(new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new java.util.Date()));
        sb.append(" | ");
        for (Map.Entry<String, Integer> entry : cart.entrySet()) {
            ProductRow p = findProductByTag(entry.getKey());
            if (p != null) {
                sb.append(p.name).append(" x").append(entry.getValue()).append(" (Rs ").append(p.price * entry.getValue()).append(") | ");
            }
        }
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(ORDER_HISTORY_FILE, true))) {
            bw.write(currentUser + "," + sb.toString());
            bw.newLine();
        } catch (IOException ignored) {}
    }

    // ---------------------- DATA CLASSES ----------------------
    private static class ProductRow {
        String name, type;
        double price;
        int qty;
        ProductRow(String n, String t, double p, int q) {
            name = n; type = t; price = p; qty = q;
        }
    }
    private static class WishRow {
        String type, productName;
        WishRow(String t, String n) { type = t; productName = n; }
    }

    // ---------------------- ADMIN HELPER METHODS ----------------------
    private static void showAddProductDialog() {
        JDialog dialog = new JDialog(frame, "Add Product", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 500);
        dialog.setLocationRelativeTo(frame);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(DARK_BLUE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Product type selection
        JLabel typeLabel = new JLabel("Product Type:");
        typeLabel.setForeground(WHITE);
        typeLabel.setFont(TEXT_FONT);
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(typeLabel, gbc);

        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"Physical", "Digital"});
        gbc.gridx = 1;
        panel.add(typeCombo, gbc);

        // Name field
        JLabel nameLabel = new JLabel("Product Name:");
        nameLabel.setForeground(WHITE);
        nameLabel.setFont(TEXT_FONT);
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(nameLabel, gbc);

        JTextField nameField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        // Category field
        JLabel catLabel = new JLabel("Category:");
        catLabel.setForeground(WHITE);
        catLabel.setFont(TEXT_FONT);
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(catLabel, gbc);

        JTextField catField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(catField, gbc);

        // Price field
        JLabel priceLabel = new JLabel("Price (Rs):");
        priceLabel.setForeground(WHITE);
        priceLabel.setFont(TEXT_FONT);
        gbc.gridx = 0; gbc.gridy = 3;
        panel.add(priceLabel, gbc);

        JTextField priceField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(priceField, gbc);

        // Quantity field
        JLabel qtyLabel = new JLabel("Quantity:");
        qtyLabel.setForeground(WHITE);
        qtyLabel.setFont(TEXT_FONT);
        gbc.gridx = 0; gbc.gridy = 4;
        panel.add(qtyLabel, gbc);

        JTextField qtyField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(qtyField, gbc);

        // Additional field (weight for physical, link for digital)
        JLabel extraLabel = new JLabel("Weight (kg) / Link:");
        extraLabel.setForeground(WHITE);
        extraLabel.setFont(TEXT_FONT);
        gbc.gridx = 0; gbc.gridy = 5;
        panel.add(extraLabel, gbc);

        JTextField extraField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(extraField, gbc);

        // Buttons
        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(DARK_BLUE);
        
        JButton addBtn = new JButton("Add Product");
        styleButton(addBtn, ORANGE);
        addBtn.addActionListener(e -> {
            try {
                String type = (String) typeCombo.getSelectedItem();
                String name = nameField.getText().trim();
                String category = catField.getText().trim();
                double price = Double.parseDouble(priceField.getText().trim());
                int qty = Integer.parseInt(qtyField.getText().trim());
                String extra = extraField.getText().trim();

                if (name.isEmpty() || category.isEmpty() || extra.isEmpty()) {
                    JOptionPane.showMessageDialog(dialog, "All fields are required!");
                    return;
                }

                String file = type.equals("Physical") ? PHYSICAL_FILE : DIGITAL_FILE;
                try (BufferedWriter bw = new BufferedWriter(new FileWriter(file, true))) {
                    String line = String.join(",", name, category, String.valueOf(price), String.valueOf(qty), extra);
                    bw.write(line);
                    bw.newLine();
                    JOptionPane.showMessageDialog(dialog, "Product added successfully!");
                    dialog.dispose();
                } catch (IOException ex) {
                    JOptionPane.showMessageDialog(dialog, "Error saving product: " + ex.getMessage());
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid price or quantity format!");
            }
        });

        JButton cancelBtn = new JButton("Cancel");
        styleButton(cancelBtn, BLACK);
        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(addBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private static void showUpdateProductDialog() {
        JDialog dialog = new JDialog(frame, "Update Product", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(frame);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(DARK_BLUE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Product Name:");
        nameLabel.setForeground(WHITE);
        nameLabel.setFont(TEXT_FONT);
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(nameLabel, gbc);

        JTextField nameField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        JLabel fieldLabel = new JLabel("Field to Update:");
        fieldLabel.setForeground(WHITE);
        fieldLabel.setFont(TEXT_FONT);
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(fieldLabel, gbc);

        JComboBox<String> fieldCombo = new JComboBox<>(new String[]{"Price", "Quantity", "Category"});
        gbc.gridx = 1;
        panel.add(fieldCombo, gbc);

        JLabel valueLabel = new JLabel("New Value:");
        valueLabel.setForeground(WHITE);
        valueLabel.setFont(TEXT_FONT);
        gbc.gridx = 0; gbc.gridy = 2;
        panel.add(valueLabel, gbc);

        JTextField valueField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(valueField, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(DARK_BLUE);
        
        JButton updateBtn = new JButton("Update");
        styleButton(updateBtn, ORANGE);
        updateBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String field = (String) fieldCombo.getSelectedItem();
            String value = valueField.getText().trim();

            if (name.isEmpty() || value.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required!");
                return;
            }

            // Try to update in both files
            boolean updated = updateProductInFile(PHYSICAL_FILE, name, field, value) || 
                            updateProductInFile(DIGITAL_FILE, name, field, value);

            if (updated) {
                JOptionPane.showMessageDialog(dialog, "Product updated successfully!");
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Product not found!");
            }
        });

        JButton cancelBtn = new JButton("Cancel");
        styleButton(cancelBtn, BLACK);
        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(updateBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private static void showRemoveProductDialog() {
        JDialog dialog = new JDialog(frame, "Remove Product", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(frame);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(DARK_BLUE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel nameLabel = new JLabel("Product Name:");
        nameLabel.setForeground(WHITE);
        nameLabel.setFont(TEXT_FONT);
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(nameLabel, gbc);

        JTextField nameField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(nameField, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(DARK_BLUE);
        
        JButton removeBtn = new JButton("Remove");
        styleButton(removeBtn, ORANGE);
        removeBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Product name is required!");
                return;
            }

            boolean removed = removeProductFromFile(PHYSICAL_FILE, name) || 
                            removeProductFromFile(DIGITAL_FILE, name);

            if (removed) {
                JOptionPane.showMessageDialog(dialog, "Product removed successfully!");
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Product not found!");
            }
        });

        JButton cancelBtn = new JButton("Cancel");
        styleButton(cancelBtn, BLACK);
        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(removeBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private static void showViewProductsDialog() {
        JDialog dialog = new JDialog(frame, "View Products", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 400);
        dialog.setLocationRelativeTo(frame);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DARK_BLUE);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setBackground(DARK_BLUE);
        tabbedPane.setForeground(WHITE);

        // Physical Products Tab
        java.util.List<ProductRow> physicalProducts = loadProducts("physical", null);
        DefaultListModel<String> physicalModel = new DefaultListModel<>();
        for (ProductRow p : physicalProducts) {
            physicalModel.addElement(p.name + " (" + p.type + ") - Rs " + p.price + " - Qty: " + p.qty);
        }
        JList<String> physicalList = new JList<>(physicalModel);
        physicalList.setBackground(new Color(255, 255, 255, 150));
        physicalList.setFont(TEXT_FONT);
        JScrollPane physicalScroll = new JScrollPane(physicalList);
        tabbedPane.addTab("Physical Products", physicalScroll);

        // Digital Products Tab
        java.util.List<ProductRow> digitalProducts = loadProducts("digital", null);
        DefaultListModel<String> digitalModel = new DefaultListModel<>();
        for (ProductRow p : digitalProducts) {
            digitalModel.addElement(p.name + " (" + p.type + ") - Rs " + p.price + " - Qty: " + p.qty);
        }
        JList<String> digitalList = new JList<>(digitalModel);
        digitalList.setBackground(new Color(255, 255, 255, 150));
        digitalList.setFont(TEXT_FONT);
        JScrollPane digitalScroll = new JScrollPane(digitalList);
        tabbedPane.addTab("Digital Products", digitalScroll);

        JButton closeBtn = new JButton("Close");
        styleButton(closeBtn, BLACK);
        closeBtn.addActionListener(e -> dialog.dispose());

        panel.add(tabbedPane, BorderLayout.CENTER);
        panel.add(closeBtn, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private static void showAddCouponDialog() {
        JDialog dialog = new JDialog(frame, "Add Coupon", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(frame);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(DARK_BLUE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel codeLabel = new JLabel("Coupon Code:");
        codeLabel.setForeground(WHITE);
        codeLabel.setFont(TEXT_FONT);
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(codeLabel, gbc);

        JTextField codeField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(codeField, gbc);

        JButton generateBtn = new JButton("Generate");
        styleButton(generateBtn, ORANGE);
        generateBtn.addActionListener(e -> {
            String code = generateCouponCode();
            codeField.setText(code);
        });
        gbc.gridx = 2;
        panel.add(generateBtn, gbc);

        JLabel discLabel = new JLabel("Discount (%):");
        discLabel.setForeground(WHITE);
        discLabel.setFont(TEXT_FONT);
        gbc.gridx = 0; gbc.gridy = 1;
        panel.add(discLabel, gbc);

        JTextField discField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(discField, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(DARK_BLUE);
        
        JButton addBtn = new JButton("Add Coupon");
        styleButton(addBtn, ORANGE);
        addBtn.addActionListener(e -> {
            String code = codeField.getText().trim();
            String discount = discField.getText().trim();

            if (code.isEmpty() || discount.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "All fields are required!");
                return;
            }

            if (!discount.endsWith("%")) {
                discount += "%";
            }

            try {
                int pct = Integer.parseInt(discount.replace("%", ""));
                if (pct <= 0) {
                    JOptionPane.showMessageDialog(dialog, "Discount must be positive!");
                    return;
                }
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(dialog, "Invalid discount format!");
                return;
            }

            try (BufferedWriter bw = new BufferedWriter(new FileWriter("coupon.txt", true))) {
                bw.write(code + "," + discount);
                bw.newLine();
                JOptionPane.showMessageDialog(dialog, "Coupon added successfully!");
                dialog.dispose();
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(dialog, "Error saving coupon: " + ex.getMessage());
            }
        });

        JButton cancelBtn = new JButton("Cancel");
        styleButton(cancelBtn, BLACK);
        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(addBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private static void showViewCouponsDialog() {
        JDialog dialog = new JDialog(frame, "View Coupons", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(frame);

        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(DARK_BLUE);

        java.util.List<CouponRow> coupons = loadCoupons();
        DefaultListModel<String> couponModel = new DefaultListModel<>();
        for (CouponRow c : coupons) {
            couponModel.addElement(c.code + " - " + c.discount);
        }
        JList<String> couponList = new JList<>(couponModel);
        couponList.setBackground(new Color(255, 255, 255, 150));
        couponList.setFont(TEXT_FONT);
        JScrollPane scrollPane = new JScrollPane(couponList);

        JButton closeBtn = new JButton("Close");
        styleButton(closeBtn, BLACK);
        closeBtn.addActionListener(e -> dialog.dispose());

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(closeBtn, BorderLayout.SOUTH);

        dialog.add(panel);
        dialog.setVisible(true);
    }

    private static void showRemoveCouponDialog() {
        JDialog dialog = new JDialog(frame, "Remove Coupon", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(400, 200);
        dialog.setLocationRelativeTo(frame);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBackground(DARK_BLUE);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel codeLabel = new JLabel("Coupon Code:");
        codeLabel.setForeground(WHITE);
        codeLabel.setFont(TEXT_FONT);
        gbc.gridx = 0; gbc.gridy = 0;
        panel.add(codeLabel, gbc);

        JTextField codeField = new JTextField(20);
        gbc.gridx = 1;
        panel.add(codeField, gbc);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setBackground(DARK_BLUE);
        
        JButton removeBtn = new JButton("Remove");
        styleButton(removeBtn, ORANGE);
        removeBtn.addActionListener(e -> {
            String code = codeField.getText().trim();
            if (code.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Coupon code is required!");
                return;
            }

            if (removeCouponFromFile(code)) {
                JOptionPane.showMessageDialog(dialog, "Coupon removed successfully!");
                dialog.dispose();
            } else {
                JOptionPane.showMessageDialog(dialog, "Coupon not found!");
            }
        });

        JButton cancelBtn = new JButton("Cancel");
        styleButton(cancelBtn, BLACK);
        cancelBtn.addActionListener(e -> dialog.dispose());

        buttonPanel.add(removeBtn);
        buttonPanel.add(cancelBtn);

        dialog.add(panel, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    // ---------------------- UTILITY METHODS ----------------------
    private static boolean updateProductInFile(String filename, String name, String field, String value) {
        java.util.List<String> lines = new ArrayList<>();
        boolean found = false;
        
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1 && parts[0].equalsIgnoreCase(name)) {
                    found = true;
                    // Update the appropriate field
                    if (field.equals("Price") && parts.length >= 3) {
                        parts[2] = value;
                    } else if (field.equals("Quantity") && parts.length >= 4) {
                        parts[3] = value;
                    } else if (field.equals("Category") && parts.length >= 2) {
                        parts[1] = value;
                    }
                    line = String.join(",", parts);
                }
                lines.add(line);
            }
        } catch (IOException e) {
            return false;
        }

        if (!found) return false;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static boolean removeProductFromFile(String filename, String name) {
        java.util.List<String> lines = new ArrayList<>();
        boolean found = false;
        
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1 && parts[0].equalsIgnoreCase(name)) {
                    found = true;
                    continue; // skip this line
                }
                lines.add(line);
            }
        } catch (IOException e) {
            return false;
        }

        if (!found) return false;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static boolean removeCouponFromFile(String code) {
        java.util.List<String> lines = new ArrayList<>();
        boolean found = false;
        
        try (BufferedReader br = new BufferedReader(new FileReader("coupon.txt"))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 1 && parts[0].equalsIgnoreCase(code)) {
                    found = true;
                    continue; // skip this line
                }
                lines.add(line);
            }
        } catch (IOException e) {
            return false;
        }

        if (!found) return false;

        try (BufferedWriter bw = new BufferedWriter(new FileWriter("coupon.txt"))) {
            for (String l : lines) {
                bw.write(l);
                bw.newLine();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    private static String generateCouponCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random rand = new Random();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 8; i++) {
            sb.append(chars.charAt(rand.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private static java.util.List<CouponRow> loadCoupons() {
        java.util.List<CouponRow> list = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader("coupon.txt"))) {
            String ln;
            while ((ln = br.readLine()) != null) {
                String[] p = ln.split(",");
                if (p.length >= 2) {
                    list.add(new CouponRow(p[0], p[1]));
                }
            }
        } catch (IOException ignored) {}
        return list;
    }

    // ---------------------- DATA CLASSES ----------------------
    private static class CouponRow {
        String code, discount;
        CouponRow(String c, String d) { code = c; discount = d; }
    }
}

