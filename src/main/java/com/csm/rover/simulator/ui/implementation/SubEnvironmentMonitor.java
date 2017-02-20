package com.csm.rover.simulator.ui.implementation;

import com.csm.rover.simulator.environments.EnvironmentIO;
import com.csm.rover.simulator.environments.EnvironmentRegistry;
import com.csm.rover.simulator.environments.PlatformEnvironment;
import com.csm.rover.simulator.environments.sub.AquaticEnvironment;
import com.csm.rover.simulator.objects.io.EnvrioFileFilter;
import com.csm.rover.simulator.objects.util.DecimalPoint3D;
import com.csm.rover.simulator.objects.FreeThread;
import com.csm.rover.simulator.platforms.PlatformState;
import com.csm.rover.simulator.platforms.sub.SubObject;
import com.csm.rover.simulator.ui.visual.PopulatorDisplayFunction;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSlider;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import net.miginfocom.swing.MigLayout;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.awt.image.BufferedImage;
import java.util.List;

@FrameMarker(name = "Aquatic Map Monitor", platform = "Sub")
class SubEnvironmentMonitor extends EnvironmentDisplay {

    private JPanel content;
    private List<AquaticMapDisplay> displays;
    private DecimalPoint3D focus;

    SubEnvironmentMonitor(){
        super("Sub");
        setTitle("Aquatic Environment Monitor");
        setSize(500, 500);

        content = new JPanel();
        content.setBackground(Color.WHITE);
        content.setLayout(new BorderLayout());
        this.setContentPane(content);

        displays = new ArrayList<>();
    }


    @Override
    protected void update() {
        for (AquaticMapDisplay display : displays){
            display.setFocusPoint(focus);
            display.updateX();
        }
    }

    @Override
    protected void doSetEnvironment(PlatformEnvironment environment) {
        JPanel globalPnl = new JPanel();
        globalPnl.setBackground(Color.WHITE);
        globalPnl.setLayout(new MigLayout("", "[grow,fill][grow,fill]", "[fill][grow,fill]"));
        JLabel label1 = new JLabel("Top Down View");
        label1.setHorizontalAlignment(SwingConstants.CENTER);
        label1.setFont(FontFunctions.bigger(FontFunctions.bold(label1.getFont())));
        globalPnl.add(label1, "cell 0 0");
        AquaticMapDisplay display = new AquaticMapDisplay((AquaticEnvironment)environment, AquaticMapDisplay.Slice.Z);
        globalPnl.add(display, "cell 0 1");
        JLabel label2 = new JLabel("Front View");
        label2.setHorizontalAlignment(SwingConstants.CENTER);
        label2.setFont(FontFunctions.bigger(FontFunctions.bold(label2.getFont())));
        globalPnl.add(label2, "cell 1 0");
        AquaticMapDisplay display2 = new AquaticMapDisplay((AquaticEnvironment)environment, AquaticMapDisplay.Slice.Y);
        globalPnl.add(display2, "cell 1 1");
        content.add(globalPnl, BorderLayout.CENTER);
    }


}

class AquaticMapDisplay extends JPanel {

    private final String platform_type = "Sub";

    private static Map<Slice, Map<Integer, DecimalPoint3D>> panMovesMap;
    static {
        panMovesMap = new HashMap<>();
        panMovesMap.put(Slice.X, new TreeMap<>());
        panMovesMap.get(Slice.X).put(SwingConstants.WEST, new DecimalPoint3D(0, -1, 0));
        panMovesMap.get(Slice.X).put(SwingConstants.EAST, new DecimalPoint3D(0, 1, 0));
        panMovesMap.get(Slice.X).put(SwingConstants.NORTH, new DecimalPoint3D(0, 0, 1));
        panMovesMap.get(Slice.X).put(SwingConstants.SOUTH, new DecimalPoint3D(0, 0, -1));
        panMovesMap.put(Slice.Y, new TreeMap<>());
        panMovesMap.get(Slice.Y).put(SwingConstants.WEST, new DecimalPoint3D(0, 0, -1));
        panMovesMap.get(Slice.Y).put(SwingConstants.EAST, new DecimalPoint3D(0, 0, 1));
        panMovesMap.get(Slice.Y).put(SwingConstants.NORTH, new DecimalPoint3D(1, 0, 0));
        panMovesMap.get(Slice.Y).put(SwingConstants.SOUTH, new DecimalPoint3D(-1, 0, 0));
        panMovesMap.put(Slice.Z, new TreeMap<>());
        panMovesMap.get(Slice.Z).put(SwingConstants.WEST, new DecimalPoint3D(-1, 0, 0));
        panMovesMap.get(Slice.Z).put(SwingConstants.EAST, new DecimalPoint3D(1, 0, 0));
        panMovesMap.get(Slice.Z).put(SwingConstants.NORTH, new DecimalPoint3D(0, 1, 0));
        panMovesMap.get(Slice.Z).put(SwingConstants.SOUTH, new DecimalPoint3D(0, -1, 0));
    }

