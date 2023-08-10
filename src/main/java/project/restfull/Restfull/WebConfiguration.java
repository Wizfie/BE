//package project.restfull.Restfull;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.web.method.support.HandlerMethodArgumentResolver;
//import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
//import project.restfull.Restfull.resolver.UserArgumentResolver;
//
//import java.util.List;
//
//public class WebConfiguration implements WebMvcConfigurer {
//
//
//    @Autowired
//    private UserArgumentResolver userArgumentResolver;
//
//
//    @Override
//    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
//        WebMvcConfigurer.super.addArgumentResolvers(resolvers);
//        resolvers.add(userArgumentResolver);
//    }
//}