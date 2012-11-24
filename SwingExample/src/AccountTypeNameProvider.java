


import java.util.HashMap;
import java.util.Map;

import combo.interfaces.NameProvider;

public final class AccountTypeNameProvider implements NameProvider<AccountType>
{
	private static final Map<AccountType, String> NAMES = new HashMap<AccountType, String>();
	private static final Map<String, AccountType> VALUES = new HashMap<String, AccountType>();

	static
	{
		NAMES.put(AccountType.DEMO, "Demo");
		NAMES.put(AccountType.REAL, "Real");
		NAMES.put(AccountType.TDA_REAL, "TDA Real");
		NAMES.put(AccountType.TDA_DEMO, "TDA Demo");

		for(Map.Entry<AccountType, String> entry : NAMES.entrySet())
			VALUES.put(entry.getValue(), entry.getKey());
	}

	/*
	 * NameProvider
	 */

	public String getName(AccountType key)
	{
		if(key == null)
			return "<empty>";

		String name = NAMES.get(key);
		return (name != null) ? name : key.toString();

	}

	public AccountType getKey(String name)
	{
		return VALUES.get(name);
	}
}