    enum Slice { X, Y, Z }
    private final Slice slice;

    private JPanel mapHolder;
    private AquaticSlicePanel mapSlice;

    private DecimalPoint3D focus = new DecimalPoint3D(0, 0, 0);
    private List<SubIcon> subIcons;

    AquaticMapDisplay(AquaticEnvironment environment, Slice slice){
        this.slice = slice;
        this.setOpaque(false);
        this.setLayout(new BorderLayout());
        this.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                redraw();
            }
        });

        mapHolder = new JPanel();
        mapHolder.setLayout(null);
        mapHolder.setBackground(Color.BLACK);
        this.add(mapHolder, BorderLayout.CENTER);

        mapSlice = new AquaticSlicePanel(environment, slice);
        mapSlice.addMouseListener(new MouseAdapter(){
            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == 1){
                    dragMap(e.getPoint());
                }
                if (e.getButton() == 3){
                    showMenu(new Point(e.getXOnScreen()-2, e.getYOnScreen()-2));
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dragMap(null);
            }
        });
        mapSlice.addMouseMotionListener(new MouseMotionAdapter(){
            @Override
            public void mouseDragged(MouseEvent e) {
                dragMap(e.getPoint());
            }
        });
        mapSlice.addMouseWheelListener((e) -> zoomMap(5 * -e.getWheelRotation()));

        getInputMap().put(KeyStroke.getKeyStroke("LEFT"), "pan left");
        getActionMap().put("pan left", ActionBuilder.newAction(() -> setFocusPoint(focus.offset(panMovesMap.get(slice).get(SwingConstants.WEST)))));
        getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), "pan right");
        getActionMap().put("pan right", ActionBuilder.newAction(() -> setFocusPoint(focus.offset(panMovesMap.get(slice).get(SwingConstants.EAST)))));
        getInputMap().put(KeyStroke.getKeyStroke("UP"), "pan up");
        getActionMap().put("pan up", ActionBuilder.newAction(() -> setFocusPoint(focus.offset(panMovesMap.get(slice).get(SwingConstants.NORTH)))));
        getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "pan down");
        getActionMap().put("pan down", ActionBuilder.newAction(() -> setFocusPoint(focus.offset(panMovesMap.get(slice).get(SwingConstants.SOUTH)))));

        subIcons = new ArrayList<>();
        for (SubObject sub : environment.getPlatforms()){
            SubIcon icon = new SubIcon(sub);
            mapSlice.add(icon);
            subIcons.add(icon);
        }
        mapHolder.add(mapSlice);

        JSlider slider = new JSlider();
        slider.setMinimum(0);
        slider.setMaximum(environment.getSize()-1);
        slider.setSnapToTicks(true);
        slider.setMajorTickSpacing(10);
        slider.setMinorTickSpacing(1);
        slider.addChangeListener((e) -> {
            JSlider source = (JSlider)e.getSource();
            setFocusPoint(new DecimalPoint3D(
                    slice == Slice.X ? source.getValue() : focus.getX(),
                    slice == Slice.Y ? source.getValue() : focus.getY(),
                    slice == Slice.Z ? source.getValue() : focus.getZ()
            ));
        });
        slider.setOpaque(false);
        slider.getInputMap().put(KeyStroke.getKeyStroke("LEFT"), "pan left");
        slider.getActionMap().put("pan left", ActionBuilder.newAction(() -> setFocusPoint(focus.offset(panMovesMap.get(slice).get(SwingConstants.WEST)))));
        slider.getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), "pan right");
        slider.getActionMap().put("pan right", ActionBuilder.newAction(() -> setFocusPoint(focus.offset(panMovesMap.get(slice).get(SwingConstants.EAST)))));
        slider.getInputMap().put(KeyStroke.getKeyStroke("UP"), "pan up");
        slider.getActionMap().put("pan up", ActionBuilder.newAction(() -> setFocusPoint(focus.offset(panMovesMap.get(slice).get(SwingConstants.NORTH)))));
        slider.getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "pan down");
        slider.getActionMap().put("pan down", ActionBuilder.newAction(() -> setFocusPoint(focus.offset(panMovesMap.get(slice).get(SwingConstants.SOUTH)))));
        this.add(slider, BorderLayout.SOUTH);
        slider.setValue(environment.getSize()/2);

        redraw();
    }

    private void showMenu(Point p){
        JPopupMenu menu = new JPopupMenu();
        menu.setVisible(false);
        menu.setLocation(p);
        menu.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseExited(MouseEvent e) {
                if (e.getX() <= menu.getX() ||
                        e.getX() >= menu.getX()+menu.getWidth() ||
                        e.getY() <= menu.getY() ||
                        e.getY() >= menu.getY()+menu.getHeight()) {
                    menu.setVisible(false);
                }
            }
        });

        JMenuItem mntmSaveMapFile = new JMenuItem("Save Map File");
        mntmSaveMapFile.addActionListener((e) -> {
            JFileChooser finder = new JFileChooser();
            finder.setFileFilter(new EnvrioFileFilter(platform_type));
            finder.setApproveButtonText("Save");
            finder.setCurrentDirectory(UiConfiguration.getFolder(platform_type));
            int option = finder.showSaveDialog(getParent());
            menu.setVisible(false);
            new FreeThread(0, () -> {
                if (option == JFileChooser.APPROVE_OPTION) {
                    UiConfiguration.changeFolder(platform_type, finder.getCurrentDirectory());
                    EnvironmentIO.saveEnvironment(mapSlice.getEnvironment(), EnvironmentIO.appendSuffix(platform_type, finder.getSelectedFile()));
                }
            }, 1, "SaveDialog");
        });
        menu.add(mntmSaveMapFile);

        for (String pop : mapSlice.getPopulators()){
            JRadioButtonMenuItem item = new JRadioButtonMenuItem("Show " + pop);
            item.setSelected(mapSlice.isPopulatorVisible(pop));
            item.addActionListener((a) -> {
                mapSlice.setPopulatorVisible(pop, item.isSelected());
                menu.setVisible(false);
            });
            menu.add(item);
        }

        mapSlice.setComponentPopupMenu(menu);
    }

    private Optional<Point> last = Optional.empty();
    private void dragMap(Point p){
        if (p == null){
            last = Optional.empty();
        }
        else if (last.isPresent()){
            double dx = last.get().getX() - p.x;
            double dy = p.y - last.get().getY();
            double cx = dx / (double)mapSlice.getResolution();
            double cy = dy / (double)mapSlice.getResolution();
            switch (slice){
                case X:
                    setFocusPoint(new DecimalPoint3D(focus.getX(), focus.getY()+cx, focus.getZ()+cy));
                    break;
                case Y:
                    setFocusPoint(new DecimalPoint3D(focus.getX()+cy, focus.getY(), focus.getZ()+cx));
                    break;
                case Z:
                    setFocusPoint(new DecimalPoint3D(focus.getX()+cx, focus.getY()+cy, focus.getZ()));
                    break;
            }
        }
        else {
            last = Optional.of(p);
        }
    }

    private void zoomMap(int zoom){
        mapSlice.setResolution(mapSlice.getResolution()+zoom);
        redraw();
    }

    private void redraw(){
        double eff_x, eff_y, eff_z;
        switch (slice){
            case X:
                eff_x = focus.getY();
                eff_y = focus.getZ();
                eff_z = focus.getX();
                break;
            case Y:
                eff_x = focus.getZ();
                eff_y = focus.getX();
                eff_z = focus.getY();
                break;
            case Z:
                eff_x = focus.getX();
                eff_y = focus.getY();
                eff_z = focus.getZ();
                break;
            default:
                throw new IllegalStateException("There is no slice");
        }
        mapSlice.setLocation((int)Math.round(mapHolder.getWidth()/2.0-eff_x* mapSlice.getResolution()- mapSlice.getWidth()/2.0),
                (int)Math.round(mapHolder.getHeight()/2.0+eff_y* mapSlice.getResolution() - mapSlice.getHeight()/2.0));
        mapSlice.setSliceDepth((int)Math.round(eff_z));
    }

    void setFocusPoint(DecimalPoint3D point){
        focus = point;
        redraw();
    }

    void updateX() {
        redraw();
        for (SubIcon sub : subIcons){
            sub.updateX(mapSlice.getResolution());
        }
    }
    void updateZ() {
        redraw();
        for (SubIcon sub : subIcons){
            sub.updateZ(mapSlice.getResolution());
        }
    }
}

