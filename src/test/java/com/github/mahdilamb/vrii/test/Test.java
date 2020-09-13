package com.github.mahdilamb.vrii.test;

import com.github.mahdilamb.vrii.*;
import com.github.mahdilamb.vrii.Renderer;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;
import java.awt.*;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.File;
import java.io.IOException;

class ColorMapCellRenderer extends BasicComboBoxRenderer {
    @Override
    public Component getListCellRendererComponent(
            JList list,
            Object value,
            int index,
            boolean isSelected,
            boolean cellHasFocus) {
        final ColorMap colorMap = (ColorMap) value;
        final ColorMapCellRenderer out = (ColorMapCellRenderer) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        out.setText(null);

        out.setIcon(new Icon() {
            @Override
            public void paintIcon(Component c, Graphics g, int x, int y) {
                final Graphics2D g2d = (Graphics2D) g;
                for (int i = 0; i < getIconWidth(); i++) {
                    g2d.setColor(colorMap.getColorAt(i));
                    g2d.drawLine(i, 0, i, getIconHeight());
                }
            }

            @Override
            public int getIconWidth() {
                return 256;
            }

            @Override
            public int getIconHeight() {
                return 20;
            }
        });
        return out;
    }

}

public class Test {
    public static void main(String... args) throws IOException {
        final JFrame frame = new JFrame();
        frame.setLayout(new GridBagLayout());
        final GridBagConstraints fGBC = new GridBagConstraints();
        fGBC.gridy = 0;
        fGBC.weightx = 1;
        frame.add(Renderer.getCanvas());
        fGBC.weightx = 0;
        final JPanel controls = new JPanel();
        final GridBagConstraints cGBC = new GridBagConstraints();
        controls.setLayout(new GridBagLayout());
        cGBC.gridx = 0;
        cGBC.weighty = 1;
        cGBC.fill = GridBagConstraints.BOTH;
        controls.add(new JComboBox<MosaicVolumeSource>() {{
                         addItem(new MosaicVolumeSource(
                                 "Brain - Water",
                                 new File("D:\\Documents\\idea\\VolumeRenderingMark2\\src\\main\\resources\\volumes\\sagittal.png"),
                                 2,
                                 176,
                                 .7f
                         ));
                         addItem(new MosaicVolumeSource(
                                 "Brain - Vessels",
                                 new File("D:\\Documents\\idea\\VolumeRenderingMark2\\src\\main\\resources\\volumes\\vessels.png"),
                                 1,
                                 160,
                                 .65f
                         ));
                         addItem(new MosaicVolumeSource(
                                 "Wrist",
                                 new File("D:\\Documents\\idea\\VolumeRenderingMark2\\src\\main\\resources\\volumes\\handgelenk.jpg"),
                                 4,
                                 316,
                                 1.5f
                         ));
                         addItem(new MosaicVolumeSource(
                                 "Wrist 2",
                                 new File("D:\\Documents\\idea\\VolumeRenderingMark2\\src\\main\\resources\\volumes\\handgelenk2.jpg"),
                                 2,
                                 160,
                                 .5f
                         ));
                         addItem(new MosaicVolumeSource(
                                 "Broccoli",
                                 new File("D:\\Documents\\idea\\VolumeRenderingMark2\\src\\main\\resources\\volumes\\broccoli.png"),
                                 1,
                                 50,
                                 .7f
                         ));
                         addItem(new MosaicVolumeSource(
                                 "Sphere (Anti-aliased)",
                                 new File("D:\\Documents\\idea\\VolumeRenderingMark2\\src\\main\\resources\\volumes\\sphere_antialiased.png"),
                                 16,
                                 256,
                                 1f
                         ));
                         addItem(new MosaicVolumeSource(
                                 "Cube",
                                 new File("D:\\Documents\\idea\\VolumeRenderingMark2\\src\\main\\resources\\volumes\\cuuube.png"),
                                 16,
                                 128,
                                 1f
                         ));
                         addItem(new MosaicVolumeSource(
                                 "Small Sphere",
                                 new File("D:\\Documents\\idea\\VolumeRenderingMark2\\src\\main\\resources\\volumes\\smallsphere.png"),
                                 16,
                                 128,
                                 1f
                         ));
                         addActionListener(e -> {
                             try {
                                 com.github.mahdilamb.vrii.Renderer.setVolume((MosaicVolumeSource) ((JComboBox<MosaicVolumeSource>) e.getSource()).getSelectedItem());
                             } catch (IOException ioException) {
                                 ioException.printStackTrace();
                             }
                         });
                     }},
                cGBC);
        controls.add(new JComboBox<ColorMap>() {{
                         final File directory = new File("D:\\Documents\\idea\\VolumeRenderingMark2\\src\\main\\resources\\colorMappings");
                         for (final File file : directory.listFiles()) {
                             if (file.isDirectory()) {
                                 continue;
                             }
                             addItem(new ColorMap(file));
                         }
                         setRenderer(new ColorMapCellRenderer());
                         addActionListener(e -> {
                             com.github.mahdilamb.vrii.Renderer.setColorMap((ColorMap) ((JComboBox<ColorMap>) e.getSource()).getSelectedItem());
                         });
                         setSelectedIndex(4);
                     }},
                cGBC);
        controls.add(new JSlider(0, 1000, 800) {{
            addChangeListener(e -> {
                com.github.mahdilamb.vrii.Renderer.setOpacityMin(((float) ((JSlider) e.getSource()).getValue()) / 1000);
            });
        }}, cGBC);
        controls.add(new JSlider(0, 1000, 1000) {{
            addChangeListener(e -> {
                com.github.mahdilamb.vrii.Renderer.setOpacityMax(((float) ((JSlider) e.getSource()).getValue()) / 1000);
            });
        }}, cGBC);
        controls.add(
                new JTextField("512") {{
                    addFocusListener(new FocusListener() {
                        @Override
                        public void focusGained(FocusEvent e) {

                        }

                        @Override
                        public void focusLost(FocusEvent e) {
                            try {
                                final int value = Integer.parseInt(((JTextField) e.getSource()).getText());
                                com.github.mahdilamb.vrii.Renderer.setSampleCount(value);
                            } catch (Exception E) {
                                E.printStackTrace();
                            }

                        }
                    });
                }},
                cGBC);
        Renderer.setShader(Program.maxIntensity);


        frame.add(controls);

        frame.addWindowListener(new WindowListener() {
            public void windowOpened(WindowEvent e) {

            }

            public void windowClosing(WindowEvent e) {
                Renderer.getCanvas().destroy();
                System.exit(0);
            }

            public void windowClosed(WindowEvent e) {

            }

            public void windowIconified(WindowEvent e) {

            }

            public void windowDeiconified(WindowEvent e) {

            }

            public void windowActivated(WindowEvent e) {

            }

            public void windowDeactivated(WindowEvent e) {

            }
        });
        frame.pack();
        frame.setVisible(true);

    }
}
