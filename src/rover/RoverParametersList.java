package rover;

public class RoverParametersList {

	private double wheel_radius = 0.0476; //m
	private double wheel_mass = 0.064; //kg
	private double rover_width = 0.438; //m
	private double rover_length = 0.229; //m
	private double motor_arm = Math.sqrt(Math.pow(rover_width, 2) + Math.pow(rover_length, 2)) / 2.0; //m
	private double rover_mass = 2.266; //kg
	private double rover_inertia = 0.1025; //kg*m^2
	private double wheel_inertia = 0.5 * wheel_mass * Math.pow(wheel_radius, 2); //kg*m^2
	
	private double motor_energy_transform = 0.035;
	private double motor_voltage_transform = 0.571;
	private double motor_resistance = 3; //Ohm
	private double motor_inductance = 0.11455; //H
	private double friction_axle = 0.0002621;
	private double friction_gr = .65;
	private double friction_s = 1.2;
	private double gamma = Math.atan(1/rover_width);
	
	private double R_cp0 = 0.07; //Ohm
	private double R_cp1 = 0.01; //Ohm
	private double R_cp2 = 3;
	private double capacitance_battery = 12000; //F
	private double capacitance_cp = 0.2; //F
	private double resistance_s = 0.01; //Ohm
	private double resistance_parasite = 100000000; //Ohm
	private double battery_max_charge = 140000; //C
	
	private double battery_heat_transfer = 10; //J/s/*c
	private double battery_thermal_cap = 170; //J/K
	
	private double winding_heat_transfer = 2; //J/s/*c
	private double winding_thermal_cap = 1.7; //J/*c
	private double motor_surface_heat_transfer = 0.9; //J/s/*c
	private double motor_thermal_cap = 0.8; //J/*c	
	
	public RoverParametersList() {}
	
	public RoverParametersList(double wheel_radius, double wheel_mass, double rover_width, double rover_length, double rover_mass,
			double rover_inertia, double wheel_inertia, double motor_energy_transform, double motor_voltage_transform, double motor_resistance,
			double motor_inductance, double friction_axle, double friction_gr, double friction_s, double R_cp0, double R_cp1, double R_cp2,
			double capacitance_battery, double capacitance_cp, double resistance_s, double resistance_parasite, double battery_max_charge, double battery_heat_transfer,
			double battery_thermal_cap, double winding_heat_transfer, double winding_thermal_cap, double motor_surface_heat_transfer, double motor_thermal_cap){
		
		this.wheel_radius = wheel_radius;
		this.wheel_mass = wheel_mass;
		this.rover_width = rover_width;
		this.rover_length = rover_length;
		this.rover_mass = rover_mass;
		this.rover_inertia = rover_inertia;
		this.wheel_inertia = wheel_inertia;
		this.motor_energy_transform = motor_energy_transform;
		this.motor_voltage_transform = motor_voltage_transform;
		this.motor_resistance = motor_resistance;
		this.motor_inductance = motor_inductance;
		this.friction_axle = friction_axle;
		this.friction_gr = friction_gr;
		this.friction_s = friction_s;
		this.R_cp0 = R_cp0;
		this.R_cp1 = R_cp1;
		this.R_cp2 = R_cp2;
		this.capacitance_battery = capacitance_battery;
		this.capacitance_cp = capacitance_cp;
		this.resistance_s = resistance_s;
		this.resistance_parasite = resistance_parasite;
		this.battery_max_charge = battery_max_charge;
		this.battery_heat_transfer = battery_heat_transfer;
		this.battery_thermal_cap = battery_thermal_cap;
		this.winding_heat_transfer = winding_heat_transfer;
		this.winding_thermal_cap = winding_thermal_cap;
		this.motor_surface_heat_transfer = motor_surface_heat_transfer;
		this.motor_thermal_cap = motor_thermal_cap;
	}
	
	public double getwheel_radius(){
		return wheel_radius;
	}

	public double getwheel_mass(){
		return wheel_mass;
	}

	public double getrover_width(){
		return rover_width;
	}

	public double getrover_length(){
		return rover_length;
	}

	public double getmotor_arm(){
		return motor_arm;
	}

	public double getrover_mass(){
		return rover_mass;
	}

	public double getrover_inertia(){
		return rover_inertia;
	}

	public double getwheel_inertia(){
		return wheel_inertia;
	}

	public double getmotor_energy_transform(){
		return motor_energy_transform;
	}

	public double getmotor_voltage_transform(){
		return motor_voltage_transform;
	}

	public double getmotor_resistance(){
		return motor_resistance;
	}

	public double getmotor_inductance(){
		return motor_inductance;
	}

	public double getfriction_axle(){
		return friction_axle;
	}

	public double getfriction_gr(){
		return friction_gr;
	}

	public double getfriction_s(){
		return friction_s;
	}

	public double getgamma(){
		return gamma;
	}

	public double getR_cp0(){
		return R_cp0;
	}

	public double getR_cp1(){
		return R_cp1;
	}

	public double getR_cp2(){
		return R_cp2;
	}

	public double getcapacitance_battery(){
		return capacitance_battery;
	}

	public double getcapacitance_cp(){
		return capacitance_cp;
	}

	public double getresistance_s(){
		return resistance_s;
	}

	public double getresistance_parasite(){
		return resistance_parasite;
	}

	public double getbattery_max_charge(){
		return battery_max_charge;
	}

	public double getbattery_heat_transfer(){
		return battery_heat_transfer;
	}

	public double getbattery_thermal_cap(){
		return battery_thermal_cap;
	}

	public double getwinding_heat_transfer(){
		return  winding_heat_transfer;
	}

	public double getwinding_thermal_cap(){
		return winding_thermal_cap;
	}

	public double getmotor_surface_heat_transfer(){
		return motor_surface_heat_transfer;
	}

	public double getmotor_thermal_cap(){
		return motor_thermal_cap;
	}
	
}
