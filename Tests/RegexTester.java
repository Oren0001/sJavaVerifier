package Tests;

import java.util.regex.Pattern;
import java.util.regex.Matcher;

public class RegexTester {

	private static void regexFind(String pattern, String s) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(s);
		while (m.find()) {
			System.out.println(s.substring(m.start(), m.end()));
		}
	}


	private static void regexMatch(String pattern, String s) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(s);
		if (m.matches()) {
			System.out.println(s.substring(m.start(), m.end()));
		}
	}


	private static void regexLookingAt(String pattern, String s) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(s);
		if (m.lookingAt()) {
			System.out.println(s.substring(m.start(), m.end()));
		}
	}


	private static Matcher m(String pattern, String s) {
		Pattern p = Pattern.compile(pattern);
		Matcher m = p.matcher(s);
		return m;
	}

	public static void main(String[] args) {
		Matcher test=m("//.*", "//fdfd");
		System.out.println(test.matches());
	}
}

