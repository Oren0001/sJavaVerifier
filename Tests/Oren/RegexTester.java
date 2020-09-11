package Tests.Oren;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
public class RegexTester {

    /**
     * Abbreviated print.
     * @param obj Any object.
     */
    private static void print(Object obj) {
        System.out.println(obj);
    }


    private static Matcher regexFind(String pattern, String s) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(s);
        while (m.find()) {
            System.out.println(s.substring(m.start(), m.end()));
            return m;
        }
        return null;
    }


    private static Matcher regexMatch(String pattern, String s) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(s);
        if (m.matches()) {
            System.out.println(s.substring(m.start(), m.end()));
            return m;
        }
        return null;
    }



    private static Matcher regexLookingAt(String pattern, String s) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(s);
        if (m.lookingAt()) {
            System.out.println(s.substring(m.start(), m.end()));
            return m;
        }
        return null;
    }


    private static Matcher m(String pattern, String s) {
        Pattern p = Pattern.compile(pattern);
        Matcher m = p.matcher(s);
        return m;
    }


    public static void main(String[] args) {
        String line = "void foo (sdfsdfsdf";
        Pattern p = Pattern.compile("void [a-zA-Z]+[_0-9]* *\\(");
        Matcher m = p.matcher(line);
        if (m.lookingAt()) {
            String[] matches = line.substring(0, m.end() - 1).split(" ");
            print(matches.length);
            for (String match : matches)
                print(match);
        }
    }

}
