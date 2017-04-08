package webserver;

import config.Config;
import controller.Controller;
import controller.Nothing;
import util.ReflectionUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

/**
 * controller 패키지 내에 정의된 Controller 어노테이션이 붙은 class 를 검색하여, instance를 보관한다.
 */
public class ControllerRegister {

    public static Map<String, Controller> controller = ControllerRegister.collectController();
    private static final controller.Controller NOTHING = new Nothing();

    /**
     * Controller annotation의 name 속성으로 컨트롤러 인스턴스를 찾아 리턴한다.
     * @param url
     * @return
     */
    public static Controller get(final String url) {
        if(controller.containsKey(url)) {
            return controller.get(url);
        }
        return NOTHING;
    }

    /**
     * 컨트롤러를 수집한다.
     * @return
     */
    private static Map<String, Controller> collectController() {

        final List<Class<?>> classes = ReflectionUtil.getClassesInPackage(Config.CONTROLLER_PKG);

        final List<Class<?>> controllerList = ReflectionUtil.getAnnotatedClasses(classes, config.annotation.Controller.class);

        final Map<String, Controller> controllers = new HashMap<>();

        controllerList.stream().forEach(c -> {
            final String name = c.getAnnotation(config.annotation.Controller.class).name();
            controllers.put(name, getControllerInstance(c));
        });

        return Collections.unmodifiableMap(controllers);
    };

    /**
     * 컨트롤러 인스턴스를 미리 생성해둔다.
     * @param c
     * @return
     */
    private static Controller getControllerInstance(final Class c) {

        Controller cont = new Nothing();
        try {
            Constructor<?> constructor = c.getConstructor(new Class[]{});
            cont = (Controller) constructor.newInstance(new Object[]{});
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        return cont;
    }
}
