package com.foodbuddy.ui;

import javax.swing.*;
import javax.swing.border.AbstractBorder;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.geom.RoundRectangle2D;
import java.text.SimpleDateFormat;
import java.util.Date;

public class UITheme {

    // ==========================================
    // Color Palette (Polished Dark Dashboard)
    // ==========================================
    public static final Color BG_DARK = new Color(0x0F, 0x17, 0x2A);          // #0F172A - Deep Canvas Navy
    public static final Color SIDEBAR_BG = new Color(0x0A, 0x10, 0x1D);       // #0A101D - Deeper Blue/Black
    public static final Color CARD_BG = new Color(0x16, 0x22, 0x35);          // #162235 - Surface Card Navy
    public static final Color CARD_HOVER_BG = new Color(0x1D, 0x2C, 0x44);    // #1D2C44
    public static final Color BORDER_COLOR = new Color(0x23, 0x33, 0x4A);     // #23334A - Subtle Separator
    public static final Color BORDER_LIGHT = new Color(0x31, 0x44, 0x61);     // #314461
    public static final Color INPUT_BG = new Color(0x0C, 0x14, 0x24);         // #0C1424 - Input Field
    public static final Color INPUT_BORDER = new Color(0x28, 0x39, 0x52);     // #283952

    // Accents
    public static final Color PRIMARY = new Color(0x10, 0xB9, 0x81);           // #10B981 - Emerald Green
    public static final Color PRIMARY_HOVER = new Color(0x05, 0x96, 0x69);     // #059669
    public static final Color SECONDARY = new Color(0x38, 0xBD, 0xF8);         // #38BDF8 - Cyan
    public static final Color SECONDARY_HOVER = new Color(0x02, 0x84, 0xC7);   // #0284C7
    public static final Color PURPLE_ACCENT = new Color(0x81, 0x8C, 0xF8);     // #818CF8 - Indigo
    public static final Color AMBER = new Color(0xF5, 0x9E, 0x0B);            // #F59E0B - Amber Warning
    public static final Color DANGER = new Color(0xEF, 0x44, 0x44);           // #EF4444 - Soft Red
    public static final Color DANGER_HOVER = new Color(0xDC, 0x26, 0x26);

    // Text Hierarchy
    public static final Color TEXT_MAIN = new Color(0xF8, 0xFA, 0xFC);        // #F8FAFC - Pure White
    public static final Color TEXT_MUTED = new Color(0x94, 0xA3, 0xB8);       // #94A3B8 - Slate Gray
    public static final Color TEXT_DIM = new Color(0x64, 0x74, 0x8B);         // #64748B - Dark Slate

    // Table Colors
    public static final Color TABLE_HEADER_BG = new Color(0x12, 0x1C, 0x2C);  // Dark flat header
    public static final Color TABLE_ROW_EVEN = new Color(0x16, 0x22, 0x35);   // Card surface
    public static final Color TABLE_ROW_ODD = new Color(0x11, 0x1A, 0x28);    // Slightly darker alternate
    public static final Color TABLE_SELECTION_BG = new Color(0x1E, 0x3A, 0x5F); // Focus row
    public static final Color TABLE_GRID_LINE = new Color(0x1C, 0x29, 0x3D);   // Very soft subtle separator

    // Status Badge Palette
    public static final Color BADGE_AVAIL_BG = new Color(0x06, 0x4E, 0x3B);   // Dark Emerald
    public static final Color BADGE_AVAIL_FG = new Color(0x34, 0xD3, 0x99);   // Light Mint
    public static final Color BADGE_CLAIM_BG = new Color(0x22, 0x2E, 0x40);   // Dark Slate
    public static final Color BADGE_CLAIM_FG = new Color(0x94, 0xA3, 0xB8);   // Muted Slate
    public static final Color BADGE_PEND_BG = new Color(0x78, 0x35, 0x0F);    // Dark Amber
    public static final Color BADGE_PEND_FG = new Color(0xFB, 0xBF, 0x24);    // Light Amber
    public static final Color BADGE_REJ_BG = new Color(0x7F, 0x1D, 0x1D);     // Dark Red
    public static final Color BADGE_REJ_FG = new Color(0xF8, 0x71, 0x71);     // Light Coral

