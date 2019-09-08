package me.fixeddev.base.api.permissions;

public enum Tristate {
    TRUE(true), FALSE(false), UNDEFINED(false);

    private boolean booleanValue;

    Tristate(boolean booleanValue) {
        this.booleanValue = booleanValue;
    }

    public boolean toBoolean(){
        return booleanValue;
    }

    public static Tristate fromBoolean(boolean booleanValue){
        return booleanValue ? TRUE : FALSE;
    }
}
