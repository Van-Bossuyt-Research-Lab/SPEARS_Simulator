package com.csm.rover.simulator.ui.implementation;

import com.csm.rover.simulator.platforms.Platform;
import com.csm.rover.simulator.platforms.PlatformState;
import com.csm.rover.simulator.platforms.sub.SubObject;
import net.miginfocom.swing.MigLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.SwingConstants;
import java.awt.BorderLayout;
import java.awt.Color;
import java.util.Optional;

@FrameMarker(name = "Sub Monitor", platform = "Sub")
class SubPlatformMonitor extends PlatformDisplay {

    private JPanel contentPane;
    private JTabbedPane tabs;

    private JLabel locationLbl, directionLbl, velocityLbl, angularVelLbl, accelLbl, angularAccelLbl, directionLb2, angularVelLb2, angularAccelLbl;
    private com.csm.rover.simulator.ui.implementation.SubMotorDisplay[] motorDisplays;

    private Optional<SubObject> sub;

    SubPlatformMonitor(){
        super("Sub");
        super.setInterval(500);
        sub = Optional.empty();
        initialize();
    }

    private void initialize(){
        setTitle("");
        setSize(500, 300);
        setMinimumSize(getSize());

        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        tabs = new JTabbedPane();
        tabs.setOpaque(false);
        tabs.addChangeListener((e) -> update());
        contentPane.add(tabs, BorderLayout.CENTER);

        JPanel drivePnl = new JPanel();
        drivePnl.setBackground(Color.WHITE);
        drivePnl.setLayout(new MigLayout("", "[fill,grow][fill,grow][fill,grow][fill,grow][fill,grow][fill,grow]", "fill"));
        tabs.addTab("Drive", drivePnl);

        JLabel locationTtl = new JLabel("Location:");
        locationTtl.setHorizontalAlignment(SwingConstants.RIGHT);
        drivePnl.add(locationTtl, "cell 0 0");

        locationLbl = new JLabel();
        locationLbl.setFont(FontFunctions.bold(locationLbl.getFont()));
        drivePnl.add(locationLbl, "cell 1 0");

        JLabel velocityTtl = new JLabel("Velocity:");
        velocityTtl.setHorizontalAlignment(SwingConstants.RIGHT);
        drivePnl.add(velocityTtl, "cell 2 0");

        velocityLbl = new JLabel();
        velocityLbl.setFont(FontFunctions.bold(velocityLbl.getFont()));
        drivePnl.add(velocityLbl, "cell 3 0");

        JLabel accelTtl = new JLabel("Acceleration:");
        accelTtl.setHorizontalAlignment(SwingConstants.RIGHT);
        drivePnl.add(accelTtl, "cell 4 0");

        accelLbl = new JLabel();
        accelLbl.setFont(FontFunctions.bold(accelLbl.getFont()));
        drivePnl.add(accelLbl, "cell 5 0");

        JLabel directionTtl = new JLabel("Pitch:");
        directionTtl.setHorizontalAlignment(SwingConstants.RIGHT);
        drivePnl.add(directionTtl, "cell 0 1");

        directionLbl = new JLabel();
        directionLbl.setFont(FontFunctions.bold(directionLbl.getFont()));
        drivePnl.add(directionLbl, "cell 1 1");

        JLabel angularVelTtl = new JLabel("Pitch Velocity:");
        angularVelTtl.setHorizontalAlignment(SwingConstants.RIGHT);
        drivePnl.add(angularVelTtl, "cell 2 1");

        angularVelLbl = new JLabel();
        angularVelLbl.setFont(FontFunctions.bold(angularVelLbl.getFont()));
        drivePnl.add(angularVelLbl, "cell 3 1");

        JLabel angularAccelTtl = new JLabel("Pitch Acceleration:");
        angularAccelTtl.setHorizontalAlignment(SwingConstants.RIGHT);
        drivePnl.add(angularAccelTtl, "cell 4 1");

        angularAccelLbl = new JLabel();
        angularAccelLbl.setFont(FontFunctions.bold(angularAccelLbl.getFont()));
        drivePnl.add(angularAccelLbl, "cell 5 1");

        JPanel motorPnl = new JPanel();
        motorPnl.setBackground(Color.WHITE);
        motorPnl.setLayout(new BorderLayout());
        tabs.addTab("Motors", motorPnl);

        ImageDisplay wheels = new ImageDisplay();
        wheels.setImage(ImageFunctions.getImage("/images/wheel_base.png"));
        motorPnl.add(wheels, BorderLayout.CENTER);

        JPanel leftWheels = new JPanel();
        leftWheels.setOpaque(false);
        leftWheels.setLayout(new MigLayout("", "[fill,grow]", "[fill][fill,grow][fill]"));
        motorPnl.add(leftWheels, BorderLayout.WEST);

        com.csm.rover.simulator.ui.implementation.SubMotorDisplay flmotor = new com.csm.rover.simulator.ui.implementation.SubMotorDisplay();
        leftWheels.add(flmotor, "cell 0 0");

        com.csm.rover.simulator.ui.implementation.SubMotorDisplay blmotor = new com.csm.rover.simulator.ui.implementation.SubMotorDisplay();
        leftWheels.add(blmotor, "cell 0 2");

        JPanel rightWheels = new JPanel();
        rightWheels.setOpaque(false);
        rightWheels.setLayout(new MigLayout("", "[fill,grow]", "[fill][fill,grow][fill]"));
        motorPnl.add(rightWheels, BorderLayout.EAST);

        com.csm.rover.simulator.ui.implementation.SubMotorDisplay frmotor = new com.csm.rover.simulator.ui.implementation.SubMotorDisplay();
        rightWheels.add(frmotor, "cell 0 0");

        com.csm.rover.simulator.ui.implementation.SubMotorDisplay brmotor = new com.csm.rover.simulator.ui.implementation.SubMotorDisplay();
        rightWheels.add(brmotor, "cell 0 2");

        motorDisplays = new com.csm.rover.simulator.ui.implementation.SubMotorDisplay[] { flmotor, frmotor, blmotor, brmotor };

    }