class AquaticSlicePanel extends JPanel {
    private static final Logger LOG = LogManager.getLogger(AquaticSlicePanel.class);

    private final AquaticMapDisplay.Slice slice;
    private final AquaticEnvironment subMap;

    private Map<String, Boolean> viewPopulators;
    private Map<String, PopulatorDisplayFunction> popDisplays;
    private int slice_depth = 0;
    private int squareResolution = 50;

    AquaticSlicePanel(AquaticEnvironment environment, AquaticMapDisplay.Slice slice){
        this.slice = slice;
        this.subMap = environment;
        this.setBackground(Color.GREEN);
        viewPopulators = new TreeMap<>();
        popDisplays = new TreeMap<>();
        for (String pop : subMap.getPopulators()){
            viewPopulators.put(pop, true);
            popDisplays.put(pop, EnvironmentRegistry.getPopulatorDisplayFunction(subMap.getType(), pop));
        }
        this.setBackground(Color.BLACK);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent arg0) {
                setSize(subMap.getSize() * squareResolution + 1, subMap.getSize() * squareResolution + 1);
            }
        });
        this.setBounds(2, 2, 100, 100);
    }

    //paint the map
    //kind long and ugly, I know, but necessary for performance
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            int horzstart = (-this.getLocation().x / squareResolution - 2);
            int horzend = horzstart + (this.getParent().getWidth() / squareResolution + 4);
            if (horzstart < 0){
                horzstart = 0;
            }
            if (horzend > subMap.getSize()){
                horzend = subMap.getSize();
            }
            int vertstart = (-this.getLocation().y / squareResolution - 2);
            int vertend = vertstart + (this.getParent().getHeight() / squareResolution + 4);
            if (vertstart < 0){
                vertstart = 0;
            }
            if (vertend > subMap.getSize()){
                vertend = subMap.getSize();
            }

            int xstart, xend, ystart, yend, zstart, zend;
            switch (slice){
                case X:
                    xstart = slice_depth;
                    xend = slice_depth + 1;
                    ystart = horzstart;
                    yend = horzend;
                    zstart = vertstart;
                    zend = vertend;
                    break;
                case Y:
                    xstart = horzstart;
                    xend = horzend;
                    ystart = slice_depth;
                    yend = slice_depth + 1;
                    zstart = vertstart;
                    zend = vertend;
                    break;
                case Z:
                    xstart = horzstart;
                    xend = horzend;
                    ystart = vertstart;
                    yend = vertend;
                    zstart = slice_depth;
                    zend = slice_depth + 1;
                    break;
                default:
                    throw new IllegalStateException("There is no slice");
            }
            int fails = 0;
            for (int y = ystart; y < yend; y++){
                for (int x = xstart; x < xend; x++) {
                    for (int z = zstart; z < zend; z++) {
                        try {
                            DecimalPoint3D loc = new DecimalPoint3D(x - subMap.getSize()/2 + 0.5, subMap.getSize()/2 -(y+1) + 0.5, subMap.getSize()/2 - (z+1) + 0.5);
                            Color color = null;
                            for (String pop : viewPopulators.keySet()) {
                                if (viewPopulators.get(pop) && color == null) {
                                    double value = subMap.getPopulatorValue(pop, loc);
                                    color = value == 0 ? null : popDisplays.get(pop).displayFunction(value);
                                }
                            }
                            if (color == null) {
                                int red_green = (int) ((1 - subMap.getDensityAt(loc) / subMap.getMaxDensity()) * 255);
                                color = new Color(red_green, red_green, 255);
                            }

                            int eff_x = getHorizontalAxis(x, y, z);
                            int eff_y = getVerticalAxis(x, y, z);
                            g.setColor(color);
                            g.fillRect(eff_x * squareResolution, eff_y * squareResolution, squareResolution, squareResolution);

                            g.setColor(Color.DARK_GRAY);
                            g.drawRect(eff_x * squareResolution, eff_y * squareResolution, squareResolution, squareResolution);
                        }
                        catch (Exception e) {
                            if (fails > 10) {
                                throw e;
                            } else {
                                LOG.log(Level.WARN, "Failed to draw square [" + x + ", " + y + ", " + z +"]: " + e.getMessage());
                                fails++;
                            }
                        }
                    }
                }
            }
        }
        catch (Exception e) {
            LOG.log(Level.ERROR, "Failed to draw map", e);
        }
    }

    private int getHorizontalAxis(int x, int y, int z){
        switch (slice){
            case Z:
            case Y:
                return x;
            case X:
                return y;
            default:
                throw new IllegalStateException("There is no slice");
        }
    }

    private int getVerticalAxis(int x, int y, int z){
        switch (slice){
            case X:
            case Y:
                return z;
            case Z:
                return y;
            default:
                throw new IllegalStateException("There is no slice");
        }
    }

    void setSliceDepth(int slice){
        this.slice_depth = slice;
        repaint();
    }

    int getSliceDepth(){
        return slice_depth;
    }

    PlatformEnvironment getEnvironment(){
        return subMap;
    }

    Set<String> getPopulators(){
        return viewPopulators.keySet();
    }

    void setPopulatorVisible(String pop, boolean b){
        if (viewPopulators.containsKey(pop)){
            viewPopulators.put(pop, b);
        }
        repaint();
    }

    boolean isPopulatorVisible(String pop){
        return viewPopulators.get(pop);
    }

    void setResolution(int res){
        if (res > 0){
            squareResolution = res;
            setSize(subMap.getSize() * squareResolution, subMap.getSize() * squareResolution);
            this.repaint();
        }
    }

    int getResolution(){
        return squareResolution;
    }

}

