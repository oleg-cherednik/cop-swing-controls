package combo.interfaces;

public interface NameProviderSupport<T>
{
	void setNameProvider(NameProvider<T> nameProvider);

	NameProvider<T> getNameProvider();
}
