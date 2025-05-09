package pando.org.pandoSabor.game.rey;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;

import java.util.List;
import java.util.Map;
import java.util.Random;

import static net.kyori.adventure.text.Component.text;

public class Dialog {

    private static final Map<Humor, List<Component>> dialogos = Map.of(
            Humor.TRANQUILO, List.of(
                    reyPrefix().append(text(" Aventurero, tengo un pequeño favor que podrías hacerme.", NamedTextColor.AQUA)),
                    reyPrefix().append(text(" Escoge bien, no entrego misiones a cualquiera.", NamedTextColor.GREEN)),
                    reyPrefix().append(text(" Te daré una tarea, nada complicado para alguien como tú.", NamedTextColor.AQUA)),
                    reyPrefix().append(text(" Una misión te espera. No me hagas arrepentirme.", NamedTextColor.AQUA))
            ),
            Humor.FASTIDIADO, List.of(
                    reyPrefix().append(text(" Si vas a estar aquí, al menos haz algo útil para mí.", NamedTextColor.YELLOW)),
                    reyPrefix().append(text(" Estoy ocupado. Lleva a cabo esta tarea y déjame en paz.", NamedTextColor.YELLOW)),
                    reyPrefix().append(text(" Otra petición más... bien, haz esto y no falles.", NamedTextColor.GOLD)),
                    reyPrefix().append(text(" Te daré una misión. No me hagas perder el tiempo.", NamedTextColor.GRAY))
            ),
            Humor.ENOJADO, List.of(
                    reyPrefix().append(text(" ¡Ya es hora de que alguien demuestre su valor!", NamedTextColor.RED)),
                    reyPrefix().append(text(" ¡Ve y cumple esta orden antes de que me arrepienta!", NamedTextColor.RED)),
                    reyPrefix().append(text(" Estoy harto de incompetentes, ¡haz esta misión bien!", NamedTextColor.DARK_RED)),
                    reyPrefix().append(text(" ¡No acepto excusas, completa esta misión!", NamedTextColor.DARK_RED))
            ),
            Humor.FURIOSO, List.of(
                    reyPrefix().append(text(" ¡TÚ! ¡Lleva esta misión a cabo o será tu cabeza!", NamedTextColor.DARK_RED).decorate(net.kyori.adventure.text.format.TextDecoration.BOLD)),
                    reyPrefix().append(text(" ¡Haz esto ahora o sufrirás las consecuencias!", NamedTextColor.DARK_RED).decorate(net.kyori.adventure.text.format.TextDecoration.BOLD)),
                    reyPrefix().append(text(" ¡Una última oportunidad para redimirte, haz esta tarea!", NamedTextColor.RED).decorate(net.kyori.adventure.text.format.TextDecoration.BOLD)),
                    reyPrefix().append(text(" ¡No me hagas perder la paciencia, completa esta misión!", NamedTextColor.DARK_RED).decorate(net.kyori.adventure.text.format.TextDecoration.BOLD))
            )
    );

    public static Component getDialog(Humor estado) {
        List<Component> opciones = dialogos.getOrDefault(estado, List.of(text("[Rey] ...", NamedTextColor.GRAY)));
        return opciones.get(new Random().nextInt(opciones.size()));
    }

    private static Component reyPrefix() {
        return text("[", NamedTextColor.GOLD)
                .append(text("Rey", NamedTextColor.RED))
                .append(text("] ", NamedTextColor.GOLD));
    }
}
