package pando.org.pandoSabor.game.rey;

public enum Humor {
    TRANQUILO(0, 25),
    FASTIDIADO(26, 50),
    ENOJADO(51, 75),
    FURIOSO(76, 100);

    private final int min;
    private final int max;

    Humor(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public static Humor fromLvl(int nivel) {
        for (Humor estado : values()) {
            if (nivel >= estado.min && nivel <= estado.max) {
                return estado;
            }
        }
        return FURIOSO;
    }
}
