package com.csm.rover.simulator.ui.implementation;

import com.csm.rover.simulator.platforms.Platform;
import com.csm.rover.simulator.platforms.PlatformState;
import com.csm.rover.simulator.platforms.rover.RoverObject;
import com.csm.rover.simulator.platforms.rover.RoverState;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

@FrameMarker(name = "Rover Monitor", platform = "Rover")
class RoverPlatformMonitor extends PlatformDisplay {

    private JPanel contentPane;
    private JTabbedPane tabs;

    private JLabel locationLbl, directionLbl, velocityLbl, angularVelLbl, accelLbl, angularAccelLbl;

    private RoverMotorDisplay[] motorDisplays;

    private RoverObject rover;

    RoverPlatformMonitor(){
        super("Rover");
        initialize();
    }

    private void initialize(){
        setTitle("");
        setSize(100, 100);

        contentPane = new JPanel();
        contentPane.setBackground(Color.WHITE);
        contentPane.setLayout(new BorderLayout());
        setContentPane(contentPane);

        tabs = new JTabbedPane();
        tabs.setOpaque(false);
        contentPane.add(tabs, BorderLayout.CENTER);

        JPanel drivePnl = new JPanel();
        drivePnl.setBackground(Color.WHITE);
        drivePnl.setLayout(new MigLayout("", "[fill,grow][fill][fill,grow][fill][fill,grow][fill]", "fill"));
        tabs.addTab("Drive", drivePnl);

        JLabel locationTtl = new JLabel("Location:");
        drivePnl.add(locationTtl, "cell 0 0");

        locationLbl = new JLabel();
        locationLbl.setFont(FontFunctions.bold(locationLbl.getFont()));
        drivePnl.add(locationLbl, "cell 1 0");

        JLabel velocityTtl = new JLabel("Velocity:");
        drivePnl.add(velocityTtl, "cell 2 0");

        velocityLbl = new JLabel();
        velocityLbl.setFont(FontFunctions.bold(velocityLbl.getFont()));
        drivePnl.add(velocityLbl, "cell 3 0");

        JLabel accelTtl = new JLabel("Acceleration:");
        drivePnl.add(accelTtl, "cell 4 0");

        accelLbl = new JLabel();
        accelLbl.setFont(FontFunctions.bold(accelLbl.getFont()));
        drivePnl.add(accelLbl, "cell 5 0");

        JLabel directionTtl = new JLabel("Direction:");
        drivePnl.add(directionTtl, "cell 0 1");

        directionLbl = new JLabel();
        directionLbl.setFont(FontFunctions.bold(directionLbl.getFont()));
        drivePnl.add(directionLbl, "cell 1 1");

        JLabel angularVelTtl = new JLabel("Angular Velocity:");
        drivePnl.add(angularVelTtl, "cell 2 1");

        angularVelLbl = new JLabel();
        angularVelLbl.setFont(FontFunctions.bold(angularVelLbl.getFont()));
        drivePnl.add(angularVelLbl, "cell 3 1");

        JLabel angularAccelTtl = new JLabel("Angular Acceleration:");
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
        leftWheels.setLayout(new MigLayout("", "[fill,grow]", "[fill][fill]"));
        motorPnl.add(leftWheels, BorderLayout.WEST);

        RoverMotorDisplay flmotor = new RoverMotorDisplay();
        leftWheels.add(flmotor, "cell 0 0");

        RoverMotorDisplay blmotor = new RoverMotorDisplay();
        leftWheels.add(blmotor, "cell 0 1");

        JPanel rightWheels = new JPanel();
        rightWheels.setLayout(new MigLayout("", "[fill,grow]", "[fill][fill]"));
        motorPnl.add(rightWheels, BorderLayout.EAST);

        RoverMotorDisplay frmotor = new RoverMotorDisplay();
        rightWheels.add(frmotor, "cell 0 0");

        RoverMotorDisplay brmotor = new RoverMotorDisplay();
        rightWheels.add(brmotor);

        motorDisplays = new RoverMotorDisplay[] { flmotor, frmotor, blmotor, brmotor };

        JPanel batteryPnl = new JPanel();
        batteryPnl.setBackground(Color.WHITE);
        tabs.addTab("Battery", batteryPnl);

        
    }

    @Override
    protected void doSetPlatform(Platform platform) {
        this.rover = (RoverObject)platform;
        setTitle("Rover: " + rover.getName());
        update();
    }

