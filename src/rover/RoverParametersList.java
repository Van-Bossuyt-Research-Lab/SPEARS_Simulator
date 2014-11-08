package rover;

public class RoverParametersList {

	public final double wheel_radius = 0.0476; //m
	public final double wheel_mass = 0.034; //kg
	public final double rover_width = 0.438; //m
	public final double rover_length = 0.229; //m
	public final double motor_arm = Math.sqrt(Math.pow(rover_width, 2) + Math.pow(rover_length, 2)) / 2.0; //m
	public final double rover_mass = 2.266; //kg
	public final double rover_inertia = 0.1025; //kg*m^2
	public final double wheel_inertia = 0.5 * wheel_mass * Math.pow(wheel_radius, 2); //kg*m^2
	
	public final double motor_energy_transform = 0.035;
	public final double motor_voltage_transform = 0.571;
	public final double motor_resistance = 3; //Ohm
	public final double motor_inductance = 0.11455; //H
	public final double friction_axle = 0.0002621;
	public final double friction_gr = 0.65;
	public final double friction_s = 0.35;
	public final double gamma = Math.atan(1/rover_width);
	
	public final double R_cp0 = 0.07; //Ohm
	public final double R_cp1 = 0.01; //Ohm
	public final double R_cp2 = 3;
	public final double capacitance_battery = 12000; //F
	public final double capacitance_cp = 0.2; //F
	public final double resistance_s = 0.01; //Ohm
	public final double resistance_parasite = 100000000; //Ohm
	public final double battery_max_charge = 140000; //C
	
	public final double battery_heat_transfer = 1; //J/s/*c
	public final double battery_thermal_cap = 50;
	
	public final double winding_heat_transfer = 1; //J/s/*c
	public final double winding_thermal_cap = 50; //J/*c
	public final double motor_surface_heat_transfer = 1; //J/s/*c
	public final double motor_thermal_cap = 50; //J/*c	
	
}
