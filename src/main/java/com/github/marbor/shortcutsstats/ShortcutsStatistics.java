package com.github.marbor.shortcutsstats;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.RoamingType;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.util.xmlb.XmlSerializerUtil;
import com.intellij.util.xmlb.annotations.MapAnnotation;
import com.intellij.util.xmlb.annotations.Transient;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

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
    private final List<OnStatisticsChangeListener> listeners = new ArrayList<>();

    @MapAnnotation(surroundKeyWithTag = false, surroundValueWithTag = false, surroundWithTag = false, entryTagName = "Statistic", keyAttributeName = "Action")
    private final Map<String, Long> statistics = new ConcurrentHashMap<>();

    @Nullable
    @Override
    public ShortcutsStatistics getState() {
        return this;
    }

    @Override
    public void loadState(@NotNull ShortcutsStatistics stats) {
        XmlSerializerUtil.copyBean(stats, this);
    }

    @Transient
    public void addShortcutUsage(String key) {
        statistics.compute(
                key,
                (shortcut, counter) -> Optional.ofNullable(counter).map(c -> c + 1).orElse(1L)
        );
        listeners.forEach(OnStatisticsChangeListener::onChange);
    }

    @Transient
    public void resetStatistic() {
        statistics.clear();
    }

    @Transient
    public Map<String, Long> getStatistics() {
        return new HashMap<>(statistics);
    }

    @Transient
    public void register(OnStatisticsChangeListener listener) {
        listeners.add(listener);
    }

}
