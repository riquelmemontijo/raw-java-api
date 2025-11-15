package router;

import com.sun.net.httpserver.HttpExchange;
import router.anotacoes.ApiApplication;
import router.anotacoes.Controller;
import router.anotacoes.Delete;
import router.anotacoes.Get;
import router.anotacoes.Post;
import router.anotacoes.Put;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class AnnotationScanner {

    public List<Route> scan() {
        List<Route> routes = new ArrayList<>();
        String basePackage = findBasePackage();
        try {
            List<Class<?>> allApplicationClasses = findClassesInPackage(basePackage);

            for (Class<?> clazz : allApplicationClasses) {
                if (clazz.isAnnotationPresent(Controller.class)) {
                    Controller controllerAnnotation = clazz.getAnnotation(Controller.class);
                    String basePath = controllerAnnotation.path();
                    Object controllerInstance = clazz.getDeclaredConstructor().newInstance();
                    scanMethods(controllerInstance, basePath, routes);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Falha ao escanear e instanciar controllers", e);
        }

        return routes;
    }

    private void scanMethods(Object controllerInstance, String basePath, List<Route> routes) {
        for (Method method : controllerInstance.getClass().getDeclaredMethods()){
            if (method.isAnnotationPresent(Get.class)) {
                Get annotation = method.getAnnotation(Get.class);
                routes.add(createRoute("GET", basePath, annotation.path(), controllerInstance, method));
            } else if (method.isAnnotationPresent(Post.class)) {
                Post annotation = method.getAnnotation(Post.class);
                routes.add(createRoute("POST", basePath, annotation.path(), controllerInstance, method));
            } else if (method.isAnnotationPresent(Put.class)) {
                Put annotation = method.getAnnotation(Put.class);
                routes.add(createRoute("PUT", basePath, annotation.path(), controllerInstance, method));
            } else if (method.isAnnotationPresent(Delete.class)) {
                Delete annotation = method.getAnnotation(Delete.class);
                routes.add(createRoute("DELETE", basePath, annotation.path(), controllerInstance, method));
            }
        }
    }

    private Route createRoute(String httpMethod, String basePath, String methodPath, Object controller, Method method) {
        String fullPath = (basePath + methodPath).replaceAll("/+", "/");
        if (fullPath.length() > 1 && fullPath.endsWith("/")) {
            fullPath = fullPath.substring(0, fullPath.length() - 1);
        }

        String regexPath = fullPath.replaceAll("\\{\\w+}", "[0-9]+");
        Pattern pattern = Pattern.compile("^" + regexPath + "$");

        Consumer<HttpExchange> handler = exchange -> {
            try {
                method.invoke(controller, exchange);
            } catch (Exception e) {
                throw new RuntimeException("Erro ao invocar o método do controller", e);
            }
        };

        return new Route(httpMethod, pattern, handler);
    }

    private String findBasePackage() {
        return Stream.of(Thread.currentThread().getStackTrace())
                .map(StackTraceElement::getClassName)
                .distinct()
                .flatMap(className -> {
                    try {
                        return Stream.of(Class.forName(className));
                    } catch (ClassNotFoundException e) {
                        return Stream.empty();
                    }
                })
                .filter(clazz -> clazz.isAnnotationPresent(ApiApplication.class))
                .findFirst()
                .map(Class::getPackageName)
                .orElseThrow(() -> new IllegalStateException("Nenhuma classe encontrada com a anotação @ApiApplication na pilha de chamadas."));
    }

    private List<Class<?>> findClassesInPackage(String packageName) throws IOException, ClassNotFoundException {
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        String path = packageName.replace('.', '/');
        Enumeration<URL> resources = classLoader.getResources(path);

        List<Class<?>> classes = new ArrayList<>();
        while (resources.hasMoreElements()) {
            URL resource = resources.nextElement();
            String filePath = URLDecoder.decode(resource.getFile(), StandardCharsets.UTF_8);
            classes.addAll(findClassesInDirectory(new File(filePath), packageName));
        }
        return classes;
    }

    private List<Class<?>> findClassesInDirectory(File directory, String packageName) throws ClassNotFoundException {
        List<Class<?>> classes = new ArrayList<>();
        if (!directory.exists()) {
            return classes;
        }

        File[] files = directory.listFiles();
        if (files == null) {
            return classes;
        }

        for (File file : files) {
            if (file.isDirectory()) {
                String nextPackageName = packageName.isEmpty() ? file.getName() : packageName + "." + file.getName();
                classes.addAll(findClassesInDirectory(file, nextPackageName));
            } else if (file.getName().endsWith(".class")) {
                String simpleClassName = file.getName().substring(0, file.getName().length() - 6);
                String fullClassName = packageName.isEmpty() ? simpleClassName : packageName + "." + simpleClassName;
                classes.add(Class.forName(fullClassName));
            }
        }
        return classes;
    }
}
