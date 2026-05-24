package dev.kwlew.beaconsplus.kernel;

import dev.kwlew.beaconsplus.managers.exceptions.CircularDependencyException;
import dev.kwlew.beaconsplus.managers.exceptions.ComponentCreationException;
import dev.kwlew.beaconsplus.managers.exceptions.ConstructorSelectionException;
import dev.kwlew.beaconsplus.managers.exceptions.NonConstructableTypeException;
import dev.kwlew.beaconsplus.managers.exceptions.UnresolvedDependencyException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Objects;

public class Registry {

    private final Map<Class<?>, Object> instances = new HashMap<>();
    private final Map<Class<?>, Class<?>> bindings = new HashMap<>();
    private final ThreadLocal<Deque<Class<?>>> resolutionStack = ThreadLocal.withInitial(ArrayDeque::new);

    public <T> void register(Class<T> type, T instance) {
        Objects.requireNonNull(type, "type");
        Objects.requireNonNull(instance, "instance");
        instances.put(type, instance);
    }

    public <T> T resolve(Class<T> type) {
        Objects.requireNonNull(type, "type");
        Object existing = instances.get(type);
        if (existing != null) {
            return type.cast(existing);
        }

        Class<?> binding = bindings.get(type);
        if (binding != null) {
            Object resolved = resolve(binding);
            instances.put(type, resolved);
            return type.cast(resolved);
        }

        if (isNotConstructable(type)) {
            throw new NonConstructableTypeException(type);
        }

        Deque<Class<?>> stack = resolutionStack.get();
        if (stack.contains(type)) {
            throw new CircularDependencyException(formatCycle(stack, type));
        }

        try {
            stack.addLast(type);
            Constructor<?> constructor = selectConstructor(type);
            Object[] params = Arrays.stream(constructor.getParameterTypes())
                    .map(this::resolveDependency)
                    .toArray();

            constructor.setAccessible(true);

            T instance = type.cast(constructor.newInstance(params));
            instances.put(type, instance);
            return instance;
        } catch (ReflectiveOperationException e) {
            throw new ComponentCreationException(type, e);
        } finally {
            if (!stack.isEmpty() && stack.peekLast() == type) {
                stack.removeLast();
            }

            if (stack.isEmpty()) {
                resolutionStack.remove();
            }
        }
    }

    private Constructor<?> selectConstructor(Class<?> type) {
        Constructor<?>[] constructors = type.getDeclaredConstructors();
        if (constructors.length == 0) {
            throw new ConstructorSelectionException(type, "No constructor found.");
        }

        Constructor<?> injectConstructor = null;
        for (Constructor<?> constructor : constructors) {
            if (!constructor.isAnnotationPresent(Inject.class)) {
                continue;
            }
            if (injectConstructor != null) {
                throw new ConstructorSelectionException(type, "Multiple @Inject constructors found.");
            }
            injectConstructor = constructor;
        }

        if (injectConstructor != null) {
            return injectConstructor;
        }

        if (constructors.length == 1) {
            return constructors[0];
        }

        throw new ConstructorSelectionException(type,
                "Multiple constructors found without @Inject. Mark exactly one constructor with @Inject.");
    }

    public Collection<Object> getAll() {
        return Collections.unmodifiableSet(new LinkedHashSet<>(instances.values()));
    }

    private Object resolveDependency(Class<?> dependencyType) {
        Object existing = instances.get(dependencyType);
        if (existing != null) {
            return existing;
        }

        if (bindings.containsKey(dependencyType)) {
            return resolve(dependencyType);
        }

        if (isNotConstructable(dependencyType)) {
            throw new UnresolvedDependencyException(dependencyType);
        }

        return resolve(dependencyType);
    }

    private boolean isNotConstructable(Class<?> type) {
        return type.isInterface() || Modifier.isAbstract(type.getModifiers());
    }

    public <T> void bind(Class<T> abstraction, Class<? extends T> implementation) {
        Objects.requireNonNull(abstraction, "abstraction");
        Objects.requireNonNull(implementation, "implementation");
        if (!abstraction.isAssignableFrom(implementation)) {
            throw new IllegalArgumentException(
                    "Implementation " + implementation.getName() + " is not assignable to " + abstraction.getName());
        }
        bindings.put(abstraction, implementation);
    }

    private String formatCycle(Deque<Class<?>> stack, Class<?> repeatedType) {
        StringBuilder cycle = new StringBuilder();
        boolean append = false;

        for (Class<?> type : stack) {
            if (type == repeatedType) {
                append = true;
            }

            if (append) {
                if (!cycle.isEmpty()) {
                    cycle.append(" -> ");
                }
                cycle.append(type.getSimpleName());
            }
        }

        if (!cycle.isEmpty()) {
            cycle.append(" -> ");
        }
        cycle.append(repeatedType.getSimpleName());

        return cycle.toString();
    }
}