    // ==========================================
    // Typography System
    // ==========================================
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 22);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 15);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_REGULAR = new Font("Segoe UI", Font.PLAIN, 13);
    public static final Font FONT_BOLD = new Font("Segoe UI", Font.BOLD, 13);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 11);
    public static final Font FONT_STAT_VAL = new Font("Segoe UI", Font.BOLD, 28);
    public static final Font FONT_STAT_LABEL = new Font("Segoe UI", Font.BOLD, 11);
    public static final Font FONT_TABLE_HEADER = new Font("Segoe UI", Font.BOLD, 12);

    // ==========================================
    // Vector Icon Engine (Zero External Dependencies)
    // ==========================================
    public static class VectorIcon implements Icon {
        public enum Type {
            DASHBOARD, DONATIONS, DONORS, RECIPIENTS, REQUESTS, ABOUT,
            SEARCH, REFRESH, PLUS, EDIT, DELETE, CHECK, CROSS, CLOCK,
            FOOD_BOWL, STAR, USER, ARROW_RIGHT
        }

        private final Type type;
        private final int size;
        private final Color fixedColor;

        public VectorIcon(Type type, int size) {
            this(type, size, null);
        }

        public VectorIcon(Type type, int size, Color fixedColor) {
            this.type = type;
            this.size = size;
            this.fixedColor = fixedColor;
        }

        @Override
        public int getIconWidth() {
            return size;
        }

        @Override
        public int getIconHeight() {
            return size;
        }

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

            Color paintColor = fixedColor != null ? fixedColor : (c != null ? c.getForeground() : TEXT_MAIN);
            g2.setColor(paintColor);
            g2.translate(x, y);

            // Scale factor to map standard 18x18 icon coordinate system to desired size
            double scale = size / 18.0;
            g2.scale(scale, scale);

            float strokeWidth = 1.8f;
            g2.setStroke(new BasicStroke(strokeWidth, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));

            switch (type) {
                case DASHBOARD:
                    // 4 neat rounded squares (grid)
                    g2.drawRoundRect(2, 2, 6, 6, 2, 2);
                    g2.drawRoundRect(10, 2, 6, 6, 2, 2);
                    g2.drawRoundRect(2, 10, 6, 6, 2, 2);
                    g2.drawRoundRect(10, 10, 6, 6, 2, 2);
                    break;

                case DONATIONS:
                case FOOD_BOWL:
                    // Food bowl with chopsticks / steam
                    g2.drawArc(2, 6, 14, 10, 180, 180);
                    g2.drawLine(2, 11, 16, 11);
                    // Steam strokes
                    g2.drawLine(6, 2, 6, 4);
                    g2.drawLine(9, 1, 9, 4);
                    g2.drawLine(12, 2, 12, 4);
                    break;

                case DONORS:
                case USER:
                    // Person with donor heart
                    g2.drawOval(6, 2, 6, 6); // Head
                    // Shoulders arc
                    g2.drawArc(3, 9, 12, 9, 0, 180);
                    break;

                case RECIPIENTS:
                    // Two welcoming open hands
                    g2.drawArc(1, 4, 8, 10, 200, 130);
                    g2.drawArc(9, 4, 8, 10, 210, -130);
                    g2.drawLine(6, 14, 12, 14);
                    // Heart inside
                    g2.drawOval(7, 3, 4, 4);
                    break;

                case REQUESTS:
                    // Clipboard with checkmarks
                    g2.drawRoundRect(3, 3, 12, 13, 2, 2);
                    g2.drawRoundRect(6, 1, 6, 3, 1, 1);
                    g2.drawLine(6, 7, 12, 7);
                    g2.drawLine(6, 10, 12, 10);
                    g2.drawLine(6, 13, 10, 13);
                    break;

                case ABOUT:
                    // Clean information circle
                    g2.drawOval(2, 2, 14, 14);
                    g2.fillOval(8, 5, 2, 2); // i dot
                    g2.drawLine(9, 8, 9, 13);
                    g2.drawLine(8, 8, 9, 8);
                    g2.drawLine(7, 13, 11, 13);
                    break;

                case SEARCH:
                    // Magnifying glass
                    g2.drawOval(3, 3, 8, 8);
                    g2.drawLine(10, 10, 15, 15);
                    break;

                case REFRESH:
                    // Circular refresh arrow
                    g2.drawArc(2, 2, 14, 14, 40, 270);
                    // Arrowhead
                    g2.drawLine(12, 2, 15, 3);
                    g2.drawLine(15, 3, 14, 7);
                    break;

                case PLUS:
                    // Plus sign with rounded cap
                    g2.drawLine(9, 4, 9, 14);
                    g2.drawLine(4, 9, 14, 9);
                    break;

                case EDIT:
                    // Pencil
                    g2.drawLine(3, 15, 6, 15);
                    g2.drawLine(3, 15, 3, 12);
                    g2.drawLine(3, 12, 12, 3);
                    g2.drawLine(6, 15, 15, 6);
                    g2.drawLine(12, 3, 15, 6);
                    break;

                case DELETE:
                    // Trash can
                    g2.drawRoundRect(4, 5, 10, 11, 2, 2);
                    g2.drawLine(2, 5, 16, 5);
                    g2.drawRoundRect(7, 2, 4, 3, 1, 1);
                    g2.drawLine(7, 8, 7, 13);
                    g2.drawLine(11, 8, 11, 13);
                    break;

                case CHECK:
                    // Checkmark
                    g2.drawLine(3, 9, 7, 13);
                    g2.drawLine(7, 13, 15, 4);
                    break;

                case CROSS:
                    // X Mark
                    g2.drawLine(4, 4, 14, 14);
                    g2.drawLine(14, 4, 4, 14);
                    break;

                case CLOCK:
                    // Clock face with hands
                    g2.drawOval(2, 2, 14, 14);
                    g2.drawLine(9, 9, 9, 5);
                    g2.drawLine(9, 9, 13, 9);
                    break;

                case STAR:
                    g2.drawOval(3, 3, 12, 12);
                    g2.fillOval(7, 7, 4, 4);
                    break;

                case ARROW_RIGHT:
                    g2.drawLine(4, 9, 14, 9);
                    g2.drawLine(10, 5, 14, 9);
                    g2.drawLine(10, 13, 14, 9);
                    break;
            }

            g2.dispose();
        }
    }

    // ==========================================
    // Custom Panels & Borders
    // ==========================================
    public static class RoundedBorder extends AbstractBorder {
        private final Color color;
        private final int radius;
        private final int thickness;

        public RoundedBorder(Color color, int radius, int thickness) {
            this.color = color;
            this.radius = radius;
            this.thickness = thickness;
        }

        @Override
        public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.setStroke(new BasicStroke(thickness));
            g2.drawRoundRect(x + thickness / 2, y + thickness / 2,
                    width - thickness, height - thickness, radius, radius);
            g2.dispose();
        }

        @Override
        public Insets getBorderInsets(Component c) {
            return new Insets(radius / 2, radius / 2, radius / 2, radius / 2);
        }

        @Override
        public Insets getBorderInsets(Component c, Insets insets) {
            insets.left = insets.top = insets.right = insets.bottom = radius / 2;
            return insets;
        }
    }

    public static class RoundedPanel extends JPanel {
        private final int cornerRadius;
        private final Color backgroundColor;
        private Color borderColor;

        public RoundedPanel(int radius, Color bgColor) {
            this(radius, bgColor, BORDER_COLOR);
        }

        public RoundedPanel(int radius, Color bgColor, Color borderColor) {
            super();
            this.cornerRadius = radius;
            this.backgroundColor = bgColor;
            this.borderColor = borderColor;
            setOpaque(false);
        }

        public void setBorderColor(Color color) {
            this.borderColor = color;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            // Fill background
            g2.setColor(backgroundColor);
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);

            // Subtle border
            if (borderColor != null) {
                g2.setColor(borderColor);
                g2.setStroke(new BasicStroke(1f));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
            }
            g2.dispose();
            super.paintComponent(g);
        }
    }

    // ==========================================
    // Button Factories (With Vector Icons)
    // ==========================================
    public static JButton createPrimaryButton(String text, VectorIcon.Type iconType) {
        JButton btn = createPrimaryButton(text);
        if (iconType != null) {
            btn.setIcon(new VectorIcon(iconType, 16, Color.WHITE));
            btn.setIconTextGap(8);
        }
        return btn;
    }

    public static JButton createPrimaryButton(String text) {
        return createStyledButton(text, PRIMARY, PRIMARY_HOVER, Color.WHITE, 10);
    }

    public static JButton createSecondaryButton(String text, VectorIcon.Type iconType) {
        JButton btn = createSecondaryButton(text);
        if (iconType != null) {
            btn.setIcon(new VectorIcon(iconType, 15, TEXT_MUTED));
            btn.setIconTextGap(8);
        }
        return btn;
    }

    public static JButton createSecondaryButton(String text) {
        return createStyledButton(text, new Color(0x1A, 0x27, 0x3D), new Color(0x23, 0x36, 0x54), TEXT_MAIN, 10);
    }

    public static JButton createCyanButton(String text, VectorIcon.Type iconType) {
        JButton btn = createCyanButton(text);
        if (iconType != null) {
            btn.setIcon(new VectorIcon(iconType, 15, BG_DARK));
            btn.setIconTextGap(8);
        }
        return btn;
    }

    public static JButton createCyanButton(String text) {
        return createStyledButton(text, SECONDARY, SECONDARY_HOVER, BG_DARK, 10);
    }

    public static JButton createDangerButton(String text, VectorIcon.Type iconType) {
        JButton btn = createDangerButton(text);
        if (iconType != null) {
            btn.setIcon(new VectorIcon(iconType, 15, Color.WHITE));
            btn.setIconTextGap(8);
        }
        return btn;
    }

    public static JButton createDangerButton(String text) {
        return createStyledButton(text, DANGER, DANGER_HOVER, Color.WHITE, 10);
    }

    public static JButton createStyledButton(String text, Color bg, Color hoverBg, Color fg, int radius) {
        JButton btn = new JButton(text) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                Color currentBg = bg;
                if (!isEnabled()) {
                    currentBg = new Color(bg.getRed(), bg.getGreen(), bg.getBlue(), 80);
                } else if (getModel().isPressed()) {
                    currentBg = hoverBg.darker();
                } else if (getModel().isRollover()) {
                    currentBg = hoverBg;
                }

                g2.setColor(currentBg);
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);
                g2.dispose();
                super.paintComponent(g);
            }
        };

        btn.setFont(FONT_BOLD);
        btn.setForeground(fg);
        btn.setContentAreaFilled(false);
        btn.setFocusPainted(false);
        btn.setBorderPainted(false);
        btn.setOpaque(false);
        btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btn.setBorder(new EmptyBorder(8, 16, 8, 16));
        return btn;
    }

    // ==========================================
    // Input Fields & Controls
    // ==========================================
    public static JTextField createTextField(String placeholder) {
        JTextField tf = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !(FocusManager.getCurrentKeyboardFocusManager().getFocusOwner() == this)) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(TEXT_DIM);
                    g2.setFont(getFont());
                    Insets insets = getInsets();
                    FontMetrics fm = g2.getFontMetrics();
                    g2.drawString(placeholder, insets.left, (getHeight() - fm.getHeight()) / 2 + fm.getAscent());
                    g2.dispose();
                }
            }
        };

        tf.setBackground(INPUT_BG);
        tf.setForeground(TEXT_MAIN);
        tf.setCaretColor(PRIMARY);
        tf.setFont(FONT_REGULAR);
        tf.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(INPUT_BORDER, 8, 1),
            new EmptyBorder(8, 12, 8, 12)
        ));
        return tf;
    }

    public static <T> JComboBox<T> createComboBox(T[] items) {
        JComboBox<T> cb = new JComboBox<>(items);
        styleComboBox(cb);
        return cb;
    }

    public static <T> JComboBox<T> createComboBox(DefaultComboBoxModel<T> model) {
        JComboBox<T> cb = new JComboBox<>(model);
        styleComboBox(cb);
        return cb;
    }

    private static <T> void styleComboBox(JComboBox<T> cb) {
        cb.setBackground(INPUT_BG);
        cb.setForeground(TEXT_MAIN);
        cb.setFont(FONT_REGULAR);
        cb.setFocusable(false);
        cb.setBorder(BorderFactory.createCompoundBorder(
            new RoundedBorder(INPUT_BORDER, 8, 1),
            new EmptyBorder(2, 4, 2, 4)
        ));
        cb.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel l = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, false);
                l.setBorder(new EmptyBorder(8, 12, 8, 12));
                l.setFont(FONT_REGULAR);
                if (isSelected) {
                    l.setBackground(PRIMARY);
                    l.setForeground(Color.WHITE);
                } else {
                    l.setBackground(INPUT_BG);
                    l.setForeground(TEXT_MAIN);
                }
                return l;
            }
        });
    }

    // ==========================================
    // Table Styling (No White Borders, Dark Theme)
    // ==========================================
    public static void styleTable(JTable table) {
        table.setRowHeight(38);
        table.setBackground(TABLE_ROW_EVEN);
        table.setForeground(TEXT_MAIN);
        table.setFont(FONT_REGULAR);
        table.setSelectionBackground(TABLE_SELECTION_BG);
        table.setSelectionForeground(Color.WHITE);
        table.setFillsViewportHeight(true);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.setShowGrid(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(TABLE_GRID_LINE);

        // Header Styling
        JTableHeader header = table.getTableHeader();
        header.setPreferredSize(new Dimension(header.getPreferredSize().width, 42));
        header.setBackground(TABLE_HEADER_BG);
        header.setForeground(TEXT_MUTED);
        header.setFont(FONT_TABLE_HEADER);
        header.setReorderingAllowed(false);
        header.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR));

        // Header renderer to eliminate any Windows Look & Feel white 3D bevels
        header.setDefaultRenderer(new TableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasFocus, int row, int col) {
                JLabel lbl = new JLabel(val != null ? val.toString() : "");
                lbl.setFont(FONT_TABLE_HEADER);
                lbl.setForeground(TEXT_MUTED);
                lbl.setBackground(TABLE_HEADER_BG);
                lbl.setOpaque(true);
                lbl.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER_COLOR),
                    new EmptyBorder(0, 12, 0, 12)
                ));

                // Align center if col indicates ID, Quantity, Status, Date
                String headerStr = (val != null) ? val.toString().toUpperCase() : "";
                if (headerStr.contains("ID") || headerStr.contains("QTY") || headerStr.contains("QUANTITY")
                        || headerStr.contains("STATUS") || headerStr.contains("DATE") || headerStr.contains("TOTAL")) {
                    lbl.setHorizontalAlignment(SwingConstants.CENTER);
                } else {
                    lbl.setHorizontalAlignment(SwingConstants.LEFT);
                }
                return lbl;
            }
        });

        // Alternating row renderer with zero focus border
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasFocus, int row, int col) {
                // Pass hasFocus = false to prevent Swing drawing bright white focus rectangles
                JLabel l = (JLabel) super.getTableCellRendererComponent(t, val, isSel, false, row, col);
                l.setFont(FONT_REGULAR);
                l.setBorder(new EmptyBorder(0, 12, 0, 12));

                if (isSel) {
                    l.setBackground(TABLE_SELECTION_BG);
                    l.setForeground(Color.WHITE);
                } else {
                    l.setBackground(row % 2 == 0 ? TABLE_ROW_EVEN : TABLE_ROW_ODD);
                    l.setForeground(TEXT_MAIN);
                }
                return l;
            }
        });
    }

    // Custom cell renderer for center-aligned text (e.g. IDs, Quantities)
    public static class CenterTableCellRenderer extends DefaultTableCellRenderer {
        public CenterTableCellRenderer() {
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        @Override
        public Component getTableCellRendererComponent(JTable t, Object val, boolean isSel, boolean hasFocus, int row, int col) {
            JLabel l = (JLabel) super.getTableCellRendererComponent(t, val, isSel, false, row, col);
            l.setHorizontalAlignment(SwingConstants.CENTER);
            l.setFont(FONT_REGULAR);
            l.setBorder(new EmptyBorder(0, 8, 0, 8));

            if (isSel) {
                l.setBackground(TABLE_SELECTION_BG);
                l.setForeground(Color.WHITE);
            } else {
                l.setBackground(row % 2 == 0 ? TABLE_ROW_EVEN : TABLE_ROW_ODD);
                l.setForeground(TEXT_MAIN);
            }
            return l;
        }
    }

    // Status Badge Cell Renderer (Custom Painted Rounded Pill)
    public static class StatusBadgeRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            String status = (value != null) ? value.toString().toUpperCase().trim() : "";

            Color bg;
            Color fg;

            switch (status) {
                case "AVAILABLE":
                    bg = BADGE_AVAIL_BG;
                    fg = BADGE_AVAIL_FG;
                    break;
                case "CLAIMED":
                    bg = BADGE_CLAIM_BG;
                    fg = BADGE_CLAIM_FG;
                    break;
                case "PENDING":
                    bg = BADGE_PEND_BG;
                    fg = BADGE_PEND_FG;
                    break;
                case "APPROVED":
                    bg = BADGE_AVAIL_BG;
                    fg = BADGE_AVAIL_FG;
                    break;
                case "REJECTED":
                    bg = BADGE_REJ_BG;
                    fg = BADGE_REJ_FG;
                    break;
                default:
                    bg = BADGE_CLAIM_BG;
                    fg = TEXT_MUTED;
            }

            Color cellBg = isSelected ? TABLE_SELECTION_BG : (row % 2 == 0 ? TABLE_ROW_EVEN : TABLE_ROW_ODD);

            return new JPanel() {
                {
                    setOpaque(true);
                    setBackground(cellBg);
                }

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

                    int badgeHeight = 22;
                    int badgeWidth = Math.max(88, g2.getFontMetrics(FONT_STAT_LABEL).stringWidth(status) + 20);
                    int x = (getWidth() - badgeWidth) / 2;
                    int y = (getHeight() - badgeHeight) / 2;

                    // Draw rounded pill background
                    g2.setColor(bg);
                    g2.fillRoundRect(x, y, badgeWidth, badgeHeight, 12, 12);

                    // Draw status text
                    g2.setColor(fg);
                    g2.setFont(FONT_STAT_LABEL);
                    FontMetrics fm = g2.getFontMetrics();
                    int textX = x + (badgeWidth - fm.stringWidth(status)) / 2;
                    int textY = y + (badgeHeight - fm.getHeight()) / 2 + fm.getAscent();
                    g2.drawString(status, textX, textY);

                    g2.dispose();
                }
            };
        }
    }

    // Scrollpane Styler with Custom Smooth Dark Scrollbar
    public static JScrollPane createScrollPane(Component view) {
        JScrollPane sp = new JScrollPane(view);
        sp.setBackground(BG_DARK);
        sp.getViewport().setBackground(BG_DARK);
        sp.setBorder(BorderFactory.createLineBorder(BORDER_COLOR, 1));

        // Dark Vertical Scrollbar
        sp.getVerticalScrollBar().setBackground(BG_DARK);
        sp.getVerticalScrollBar().setPreferredSize(new Dimension(8, 0));
        sp.getVerticalScrollBar().setUnitIncrement(18);
        sp.getVerticalScrollBar().setUI(new BasicScrollBarUI() {
            @Override
            protected void configureScrollBarColors() {
                this.thumbColor = new Color(0x2A, 0x3D, 0x56);
                this.trackColor = BG_DARK;
            }

            @Override
            protected JButton createDecreaseButton(int orientation) {
                return createZeroButton();
            }

            @Override
            protected JButton createIncreaseButton(int orientation) {
                return createZeroButton();
            }

            private JButton createZeroButton() {
                JButton btn = new JButton();
                btn.setPreferredSize(new Dimension(0, 0));
                btn.setMinimumSize(new Dimension(0, 0));
                btn.setMaximumSize(new Dimension(0, 0));
                return btn;
            }

            @Override
            protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
                if (thumbBounds.isEmpty() || !sp.getVerticalScrollBar().isEnabled()) return;
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(isThumbRollover() ? new Color(0x3B, 0x51, 0x70) : thumbColor);
                g2.fillRoundRect(thumbBounds.x + 1, thumbBounds.y + 2, thumbBounds.width - 2, thumbBounds.height - 4, 6, 6);
                g2.dispose();
            }
        });

        // Dark Horizontal Scrollbar
        sp.getHorizontalScrollBar().setBackground(BG_DARK);
        sp.getHorizontalScrollBar().setPreferredSize(new Dimension(0, 8));
        sp.getHorizontalScrollBar().setUnitIncrement(18);

        return sp;
    }

    // ==========================================
    // Date Formatter Helper
    // ==========================================
    private static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat("dd MMM yyyy, HH:mm");

    public static String formatDate(Date date) {
        if (date == null) return "—";
        return DATE_FORMATTER.format(date);
    }
}
