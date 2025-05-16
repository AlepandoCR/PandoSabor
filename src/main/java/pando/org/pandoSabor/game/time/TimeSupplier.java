package pando.org.pandoSabor.game.time;

import org.bukkit.ChatColor;

import java.util.function.Supplier;

public class TimeSupplier {

    private final TimeController timeController;

    public TimeSupplier(TimeController timeController) {
        this.timeController = timeController;
    }

    public Supplier<String> getFormattedTimeSupplier() {
        return () -> {
            long currentTick = timeController.getCurrentTime();

            double minutes;
            if (currentTick < 12000) {
                double dayRatio = currentTick / 12000.0;
                minutes = dayRatio * 240;
            } else {
                double nightRatio = (currentTick - 12000) / 12000.0;
                minutes = 240 + nightRatio * 120;
            }


            minutes *= 4;

            int hour = (int) (minutes / 60);
            int minute = (int) (minutes % 60);

            boolean isDay = currentTick < 12000;
            String emoji = isDay ? "🌞" : "🌙";
            ChatColor color = isDay ? ChatColor.GOLD : ChatColor.BLUE;

            String timeFormatted = String.format("%02d:%02d", hour, minute);
            return color + emoji + " " + timeFormatted;
        };
    }

}