class SubIcon extends JPanel {

    private static final int _size = 80;

    private JLabel nameTitle, iconImage;

    private SubObject sub;

    SubIcon(SubObject sub){
        super.setOpaque(false);
        super.setName(sub.getName());
        this.sub = sub;

        nameTitle = new JLabel(sub.getName());
        nameTitle.setFont(new Font("Trebuchet MS", Font.BOLD, 17));
        nameTitle.setHorizontalAlignment(SwingConstants.CENTER);
        if (nameTitle.getPreferredSize().getWidth() > _size){
            nameTitle.setSize((int)nameTitle.getPreferredSize().getWidth(), (int)nameTitle.getPreferredSize().getHeight()+5);
        }
        else {
            nameTitle.setSize(_size, (int)nameTitle.getPreferredSize().getHeight()+5);
        }
        nameTitle.setLocation(0, 0);
        this.add(nameTitle);

        iconImage = new JLabel();
        iconImage.setSize(_size, _size);
        iconImage.setLocation((nameTitle.getWidth()-iconImage.getWidth())/2, nameTitle.getHeight());
        updateAngle(sub.getState().get("pitch"));
        this.add(iconImage);

        this.setSize(nameTitle.getWidth(), (nameTitle.getHeight()+iconImage.getHeight()));
        setLocation(0, 0);
    }

