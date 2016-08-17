package moe.mickey.minecraft.skin.fx;

import java.util.Arrays;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public interface FunctionHelper {
	
	public static <T> void alway(Consumer<T> consumer, T... ts) {
		Arrays.asList(ts).forEach(consumer);
	}
	
	public static <A, B> void alwayA(BiConsumer<A, B> consumer, A a, B... bs) {
		Arrays.asList(bs).forEach(b -> consumer.accept(a, b));
	}
	
	public static <A, B> void alwayB(BiConsumer<A, B> consumer, B b, A... as) {
		Arrays.asList(as).forEach(a -> consumer.accept(a, b));
	}
	
	public static <A, B> BiConsumer<B, A> exchange(BiConsumer<A, B> consumer) {
		return (b, a) -> consumer.accept(a, b);
	}
	
	public static <A, B> Consumer<A> link1(Function<A, B> function, Consumer<B> consumer) {
		return a -> consumer.accept(function.apply(a));
	}
	
	public static <A, B, C> BiConsumer<A, C> link2(Function<A, B> function, BiConsumer<B, C> consumer) {
		return (a, c) -> consumer.accept(function.apply(a), c);
	}
	
	public static <A, B> Consumer<B> link2(Supplier<A> supplier, BiConsumer<A, B> consumer) {
		return b -> consumer.accept(supplier.get(), b);
	}
	
	public static <A, B> Supplier<B> link1(Supplier<A> supplier, Function<A, B> function) {
		return () -> function.apply(supplier.get());
	}

}