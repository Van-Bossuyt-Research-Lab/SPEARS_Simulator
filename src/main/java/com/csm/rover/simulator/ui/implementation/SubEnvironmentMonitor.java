package com.csm.rover.simulator.ui.implementation;

import com.csm.rover.simulator.environments.EnvironmentIO;
import com.csm.rover.simulator.environments.EnvironmentRegistry;
import com.csm.rover.simulator.environments.PlatformEnvironment;
import com.csm.rover.simulator.environments.sub.AquaticEnvironment;
import com.csm.rover.simulator.objects.io.EnvrioFileFilter;
import com.csm.rover.simulator.objects.util.DecimalPoint;
import com.csm.rover.simulator.objects.util.DecimalPoint3D;
import com.csm.rover.simulator.objects.util.FreeThread;
import com.csm.rover.simulator.platforms.PlatformState;
import com.csm.rover.simulator.platforms.sub.SubObject;
import com.csm.rover.simulator.ui.visual.PopulatorDisplayFunction;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.List;

@FrameMarker(name = "Aquatic Map Monitor", platform = "Sub")
class SubEnvironmentMonitor extends EnvironmentDisplay {
    private static final Logger LOG = LogManager.getLogger(TerrainEnvironmentMonitor.class);

    private JPanel content;
    private AquaticSliceDisplay display;
    private List<SubIcon> subIcons;

    private DecimalPoint focus;

    protected SubEnvironmentMonitor() {
        super("Sub");
        focus = new DecimalPoint(0, 0);
        initialize();
    }

