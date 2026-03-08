package br.com.AutoStock.config;

import br.com.AutoStock.security.TwoFactorInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

	private final TwoFactorInterceptor twoFactorInterceptor;

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(twoFactorInterceptor).addPathPatterns("/**").excludePathPatterns("/login", "/logout",
				"/verify-code", 
				"/verify-code/**", 
				"/registration/**", 
				"/styles/**",
				"/scripts/**",
				"/imgs/**",
				"/css/**",
				"/js/**",
				"/images/**",
				"/api/validate/**", 
				"/api/cnpj/**", 
				"/uploads/**",
				"/profile",
                "/profile/**",
				"/admin/**"
		);
	}

	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/uploads/**")
                .addResourceLocations("file:./uploads/");
    }
}