    @Override
    protected void update(){
        PlatformState state = rover.getState();
        switch (tabs.getSelectedIndex()){
            case 0:
                updateDrive(state.get("x"), state.get("y"), state.get("direction"), state.get("speed"), state.get("acceleration"), state.get("angular_velocity"), state.get("angular_acceleration"));
                break;
            case 1:
                updateMotors(state.get("motor_state"), state.get("motor_power"), state.get("motor_voltage"), state.get("motor_current"), state.get("wheel_speed"), state.get("motor_temp"));
                break;
            case 2:
                updateBattery(state.get("battery_voltage"), state.get("battery_charge"), state.get("battery_current"), state.get("battery_temp"));
                break;
            default:
                updateDrive(state.get("x"), state.get("y"), state.get("direction"), state.get("speed"), state.get("acceleration"), state.get("angular_velocity"), state.get("angular_acceleration"));
                updateMotors(state.get("motor_state"), state.get("motor_power"), state.get("motor_voltage"), state.get("motor_current"), state.get("wheel_speed"), state.get("motor_temp"));
                updateBattery(state.get("battery_voltage"), state.get("battery_charge"), state.get("battery_current"), state.get("battery_temp"));
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

    private void updateMotors(double[] states, double[] powers, double[] voltages, double[] currents, double[] speeds, double[] temps){
        for (int i = 0; i < motorDisplays.length; i++){
            motorDisplays[i].update(states[i], powers[i], voltages[i], currents[i], speeds[i], temps[i]);
        }
    }

    private void updateBattery(double voltage, double charge, double current, double temp){
        //TODO
    }

    private String round(double in){
        int a = (int)in;
        double r = in - a;
        int b = (int)(r*10);
        r = (r*10) - b;
        int c = (int)(r*10);
        r = (r*10) - c;
        int d = (int)(r*10);
        return String.format("%d.%d%d%d", a, b, c, d);
    }

}

class RoverMotorDisplay extends JPanel {

    private LEDIndicator onLed;
    private JLabel stateLbl, currentLbl, powerLbl, voltageLbl, tempLbl, speedLbl;

    RoverMotorDisplay(){
        setOpaque(false);
        setLayout(new MigLayout("", "[fill,grow][fill,grow]", "fill"));

        onLed = new LEDIndicator();
        onLed.setLEDColor(LEDIndicator.Colors.RED);
        add(onLed, "cell 0 0");

        stateLbl = new JLabel();
        stateLbl.setFont(FontFunctions.bold(stateLbl.getFont()));
        add(stateLbl, "cell 1 0");

        JLabel powerTtl = new JLabel("Power:");
        add(powerTtl, "cell 0 1");

        powerLbl = new JLabel();
        powerLbl.setFont(powerLbl.getFont());
        add(powerLbl, "cell 1 1");

        JLabel voltageTtl = new JLabel("Voltage:");
        add(voltageTtl, "cell 0 2");

        voltageLbl = new JLabel();
        voltageLbl.setFont(FontFunctions.bold(voltageLbl.getFont()));
        add(voltageLbl, "cell 1 2");

        JLabel currentTtl = new JLabel("Current:");
        add(currentTtl, "cell 0 3");

        currentLbl = new JLabel();
        currentLbl.setFont(FontFunctions.bold(currentLbl.getFont()));
        add(currentLbl, "cell 1 3");

        JLabel speedTtl = new JLabel("Speed:");
        add(speedTtl, "cell 0 4");

        speedLbl = new JLabel();
        speedLbl.setFont(stateLbl.getFont());
        add(speedLbl, "cell 1 4");

        JLabel tempTtl = new JLabel("Temperature:");
        add(tempTtl, "cell 0 5");

        tempLbl = new JLabel();
        tempLbl.setFont(tempLbl.getFont());
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
        currentLbl.setText(round(current*1000) + "mA");
        speedLbl.setText(round(speed*30/Math.PI) + " rpm");
        tempLbl.setText(round(temp) + "\u00B0C");
    }

    private String round(double in){
        int a = (int)in;
        double r = in - a;
        int b = (int)(r*10);
        r = (r*10) - b;
        int c = (int)(r*10);
        r = (r*10) - c;
        int d = (int)(r*10);
        return String.format("%d.%d%d%d", a, b, c, d);
    }

}