    void updateX(int resolution){
        PlatformState state = sub.getState();
        updateAngle(state.get("pitch"));
        setLocationOnMap((int)Math.round(state.<Double>get("x")*resolution), (int)Math.round(state.<Double>get("y")*resolution));
    }

    void updateZ(int resolution){
        PlatformState state = sub.getState();
        updateAngle(state.get("yaw"));
        setLocationOnMap((int)Math.round(state.<Double>get("y")*resolution), (int)Math.round(state.<Double>get("z")*resolution));
    }

    private void updateAngle(double angle){
        while (angle < 0){
            angle += 2*Math.PI;
        }
        angle = Math.toDegrees(angle) % 360;
        try {
            ImageIcon img = ImageFunctions.getImage("/markers/Rover Marker " + (int)(angle - angle %5) + ".png");
            img = resize(img, iconImage.getWidth(), iconImage.getHeight());
            iconImage.setIcon(img);
        }
        catch (Exception e){
            iconImage.setIcon(null);
        }
    }

    private void setLocationOnMap(int x, int y){
        super.setBounds((x - this.getWidth()/2), (y - this.getHeight()/2-nameTitle.getHeight()), this.getWidth(), this.getHeight());
    }

    private ImageIcon resize(Icon image, int width, int height) throws Exception {
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);
        Graphics2D g = bi.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        Composite comp = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1);
        g.setComposite(comp);
        g.drawImage(((ImageIcon) (image)).getImage(), 0, 0, width, height, null);
        g.dispose();
        return new ImageIcon(bi);
    }

}