    @Override
    protected void doSetPlatform(Platform platform) {
        this.sub = Optional.of((SubObject)platform);
        setTitle("Rover: " + platform.getName());
        update();
    }

    @Override
    protected void update(){
        if (!sub.isPresent()){
            return;
        }
        PlatformState state = sub.get().getState();
        switch (tabs.getSelectedIndex()){
            case 0:
                updateDrive(state.get("x"), state.get("y"), state.get("pitch"), state.get("speed"), state.get("acceleration"), state.get("pitch_velocity"), state.get("pitch_acceleration"));
                break;
            case 1:
                updateMotors(state.get("motor_state"), state.get("motor_power"), state.get("motor_voltage"), state.get("motor_current"), state.get("prop_speed"), state.get("motor_temp"));
                break;
            default:
                updateDrive(state.get("x"), state.get("y"), state.get("pitch"), state.get("speed"), state.get("acceleration"), state.get("pitch_velocity"), state.get("angular_acceleration"));
                updateMotors(state.get("motor_state"), state.get("motor_power"), state.get("motor_voltage"), state.get("motor_current"), state.get("wheel_speed"), state.get("motor_temp"));
                break;
        }
    }

    private void updateDrive(double x, double y, double dir, double vel, double accel, double avel, double aaccel){
        locationLbl.setText(round(x) + ", " + round(y));
        directionLbl.setText(round(dir*180./Math.PI) + "\u00B0");
        velocityLbl.setText(round(vel) + " m/s");
        accelLbl.setText(round(accel) + " m/s\u00B2");
        angularVelLbl.setText(round(avel*180./Math.PI) + " \u00B0/s");
        angularAccelLbl.setText(round(aaccel*180./Math.PI) + " \u00B0/s\u00B2");
    }

