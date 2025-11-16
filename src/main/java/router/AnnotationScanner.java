package router;

import com.sun.net.httpserver.HttpExchange;
import infra.controlleradvide.GlobalExceptionHandler;
import org.reflections.scanners.Scanners;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.ConfigurationBuilder;
import org.reflections.Reflections;
import router.anotacoes.*;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Consumer;
import java.util.regex.Pattern;

public class AnnotationScanner {

    public List<Route> scan() {
        List<Route> routes = new ArrayList<>();
        var config = new ConfigurationBuilder()
                .setUrls(ClasspathHelper.forJavaClassPath())
                .setScanners(Scanners.TypesAnnotated);
        Reflections reflections = new Reflections(config);
        try {
            Set<Class<?>> controllerClasses = reflections.getTypesAnnotatedWith(Controller.class);
            for (Class<?> controllerClass : controllerClasses) {
                Controller controllerAnnotation = controllerClass.getAnnotation(Controller.class);
                String basePath = controllerAnnotation.path();
                Object controllerInstance = controllerClass.getDeclaredConstructor().newInstance();
                scanMethods(controllerInstance, basePath, routes);
            }
        } catch (Exception e) {
            throw new RuntimeException("Falha ao escanear e instanciar controllers", e);
        }
        return routes;
    }

    private void scanMethods(Object controllerInstance, String basePath, List<Route> routes) {
        Map<Class<? extends Annotation>, String> httpMethodMap = Map.of(
                Get.class, "GET",
                Post.class, "POST",
                Put.class, "PUT",
                Delete.class, "DELETE"
        );

        for (Method method : controllerInstance.getClass().getDeclaredMethods()){
            httpMethodMap.forEach((annotationClass, httpMethod) -> {
                if (method.isAnnotationPresent(annotationClass)) {
                    String methodPath = getPathFromAnnotation(method, annotationClass);
                    routes.add(createRoute(httpMethod, basePath, methodPath, controllerInstance, method));
                }
            });
        }
    }

    private Route createRoute(String httpMethod, String basePath, String methodPath, Object controller, Method method) {
        String fullPath = (basePath + methodPath).replaceAll("/+", "/");
        if (fullPath.length() > 1 && fullPath.endsWith("/")) {
            fullPath = fullPath.substring(0, fullPath.length() - 1);
        }

        String regexPath = fullPath.replaceAll("\\{\\w+}", "[0-9]+");
        Pattern pattern = Pattern.compile("^" + regexPath + "$");

        Consumer<HttpExchange> handler = exchange -> GlobalExceptionHandler.handle(exchange, () -> method.invoke(controller, exchange));

        return new Route(httpMethod, pattern, handler);
    }

    private String getPathFromAnnotation(Method method, Class<? extends Annotation> annotationClass) {
        try {
            Annotation annotation = method.getAnnotation(annotationClass);
            Method pathMethod = annotationClass.getDeclaredMethod("path");
            return (String) pathMethod.invoke(annotation);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao obter o 'path' da anotação " + annotationClass.getSimpleName(), e);
        }
    }
}
