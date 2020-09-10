package Tests;

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
        String s = "void method_name (type1 parameter1, type2 parameter2, type3 parameter3) {";
        Matcher m = regexFind("\\) *\\{ *$",
                s);
        print(s.subSequence(0, 4));
    }
}