    private void updateMotors(Double[] states, Double[] powers, Double[] voltages, Double[] currents, Double[] speeds, Double[] temps){
        for (int i = 0; i < motorDisplays.length; i++){
            motorDisplays[i].update(states[i], powers[i], voltages[i], currents[i], speeds[i], temps[i]);
        }
    }

    private void updateBattery(double voltage, double charge, double current, double temp){
        voltageLbl.setText(round(voltage) + " V");
        socLbl.setText(round(charge) + "%");
        chargeLbl.setText(round(charge/1000.) + " kC");
        currentLbl.setText(round(current*1000.) + " mA");
        tempLbl.setText(round(temp) + "\u00B0C");
    }

    private String round(double in){
        int a = (int)in;
        double r = Math.abs(in - a);
        int b = (int)(r*10);
        r = (r*10) - b;
        int c = (int)(r*10);
        r = (r*10) - c;
        int d = (int)(r*10);
        return String.format("%d.%d%d%d", a, b, c, d);
    }

}

class SubMotorDisplay extends JPanel {

    private LEDIndicator onLed;
    private JLabel stateLbl, currentLbl, powerLbl, voltageLbl, tempLbl, speedLbl;

    SubMotorDisplay(){
        setOpaque(false);
        setLayout(new MigLayout("", "[grow][fill,grow]", "fill"));

        onLed = new LEDIndicator();
        onLed.setLEDColor(LEDIndicator.Colors.RED);
        add(onLed, "cell 0 0");

        stateLbl = new JLabel();
        stateLbl.setFont(FontFunctions.bold(stateLbl.getFont()));
        stateLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        add(stateLbl, "cell 1 0");

        JLabel powerTtl = new JLabel("Power:");
        add(powerTtl, "cell 0 1");

        powerLbl = new JLabel();
        powerLbl.setFont(FontFunctions.bold(powerLbl.getFont()));
        powerLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        add(powerLbl, "cell 1 1");

        JLabel voltageTtl = new JLabel("Voltage:");
        add(voltageTtl, "cell 0 2");

        voltageLbl = new JLabel();
        voltageLbl.setFont(FontFunctions.bold(voltageLbl.getFont()));
        voltageLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        add(voltageLbl, "cell 1 2");

        JLabel currentTtl = new JLabel("Current:");
        add(currentTtl, "cell 0 3");

        currentLbl = new JLabel();
        currentLbl.setFont(FontFunctions.bold(currentLbl.getFont()));
        currentLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        add(currentLbl, "cell 1 3");

        JLabel speedTtl = new JLabel("Speed:");
        add(speedTtl, "cell 0 4");

        speedLbl = new JLabel();
        speedLbl.setFont(FontFunctions.bold(stateLbl.getFont()));
        speedLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        add(speedLbl, "cell 1 4");

        JLabel tempTtl = new JLabel("Temperature:");
        add(tempTtl, "cell 0 5");

        tempLbl = new JLabel();
        tempLbl.setFont(FontFunctions.bold(tempLbl.getFont()));
        tempLbl.setHorizontalAlignment(SwingConstants.RIGHT);
        add(tempLbl, "cell 1 5");
    }

    void update(double state, double power, double voltage, double current, double speed, double temp){
        onLed.setSelected(state != 0);
        if (state > 0){
            stateLbl.setText("FORWARD");
        }
        else if (state < 0){
            stateLbl.setText("REVERSE");
        }
        else {
            stateLbl.setText("RELEASED");
        }
        powerLbl.setText(round(power/255.*100) + "%");
        voltageLbl.setText(round(voltage) + " V");
        currentLbl.setText(round(current*1000) + " mA");
        speedLbl.setText(round(speed*30/Math.PI) + " rpm");
        tempLbl.setText(round(temp) + "\u00B0C");
    }

    private String round(double in){
        int a = (int)in;
        double r = Math.abs(in - a);
        int b = (int)(r*10);
        r = (r*10) - b;
        int c = (int)(r*10);
        r = (r*10) - c;
        int d = (int)(r*10);
        return String.format("%d.%d%d%d", a, b, c, d);
    }

}
