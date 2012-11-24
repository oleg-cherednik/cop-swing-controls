package combo.interfaces;

public interface NameProvider<T>
{
	String getName(T key);

	T getKey(String name);
}
