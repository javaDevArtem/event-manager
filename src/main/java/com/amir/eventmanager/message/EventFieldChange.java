package com.amir.eventmanager.message;

public class EventFieldChange<T> {
    private T oldField;
    private T newField;

    public T getOldField() {
        return oldField;
    }

    public void setOldField(T oldField) {
        this.oldField = oldField;
    }

    public T getNewField() {
        return newField;
    }

    public void setNewField(T newField) {
        this.newField = newField;
    }
}