    private void initialize() {
        content = new JPanel();
        setBackground(Color.BLACK);
        setLayout(null);
        setContentPane(content);
        setVisible(false);
        setTitle("Aquatic Environment Monitor");
        setSize(500, 500);
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
                    EnvironmentIO.saveEnvironment(display.getEnvironment(), EnvironmentIO.appendSuffix(platform_type, finder.getSelectedFile()));
                }
            }, 1, "SaveDialog");
        });
        menu.add(mntmSaveMapFile);

        for (String pop : display.getPopulators()){
            JRadioButtonMenuItem item = new JRadioButtonMenuItem("Show " + pop);
            item.setSelected(display.isPopulatorVisible(pop));
            item.addActionListener((a) -> {
                display.setPopulatorVisible(pop, item.isSelected());
                menu.setVisible(false);
            });
            menu.add(item);
        }

        display.setComponentPopupMenu(menu);
    }

    private void setFocusPoint(DecimalPoint point){
        focus = point;
        redraw();
    }

    void redraw(){
        display.setLocation((int)Math.round(this.getWidth()/2.0-focus.getX()* display.getResolution()- display.getWidth()/2.0),
                (int)Math.round(this.getHeight()/2.0+focus.getY()* display.getResolution() - display.getHeight()/2.0));
    }

    private Optional<Point> last = Optional.empty();
    private void dragMap(Point p){
        if (p == null){
            last = Optional.empty();
        }
        else if (last.isPresent()){
            double dx = last.get().getX() - p.x;
            double dy = p.y - last.get().getY();
            double cx = dx / (double)display.getResolution();
            double cy = dy / (double)display.getResolution();
            setFocusPoint(new DecimalPoint(this.focus.getX()+cx, this.focus.getY()+cy));
        }
        else {
            last = Optional.of(p);
        }
    }

    private void zoomMap(int zoom){
        display.setResolution(display.getResolution()+zoom);
        redraw();
    }

    @Override
    protected void doSetEnvironment(PlatformEnvironment environment) {
        display = new AquaticSliceDisplay((AquaticEnvironment) environment, AquaticSliceDisplay.Slice.Z);
        display.setResolution(20);
        content.add(display);

        display.addMouseListener(new MouseAdapter(){
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
        display.addMouseMotionListener(new MouseMotionAdapter(){
            @Override
            public void mouseDragged(MouseEvent e) {
                dragMap(e.getPoint());
            }
        });
        display.addMouseWheelListener((e) -> zoomMap(5 * -e.getWheelRotation()));

        getInputMap().put(KeyStroke.getKeyStroke("LEFT"), "pan left");
        getActionMap().put("pan left", ActionBuilder.newAction(() -> setFocusPoint(focus.offset(-1, 0))));
        getInputMap().put(KeyStroke.getKeyStroke("RIGHT"), "pan right");
        getActionMap().put("pan right", ActionBuilder.newAction(() -> setFocusPoint(focus.offset(1, 0))));
        getInputMap().put(KeyStroke.getKeyStroke("UP"), "pan up");
        getActionMap().put("pan up", ActionBuilder.newAction(() -> setFocusPoint(focus.offset(0, 1))));
        getInputMap().put(KeyStroke.getKeyStroke("DOWN"), "pan down");
        getActionMap().put("pan down", ActionBuilder.newAction(() -> setFocusPoint(focus.offset(0, -1))));
        redraw();

        subIcons = new ArrayList<>();
        for (SubObject sub : ((AquaticEnvironment)environment).getPlatforms()){
            SubIcon icon = new SubIcon(sub);
            display.add(icon);
            subIcons.add(icon);
        }
    }

    @Override
    protected void update() {
        redraw();
        for (SubIcon sub : subIcons){
            sub.update(display.getResolution());
        }
    }

}

class AquaticMapDisplay extends JPanel {

    enum Slice { X, Y, Z }

    private final AquaticEnvironment subMap;

    AquaticMapDisplay(AquaticEnvironment environment, Slice slice){
        subMap = environment;
        this.setOpaque(false);
        this.setLayout(new BorderLayout());

        JPanel mapHolder = new JPanel();
        mapHolder.setBackground(Color.BLACK);
        mapHolder.setLayout(null);
        this.add(mapHolder, BorderLayout.CENTER);

        AquaticSlicePanel mapSlice = new AquaticSlicePanel(environment, slice);
        mapHolder.add(mapSlice);

        JSlider slider = new JSlider();
        slider.setMinimum(0);
        slider.setMaximum(environment.getSize()-1);
        slider.setSnapToTicks(true);
        slider.setMajorTickSpacing(10);
        slider.setMinorTickSpacing(1);
        slider.addChangeListener((e) -> {
            JSlider source = (JSlider)e.getSource();
            if (!source.getValueIsAdjusting()){
                mapSlice.setSliceDepth(source.getValue());
            }
        });
        this.add(slider, BorderLayout.SOUTH);
    }

}

class AquaticSlicePanel extends JPanel {
    private static final Logger LOG = LogManager.getLogger(AquaticSlicePanel.class);

    private final AquaticEnvironment subMap;

    private int squareResolution = 50;

    private Map<String, Boolean> viewPopulators;
    private Map<String, PopulatorDisplayFunction> popDisplays;

    private final AquaticMapDisplay.Slice slice;
    private int slice_depth = 0;

    AquaticSlicePanel(AquaticEnvironment environment, AquaticMapDisplay.Slice slice){
        this.subMap = environment;
        this.slice = slice;
        this.setLayout(null);
        viewPopulators = new HashMap<>();
        popDisplays = new HashMap<>();
        for (String pop : subMap.getPopulators()){
            viewPopulators.put(pop, true);
            popDisplays.put(pop, EnvironmentRegistry.getPopulatorDisplayFunction(subMap.getType(), pop));
        }
        this.setBounds(0, 0, 100, 100);
        this.setBackground(Color.BLACK);
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent arg0) {
                setSize(subMap.getSize() * squareResolution + 1, subMap.getSize() * squareResolution + 1);
            }
        });
    }

    //paint the map
    //kind long and ugly, I know, but necessary for performance
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        try {
            int horzstart = (-this.getLocation().x / squareResolution - 1);
            int horzend = horzstart + (this.getParent().getWidth() / squareResolution + 3);
            if (horzstart < 0){
                horzstart = 0;
            }
            if (horzend > subMap.getSize()){
                horzend = subMap.getSize();
            }
            int vertstart = (-this.getLocation().y / squareResolution - 1);
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
                    ystart = vertstart;
                    yend = vertend;
                    zstart = horzstart;
                    zend = horzend;
                    break;
                case Y:
                    xstart = vertstart;
                    xend = vertend;
                    ystart = slice_depth;
                    yend = slice_depth + 1;
                    zstart = horzstart;
                    zend = horzend;
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
                            DecimalPoint3D loc = new DecimalPoint3D(x + 0.5, y + 0.5, z + 0.5);
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
                                LOG.log(Level.WARN, "Failed to draw square [" + x + ", " + y + "]: " + e.getMessage());
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
                return x;
            case Y:
            case X:
                return z;
            default:
                throw new IllegalStateException("There is no slice");
        }
    }

    private int getVerticalAxis(int x, int y, int z){
        switch (slice){
            case X:
            case Z:
                return y;
            case Y:
                return x;
            default:
                throw new IllegalStateException("There is no slice");
        }
    }

    int getSliceDepth(){
        return slice_depth;
    }

    void setSliceDepth(int depth){
        if (depth < 0){
            depth = 0;
        }
        else if (depth > subMap.getSize()){
            depth = subMap.getSize();
        }
        slice_depth = depth;
        repaint();
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
        updateAngle(sub.getState().get("direction"));
        this.add(iconImage);

        this.setSize(nameTitle.getWidth(), (nameTitle.getHeight()+iconImage.getHeight()));
        setLocation(0, 0);
    }

    void update(int resolution){
        PlatformState state = sub.getState();
        updateAngle(state.get("direction"));
        setLocationOnMap((int)Math.round(state.<Double>get("x")*resolution), (int)Math.round(state.<Double>get("y")*resolution));
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