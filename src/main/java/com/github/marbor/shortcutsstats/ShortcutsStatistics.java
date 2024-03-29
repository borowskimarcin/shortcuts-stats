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
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

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
                this.statistics
                        .values()
                        .stream()
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
        total.incrementAndGet();
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
                .map(entry ->
                        new Shortcut(
                                entry.getKey(),
                                shortcutDescription.get(entry.getKey()),
                                entry.getValue()))
                .sorted(Comparator.comparing(Shortcut::getCount).reversed())
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
