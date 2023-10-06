package to.be.renamed.module.projectconfig.connectiontest;

import java.awt.Color;
import java.io.Serializable;

public enum EcomTaskResult implements Serializable {
    SUCCESSFUL(new Color(54, 179, 126)), // GREEN
    FAILED(new Color(255, 86, 48)), // RED
    PROBLEMATIC(new Color(255, 171, 0)), // YELLOW
    DISABLED(new Color(193, 199, 208)), // LIGHT GRAY
    UNKNOWN(new Color(255, 171, 0)); // YELLOW

    private final Color statusColor;

    EcomTaskResult(Color color) {
        this.statusColor = color;
    }

    public Color getColor() {
        return this.statusColor;
    }
}