package net.alexhyisen.dg.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Item {
    private final StringProperty label;
    private final StringProperty name;
    private final StringProperty cls;
    private final StringProperty id;
    private final StringProperty readonly;
    private final StringProperty required;

    public Item() {
        this(null, null, null, null, null, null);
    }

    public Item(String label, String name, String cls, String id, String readonly, String required) {
        this.label = new SimpleStringProperty(label);
        this.name = new SimpleStringProperty(name);
        this.cls = new SimpleStringProperty(cls);
        this.id = new SimpleStringProperty(id);
        this.readonly = new SimpleStringProperty(readonly);
        this.required = new SimpleStringProperty(required);
    }

    public String getLabel() {
        return label.get();
    }

    public String getName() {
        return name.get();
    }

    public String getCls() {
        return cls.get();
    }

    public String getId() {
        return id.get();
    }

    public String getReadonly() {
        return readonly.get();
    }

    public String getRequired() {
        return required.get();
    }

    public StringProperty labelProperty() {
        return label;
    }

    public StringProperty nameProperty() {
        return name;
    }

    public StringProperty clsProperty() {
        return cls;
    }

    public StringProperty idProperty() {
        return id;
    }

    public StringProperty readonlyProperty() {
        return readonly;
    }

    public StringProperty requiredProperty() {
        return required;
    }

    public void setLabel(String label) {
        this.label.set(label);
    }

    public void setName(String name) {
        this.name.set(name);
    }

    public void setCls(String cls) {
        this.cls.set(cls);
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public void setReadonly(String readonly) {
        this.readonly.set(readonly);
    }

    public void setRequired(String required) {
        this.required.set(required);
    }
}
