public class Util {
	private Util() {
		throw new Error("no instances");
	}

	public static String join(Iterable<?> list) {
		StringBuilder sb = new StringBuilder();
		for (Object obj : list) {
			if (sb.length() > 0) sb.append(", ");
			sb.append(obj);
		}
		return sb.toString();
	}
}
