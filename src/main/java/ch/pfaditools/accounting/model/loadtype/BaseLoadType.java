package ch.pfaditools.accounting.model.loadtype;

public enum BaseLoadType implements HasLoadType {

    NONE(null);

    private final String name;

    BaseLoadType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
