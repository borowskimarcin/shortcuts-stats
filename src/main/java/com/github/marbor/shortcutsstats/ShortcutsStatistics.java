package com.github.marbor.shortcutsstats;

import com.github.marbor.shortcutsstats.model.Shortcut;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.RoamingType;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.MapAnnotation;
import com.intellij.util.xmlb.annotations.Property;
import com.intellij.util.xmlb.annotations.Transient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import static java.util.Comparator.comparingLong;

@State(
        name = "ShortcutsStatistic",
        storages = {
                @Storage(
                        value = "ShortcutsStatistic.xml",
                        roamingType = RoamingType.DISABLED
                )
        }
)
public class ShortcutsStatistics implements PersistentStateComponent<ShortcutsStatistics> {
    private final List<Observer> observers = new ArrayList<>();
    private final Set<String> FILTERED_SHORTCUTS = Set.of(
        "↑", "↓", "→", "←", "⎋", "⌫", "⏎"
    );
    @MapAnnotation(surroundKeyWithTag = false, surroundValueWithTag = false, surroundWithTag = false, entryTagName = "Statistic", keyAttributeName = "Action")
    private final Map<String, Long> statistics = new ConcurrentHashMap<>();

    @MapAnnotation(surroundKeyWithTag = false, surroundValueWithTag = false, surroundWithTag = false, entryTagName = "ShortcutDescription", keyAttributeName = "Description")
    private final Map<String, String> shortcutDescription = new ConcurrentHashMap<>();

    @Property(surroundWithTag = false)
    private final AtomicLong total = new AtomicLong(0);

    @Nullable
    @Override
    public ShortcutsStatistics getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ShortcutsStatistics stats) {
        XmlSerializerUtil.copyBean(stats, this);

        // This is for backward compatibility
        this.total.set(
                this.statistics.entrySet()
                        .stream()
                        .filter(s -> !FILTERED_SHORTCUTS.contains(s.getKey()))
                        .map(Map.Entry::getValue)
                        .mapToLong(l -> l)
                        .sum()
        );
    }

    @Transient
    public void addShortcutUsage(String key, String desc) {
        statistics.compute(
                key,
                (shortcut, counter) -> Optional.ofNullable(counter).map(c -> c + 1).orElse(1L)
        );
        if (!FILTERED_SHORTCUTS.contains(key)) {
            total.incrementAndGet();
        }
        Optional.ofNullable(desc).ifPresent((d -> shortcutDescription.put(key, d)));
        observers.forEach(Observer::onChange);
    }

    @Transient
    public void resetStatistic() {
        statistics.clear();
        total.set(0);
        observers.forEach(Observer::onChange);
    }

    @Transient
    public int getShortcutsNumber() {
        return statistics.size();
    }

    @Transient
    public List<Shortcut> getShortcuts() {
        return statistics.entrySet()
                .stream()
                .filter(e -> !FILTERED_SHORTCUTS.contains(e.getKey()))
                .map(entry ->
                        new Shortcut(
                                entry.getKey(),
                                shortcutDescription.get(entry.getKey()),
                                entry.getValue()))
                .sorted(comparingLong(Shortcut::count).reversed())
                .collect(Collectors.toList());
    }

    @Transient
    public long getTotal() {
        return total.get();
    }

    @Transient
    public void register(Observer listener) {
        observers.add(listener);
    }

}
