package com.csm.rover.simulator.ui.implementation;

import com.csm.rover.simulator.objects.FreeThread;
import com.csm.rover.simulator.ui.visual.AbortListener;
import com.csm.rover.simulator.ui.visual.AcceleratedView;
import com.csm.rover.simulator.wrapper.Globals;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.Optional;

class AcceleratedViewImpl extends JDialog implements AcceleratedView {

    private Globals GLOBAL = Globals.getInstance();

    private JProgressBar progressBar;
    private JLabel progressLbl, timeLbl;

    private Optional<AbortListener> abortAction = Optional.empty();
    private long time_started;

    AcceleratedViewImpl(){
        initialize();
    }

    private void initialize() {
        setTitle("SPEARS Simulator - Accelerated");
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                abort();
            }
        });
        setSize(500, 150);
        setResizable(false);
        setAlwaysOnTop(true);

        JPanel contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setLayout(new MigLayout("", "[fill][fill,grow][fill]", "[fill][fill,grow][fill][fill]"));
        setContentPane(contentPane);

        JLabel title = new JLabel("The user interface has been hidden while the simulation is accelerated");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        contentPane.add(title, "cell 0 0 3 1");

        progressBar = new JProgressBar();
        progressBar.setMaximumSize(new Dimension(9999, 25));
        contentPane.add(progressBar, "cell 0 1 3 1");

        progressLbl = new JLabel("Progress: 0%");
        contentPane.add(progressLbl, "cell 0 2");

        timeLbl = new JLabel("Time Remaining: Inf");
        contentPane.add(timeLbl, "cell 0 3");

        JButton abortBtn = new JButton("Abort");
        abortBtn.addActionListener((a) -> abort());
        contentPane.add(abortBtn, "cell 2 3");

        setSize(title.getPreferredSize().width+50, 160);
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation((screenSize.width-getWidth())/2, (screenSize.height-getHeight())/2);
    }

    @Override
    public void show(){
        super.show();
        time_started = System.currentTimeMillis();
        update();
        new FreeThread(500, this::update, FreeThread.FOREVER, "accelUpdate");
    }

    private void update(){
        int sec = (int)(GLOBAL.timeMillis()/1000);
        progressBar.setValue(sec);
        int percent = sec*100 / progressBar.getMaximum();
        progressLbl.setText(String.format("Progress: %d%%", percent));
        long elapsed = System.currentTimeMillis() - time_started;
        int remaining = ((int)(elapsed * (progressBar.getMaximum() / (double)sec - 1.)) /60000+1);
        timeLbl.setText(String.format("Time Remaining: %s%d min", remaining == 1 ? "<" : "", remaining));
    }

    @Override
    public void setDuration(int min) {
        progressBar.setMaximum(min*60);
    }

    @Override
    public void setAbortAction(AbortListener listen) {
        this.abortAction = Optional.of(listen);
    }

    private void abort(){
        if (abortAction.isPresent()){
            abortAction.get().abortSimulation();
        }
    }
}
