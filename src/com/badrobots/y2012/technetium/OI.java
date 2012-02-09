
package com.badrobots.y2012.technetium;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Victor;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.DriverStationLCD;

/*
 * @author 1014 Programming Team
 */
public class OI
{
    public static Joystick leftJoystick, rightJoystick, shooterJoystick;
    public static DriverStation ds;
    public static DriverStationLCD screen;
    private static double scalingFactor = 0;

    public static Joystick controller;
    /*
     * initializes all input methods (eg. joysticks)
     */
    public static void init()
    {
        try
        {
           leftJoystick = new Joystick(RobotMap.leftJoystick);
           rightJoystick = new Joystick(RobotMap.rightJoystick);
           controller = new Joystick(RobotMap.controller);
           ds = DriverStation.getInstance();//Drivers Station
           screen = DriverStationLCD.getInstance();//Output on DS
           controller = new Joystick(3); //XBOX Controller
        }

        catch (Exception e) {System.out.println(e);}
        
    }
    
    /*
     * Printles a string to the driverstation LCD
     * Status: Untested 
     * //TODO: Test!
     */
    public static void printToDS(String out)
    {
        screen.println(DriverStationLCD.Line.kMain6,1, out);
        screen.updateLCD();
    }

    public static double getLeftX()
    {
        return deadzone(leftJoystick.getX());
    }

    public static double getLeftY()
    {
        return deadzone(leftJoystick.getY());
    }

    public static double getRightX()
    {
        return deadzone(rightJoystick.getX());
    }

    public static double getRightY()
    {
        return deadzone(rightJoystick.getY());
    }

    public static double getShooterX()
    {
        return deadzone(shooterJoystick.getX());
    }

    public static double getShooterY()
    {
        return deadzone(shooterJoystick.getY());
    }

    public static double getXboxLeftX()
    {
        detectAxis();
        return deadzone(-controller.getRawAxis(1));
    }

    public static double getXboxLeftY()
    {
        return deadzone(controller.getRawAxis(2));
    }

    public static double getXboxRightX()
    {
        return deadzone(-controller.getRawAxis(4));
    }

    public static double getXboxRightY()
    {
        return deadzone(controller.getRawAxis(5));
    }

    public static boolean xboxControl()
    {
        return ds.getDigitalIn(1);
    }

    public static boolean rightStrafe()
    {
        return ds.getDigitalIn(2);
    }

    public static boolean absoluteGyro()
    {
        return ds.getDigitalIn(3);
    }

    public static boolean getUpButton()
    {
        return controller.getRawButton(4);//Y
    }

    public static boolean getDownButton()
    {
        return controller.getRawButton(1);//X
    }
    
    /*
     * @return the currently used controller left x value
     * status: all tested 1/30/12
     */
    public static double getUsedLeftX()
    {
        if (xboxControl())
            return deadzone(controller.getRawAxis(1));
        
        return deadzone(leftJoystick.getX());
        
    }
    
    /*
     * @return the currently used controller left y value
     */
    public static double getUsedLeftY()
    {
        if (xboxControl())
            return deadzone(controller.getRawAxis(2));
        
        return deadzone(leftJoystick.getY());
    }
    
    /*
     * @return the currently used controller right x value
     */
    public static double getUsedRightX()
    {
        if (xboxControl())
            return deadzone(controller.getRawAxis(4));
        
        return deadzone(rightJoystick.getX());
    }
    
    /*
     * @return the currently used controller right y value
     */
    public static double getUsedRightY()
    {
        if (xboxControl())
            return deadzone(controller.getRawAxis(5));
        
        return deadzone(rightJoystick.getY());
    }
    
    /**
     * @return whether the secondary trigger is depressed
     */
    public static boolean getSecondaryTrigger()
    {
        if (xboxControl())
            return controller.getBumper();
        
        return leftJoystick.getTrigger();
    }
    
    /**
     * @return whether the shoot trigger is depressed
     */
    public static boolean getShooterTrigger()
    {
        if (xboxControl())
            return controller.getTrigger();
        
        return rightJoystick.getTrigger();
    }

    /*
     * Creates a deadzone for joysticks
     * Status:Untested, must test scaling 
     * 
     */
    private static double deadzone(double d)
    {
        double jsSensitivity = getJoystickSensitivty();
        double xboxSensitivity = getXboxSensitivity();
        
        if (jsSensitivity > .9 || jsSensitivity < .1)
            jsSensitivity = .1;
        
        if (xboxSensitivity > .9 || xboxSensitivity < .1)
            xboxSensitivity = .5;
        
        d *= getScalingFactor();    // scaling code -- just multiple value by double
        
        if (Math.abs(d) < jsSensitivity && !xboxControl())
            return 0;
        
        else if (Math.abs(d) < xboxSensitivity && xboxControl())
            return 0;
        
        return d / Math.abs(d) * ((Math.abs(d) - .10) / .90);
    }

    public static void detectAxis()
    {
        for(int i=0; i<=12; i++)
        {
            if(Math.abs(controller.getRawAxis(i)) > .1)
                System.out.println(i + " : " + controller.getRawAxis(i));
        }
    }
    
    /*
     * @return the deadzone for the Joysticks controller
     */
    public static double getJoystickSensitivty()
    {
        return ds.getAnalogIn(1);
    }
    
    /*
     * @return the deadzone for the Xbox controller
     */
    public static double getXboxSensitivity()
    {
        return ds.getAnalogIn(2);
    }
    
    public static double getAnalogIn(int channel)
    {
        return ds.getAnalogIn(channel);
    }
    
    /*
     * @return the value from the driverstation analog input 3. If it less than 0
     * or greater than 1.5, it returns 1
     */
    public static double getScalingFactor()
    {
        if (scalingFactor != 0)
            return scalingFactor;
        
        if (ds.getAnalogIn(3) > 0 && ds.getAnalogIn(3) < 1.5)
            return ds.getAnalogIn(3);
        
        return 1;
    }
    
    /**
     * 
     * @param d the double that will be the new scaling factor (0-1)
     */
    public static void setScalingFactor(double d)
    {
        scalingFactor = d;
    }
}

