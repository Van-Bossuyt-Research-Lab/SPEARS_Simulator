/*
 * SPEARS: Simulated Physics and Environment for Autonomous Risk Studies
 * Copyright (C) 2017  Colorado School of Mines
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.spears.ui.implementation;

import com.spears.platforms.Platform;
import com.spears.platforms.PlatformState;
import com.spears.platforms.sub.SubObject;
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

    private JLabel locationLbl, directionLbl, velocityLbl, velocityLb2, velocityLb3, accelLb3, accelLb2, angularAccelLb3, angularVelLb3, directionLb3, angularVelLbl, accelLbl, angularAccelLbl, directionLb2, angularVelLb2, angularAccelLb2;
    private SubMotorDisplay[] motorDisplays;

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
        drivePnl.add(velocityTtl, "cell 0 1");

        velocityLbl = new JLabel();
        velocityLbl.setFont(FontFunctions.bold(velocityLbl.getFont()));
        drivePnl.add(velocityLbl, "cell 1 1");

        velocityLb2 = new JLabel();
        velocityLb2.setFont(FontFunctions.bold(velocityLb2.getFont()));
        drivePnl.add(velocityLb2, "cell 2 1");

        velocityLb3 = new JLabel();
        velocityLb3.setFont(FontFunctions.bold(velocityLb3.getFont()));
        drivePnl.add(velocityLb3, "cell 3 1");

        JLabel accelTtl = new JLabel("Acceleration:");
        accelTtl.setHorizontalAlignment(SwingConstants.RIGHT);
        drivePnl.add(accelTtl, "cell 0 2");

        accelLb2 = new JLabel();
        accelLb2.setFont(FontFunctions.bold(accelLb2.getFont()));
        drivePnl.add(accelLb2, "cell 2 2");

        accelLbl = new JLabel();
        accelLbl.setFont(FontFunctions.bold(accelLbl.getFont()));
        drivePnl.add(accelLbl, "cell 1 2");

        accelLb3 = new JLabel();
        accelLb3.setFont(FontFunctions.bold(accelLb3.getFont()));
        drivePnl.add(accelLb3, "cell 3 2");

        JLabel directionTtl = new JLabel("Pitch:");
        directionTtl.setHorizontalAlignment(SwingConstants.RIGHT);
        drivePnl.add(directionTtl, "cell 0 3");

        directionLbl = new JLabel();
        directionLbl.setFont(FontFunctions.bold(directionLbl.getFont()));
        drivePnl.add(directionLbl, "cell 1 3");

        JLabel angularVelTtl = new JLabel("Pitch Velocity:");
        angularVelTtl.setHorizontalAlignment(SwingConstants.RIGHT);
        drivePnl.add(angularVelTtl, "cell 2 3");

        angularVelLbl = new JLabel();
        angularVelLbl.setFont(FontFunctions.bold(angularVelLbl.getFont()));
        drivePnl.add(angularVelLbl, "cell 3 3");

        JLabel angularAccelTtl = new JLabel("Pitch Acceleration:");
        angularAccelTtl.setHorizontalAlignment(SwingConstants.RIGHT);
        drivePnl.add(angularAccelTtl, "cell 4 3");

        angularAccelLbl = new JLabel();
        angularAccelLbl.setFont(FontFunctions.bold(angularAccelLbl.getFont()));
        drivePnl.add(angularAccelLbl, "cell 5 3");

        JLabel directionTt2 = new JLabel("Yaw:");
        directionTt2.setHorizontalAlignment(SwingConstants.RIGHT);
        drivePnl.add(directionTt2, "cell 0 4");

        directionLb2 = new JLabel();
        directionLb2.setFont(FontFunctions.bold(directionLb2.getFont()));
        drivePnl.add(directionLb2, "cell 1 4");

        JLabel directionTt3 = new JLabel("Roll:");
        directionTt3.setHorizontalAlignment(SwingConstants.RIGHT);
        drivePnl.add(directionTt3, "cell 0 5");

        directionLb3 = new JLabel();
        directionLb3.setFont(FontFunctions.bold(directionLb3.getFont()));
        drivePnl.add(directionLb3, "cell 1 5");

        JLabel angularVelTt3 = new JLabel("Roll Velocity:");
        angularVelTt3.setHorizontalAlignment(SwingConstants.RIGHT);
        drivePnl.add(angularVelTt3, "cell 2 5");

        angularVelLb3 = new JLabel();
        angularVelLb3.setFont(FontFunctions.bold(angularVelLb3.getFont()));
        drivePnl.add(angularVelLb3, "cell 3 5");

        JLabel angularAccelTt3 = new JLabel("Roll Acceleration:");
        angularAccelTt3.setHorizontalAlignment(SwingConstants.RIGHT);
        drivePnl.add(angularAccelTt3, "cell 4 5");

        angularAccelLb3 = new JLabel();
        angularAccelLb3.setFont(FontFunctions.bold(angularAccelLb3.getFont()));
        drivePnl.add(angularAccelLb3, "cell 5 5");

        JLabel angularVelTt2 = new JLabel("Yaw Velocity:");
        angularVelTt2.setHorizontalAlignment(SwingConstants.RIGHT);
        drivePnl.add(angularVelTt2, "cell 2 4");

        angularVelLb2 = new JLabel();
        angularVelLb2.setFont(FontFunctions.bold(angularVelLb2.getFont()));
        drivePnl.add(angularVelLb2, "cell 3 4");

        JLabel angularAccelTt2 = new JLabel("Yaw Acceleration:");
        angularAccelTt2.setHorizontalAlignment(SwingConstants.RIGHT);
        drivePnl.add(angularAccelTt2, "cell 4 4");

        angularAccelLb2 = new JLabel();
        angularAccelLb2.setFont(FontFunctions.bold(angularAccelLb2.getFont()));
        drivePnl.add(angularAccelLb2, "cell 5 4");

        JPanel motorPnl = new JPanel();
        motorPnl.setBackground(Color.WHITE);
        motorPnl.setLayout(new BorderLayout());
        tabs.addTab("Motors", motorPnl);

        ImageDisplay wheels = new ImageDisplay();
        wheels.setImage(ImageFunctions.getImage("/images/sub_base2.png"));
        motorPnl.add(wheels, BorderLayout.CENTER);

        JPanel vertProp = new JPanel();
        vertProp.setOpaque(false);
        vertProp.setLayout(new MigLayout("", "[fill,grow]", "[fill][fill,grow][fill]"));
        motorPnl.add(vertProp, BorderLayout.WEST);

        SubMotorDisplay fmotor = new SubMotorDisplay();
        vertProp.add(fmotor, "cell 0 0");

        SubMotorDisplay bmotor = new SubMotorDisplay();
        vertProp.add(bmotor, "cell 0 2");

        JPanel horizProp = new JPanel();
        horizProp.setOpaque(false);
        horizProp.setLayout(new MigLayout("", "[fill,grow]", "[fill][fill,grow][fill]"));
        motorPnl.add(horizProp, BorderLayout.EAST);

        SubMotorDisplay lmotor = new SubMotorDisplay();
        horizProp.add(lmotor, "cell 0 0");

        SubMotorDisplay rmotor = new SubMotorDisplay();
        horizProp.add(rmotor, "cell 0 2");

        motorDisplays = new SubMotorDisplay[] { fmotor, bmotor, lmotor, rmotor };

    }

    @Override
    protected void doSetPlatform(Platform platform) {
        this.sub = Optional.of((SubObject)platform);
        setTitle("Sub: " + platform.getName());
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
                updateDrive(state.get("x"), state.get("y"), state.get("z"), state.get("pitch"), state.get("yaw"), state.get("roll"), state.get("speed"), state.get("angular_speed"), state.get("acceleration"), state.get("angular_acceleration"));
                break;
            case 1:
                updateMotors(state.get("motor_state"), state.get("motor_power"), state.get("motor_voltage"), state.get("motor_current"), state.get("prop_speed"), state.get("motor_temp"));
                break;
            default:
                updateDrive(state.get("x"), state.get("y"), state.get("z"), state.get("pitch"), state.get("yaw"), state.get("roll"), state.get("speed"), state.get("angular_speed"), state.get("acceleration"), state.get("angular_acceleration"));              break;
        }
    }

    private void updateDrive(double x, double y, double z, double dir, double dir2, double dir3, Double [] vel, Double [] avel, Double [] accel, Double [] aaccel){
        locationLbl.setText(round(x) + ", " + round(y) + ", " + round(z));
        directionLbl.setText(round(dir*180./Math.PI) + "\u00B0");
        directionLb2.setText(round(dir2*180./Math.PI) + "\u00B0");
        directionLb3.setText(round(dir3*180./Math.PI) + "\u00B0");
        velocityLbl.setText(round(vel[0]) + " m/s");
        velocityLb2.setText(round(vel[1]) + " m/s");
        velocityLb3.setText(round(vel[2]) + " m/s");
        accelLbl.setText(round(accel[0]) + " m/s\u00B2");
        accelLb2.setText(round(accel[1]) + " m/s\u00B2");
        accelLb3.setText(round(accel[2]) + " m/s\u00B2");
        angularVelLbl.setText(round(avel[0]*180./Math.PI) + " \u00B0/s");
        angularAccelLbl.setText(round(aaccel[0]*180./Math.PI) + " \u00B0/s\u00B2");
        angularVelLb2.setText(round(avel[1]*180./Math.PI) + " \u00B0/s");
        angularVelLb3.setText(round(avel[2]*180./Math.PI) + " \u00B0/s");
        angularAccelLb2.setText(round(aaccel[1]*180./Math.PI) + " \u00B0/s\u00B2");
        angularAccelLb3.setText(round(aaccel[2]*180./Math.PI) + " \u00B0/s\u00B2");
    }

    private void updateMotors(Double[] states, Double[] powers, Double[] voltages, Double[] currents, Double[] speeds, Double[] temps){
        for (int i = 0; i < motorDisplays.length; i++){
            motorDisplays[i].update(states[i], powers[i], voltages[i], currents[i], speeds[i], temps[i]);
        }
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
