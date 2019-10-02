package org.tucantest.tucan_exercise_android.model;

public class ActionRecord {
    public long ID;
    public long EventTime;
    public long EventTimeNano;
    public long HistoricalEventTime;
    public long HistoricalEventTimeNano;
    public String EventType;  // Main or Historical
    public int ToolType;
    public int MotionEventType;
    public int ActionMasked;
    public String Action;
    public float X;
    public float Y;
    public float Z;
    public float Pressure;
    public float Orientation;
    public float Tilt;
    public int ButtonState;
    public float HistoricalX;
    public float HistoricalY;
    public float HistoricalZ;
    public float HistoricalPressure;
    public float HistoricalOrientation;
    public float HistoricalTilt;


    public ActionRecord(){}

    public long getID() {
        return ID;
    }

    public void setID(long ID) {
        this.ID = ID;
    }

    public long getEventTime() {
        return EventTime;
    }

    public void setEventTime(long eventTime) {
        EventTime = eventTime;
    }

    public long getEventTimeNano() {
        return EventTimeNano;
    }

    public void setEventTimeNano(long eventTimeNano) {
        EventTimeNano = eventTimeNano;
    }

    public long getHistoricalEventTime() {
        return HistoricalEventTime;
    }

    public void setHistoricalEventTime(long historicalEventTime) {
        HistoricalEventTime = historicalEventTime;
    }

    public long getHistoricalEventTimeNano() {
        return HistoricalEventTimeNano;
    }

    public void setHistoricalEventTimeNano(long historicalEventTimeNano) {
        HistoricalEventTimeNano = historicalEventTimeNano;
    }

    public String getEventType() {
        return EventType;
    }

    public void setEventType(String eventType) {
        EventType = eventType;
    }

    public int getToolType() {
        return ToolType;
    }

    public void setToolType(int toolType) {
        ToolType = toolType;
    }

    public int getMotionEventType() {
        return MotionEventType;
    }

    public void setMotionEventType(int motionEventType) {
        MotionEventType = motionEventType;
    }

    public int isActionMasked() {
        return ActionMasked;
    }

    public void setActionMasked(int actionMasked) {
        ActionMasked = actionMasked;
    }

    public String getAction() {
        return Action;
    }

    public void setAction(String action) {
        Action = action;
    }

    public float getX() {
        return X;
    }

    public void setX(float x) {
        X = x;
    }

    public float getY() {
        return Y;
    }

    public void setY(float y) {
        Y = y;
    }

    public float getZ() {
        return Z;
    }

    public void setZ(float z) {
        Z = z;
    }

    public float getPressure() {
        return Pressure;
    }

    public void setPressure(float pressure) {
        Pressure = pressure;
    }

    public float getOrientation() {
        return Orientation;
    }

    public void setOrientation(float orientation) {
        Orientation = orientation;
    }

    public float getTilt() {
        return Tilt;
    }

    public void setTilt(float tilt) {
        Tilt = tilt;
    }

    public int getButtonState() {
        return ButtonState;
    }

    public void setButtonState(int buttonState) {
        ButtonState = buttonState;
    }

    public float getHistoricalX() {
        return HistoricalX;
    }

    public void setHistoricalX(float historicalX) {
        HistoricalX = historicalX;
    }

    public float getHistoricalY() {
        return HistoricalY;
    }

    public void setHistoricalY(float historicalY) {
        HistoricalY = historicalY;
    }

    public float getHistoricalZ() {
        return HistoricalZ;
    }

    public void setHistoricalZ(float historicalZ) {
        HistoricalZ = historicalZ;
    }

    public float getHistoricalPressure() {
        return HistoricalPressure;
    }

    public void setHistoricalPressure(float historicalPressure) {
        HistoricalPressure = historicalPressure;
    }

    public float getHistoricalOrientation() {
        return HistoricalOrientation;
    }

    public void setHistoricalOrientation(float historicalOrientation) {
        HistoricalOrientation = historicalOrientation;
    }

    public float getHistoricalTilt() {
        return HistoricalTilt;
    }

    public void setHistoricalTilt(float historicalTilt) {
        HistoricalTilt = historicalTilt;
    }
}
