package util;

import java.io.File;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ReflectionUtil {

    static final String CLASS_REGEX = "^(.+)\\.class$";
    static final String DOT = ".";
    static final String SLASH = "/";

    /**
     * package 안에 정의된 모든 class 를 수집한다.
     */
    public static List<Class<?>> getClassesInPackage(final String packageName) {
        final String address = packageName.replace(DOT, SLASH);
        final URL scannedUrl = Thread.currentThread().getContextClassLoader().getResource(address);

        if (scannedUrl == null) {
            throw new IllegalArgumentException(packageName);
        }

        final File[] files = new File(scannedUrl.getFile()).listFiles();
        final List<Class<?>> classes = new ArrayList<>();

        for (final File file : files) {
            classes.addAll(find(file, packageName));
        }

        return classes;
    }

    /**
     * package 안에 정의된 모든 class 를 수집하여 name, class 의 Map 으로 만든다.
     * @param packageName
     * @return
     */
    public static Map<String, Class<?>> collectClassesInPackage(final String packageName) {
        return getClassesInPackage(packageName)
                .stream()
                .collect(
                        Collectors.toMap(Class::getSimpleName, Function.identity())
                );
    }

    /**
     * 어노테이션이 붙은 클래스를 수집한다.
     * @param classes
     * @param anno
     * @return
     */
    public static List<Class<?>> getAnnotatedClasses(List<Class<?>> classes, Class anno) {
        List<Class<?>> rs = new ArrayList<>();

        for (Class c : classes) {
            if(c.isAnnotationPresent(anno)) {
                rs.add(c);
            }
        }

        return rs;
    }

    /**
     * class 파일을 검색한다. [재귀] 사용.
     * @param file
     * @param packageName
     * @return
     */
    private static List<Class<?>> find(final File file, final String packageName) {

        final List<Class<?>> classes = new ArrayList<Class<?>>();

        final String resource = packageName + DOT + file.getName();

        if (file.isDirectory()) {
            Arrays.stream(file.listFiles()).forEach(
                    subFile -> classes.addAll(find(subFile, resource))  // 재귀
            );
            return classes;
        }

        if(!resource.matches(CLASS_REGEX)) {
            return classes;
        }

        final String className = RegexUtil.exec(CLASS_REGEX, resource).get(1);

        try {
            classes.add(Class.forName(className));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return classes;
    }

}
