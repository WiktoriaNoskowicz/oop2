package org.example.powt.model;

public record Dot(int x, int y, String color, int radius) {
    public static String toMessage(int x, int y, String color, int radius) {
        return x + "," + y + "," + color + "," + radius; // Serializacja
    }

    public static Dot fromMessage(String msg) {
        String[] parts = msg.split(",");
        return new Dot(
                Integer.parseInt(parts[0]),
                Integer.parseInt(parts[1]),
                parts[2],
                Integer.parseInt(parts[3])
        );
    }

    public String toNetworkMessage() {
        return toMessage(x, y, color, radius); // Używane przy wysyłaniu do sieci
    }
}
