package org.firstinspires.ftc.teamcode.Modules;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Robot.Hardware;
import org.firstinspires.ftc.teamcode.Robot.IRobotModule;
import org.firstinspires.ftc.teamcode.Robot.IStateBasedModule;
import org.firstinspires.ftc.teamcode.Wrappers.CoolServo;

@Config
public class OuttakeArm implements IStateBasedModule, IRobotModule {

    public static boolean ENABLED = true;

    public final CoolServo leftServo, rightServo;
    public static boolean reversedLeftServo = false, reversedRightServo = true;

    public static double intakePosition = 0.2, outtakePosition = 0.8, passthroughPosition = 0.05;

    public static double profileMaxVelocity = 2.2, profileAcceleration = 1, profileDeceleration = 1;

    public enum State{
        INTAKE(intakePosition), GOING_INTAKE(intakePosition, INTAKE), OUTTAKE(outtakePosition), GOING_OUTTAKE(outtakePosition, OUTTAKE),
        PASSTHROUGH(passthroughPosition), GOING_PASSTHROUGH(passthroughPosition, PASSTHROUGH);

        public double position;
        public final State nextState;

        State(double position){
            this.position = position;
            this.nextState = this;
        }

        State(double position, State nextState){
            this.position = position;
            this.nextState = nextState;
        }
    }

    private void updateStateValues(){
        State.INTAKE.position = intakePosition;
        State.GOING_INTAKE.position = intakePosition;
        State.OUTTAKE.position = outtakePosition;
        State.GOING_OUTTAKE.position = outtakePosition;
        State.PASSTHROUGH.position = passthroughPosition;
        State.GOING_PASSTHROUGH.position = passthroughPosition;
    }

    private State state;

    private final ElapsedTime timer = new ElapsedTime();

    public State getState(){
        return state;
    }

    public void setState(State newState){
        if(newState == state) return;
        this.state = newState;
        timer.reset();
    }

    public OuttakeArm(Hardware hardware, State initialState){
        if(!ENABLED) leftServo = null;
        else leftServo = new CoolServo(hardware.seh2, reversedLeftServo, profileMaxVelocity, profileAcceleration, profileDeceleration, initialState.position);
        if(!ENABLED) rightServo = null;
        else rightServo = new CoolServo(hardware.seh4, reversedRightServo, profileMaxVelocity, profileAcceleration, profileDeceleration, initialState.position);
        timer.startTime();
        setState(initialState);
    }

    @Override
    public void update() {
        if(!ENABLED) return;

        updateStateValues();
        updateHardware();
        updateState();
    }

    @Override
    public void updateState() {
        if(leftServo.getTimeToMotionEnd() == 0)state = state.nextState;
    }

    @Override
    public void updateHardware() {
        leftServo.setPosition(state.position);
        rightServo.setPosition(state.position);

        leftServo.update();
        rightServo.update();
    }
}
