package com.tellem.config;

import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import com.nimbusds.jose.jwk.source.ImmutableJWKSet;
import com.nimbusds.jose.jwk.source.JWKSource;
import com.nimbusds.jose.proc.SecurityContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.authorization.client.InMemoryRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configuration.OAuth2AuthorizationServerConfiguration;
import org.springframework.security.oauth2.server.authorization.config.annotation.web.configurers.OAuth2AuthorizationServerConfigurer;
import org.springframework.security.oauth2.server.authorization.settings.AuthorizationServerSettings;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.RSAPublicKey;
import java.util.UUID;

@EnableWebSecurity
@Configuration
public class SecurityConfig {

    @Bean
    @Order(1)
    public SecurityFilterChain webFilterChainForOAuth(HttpSecurity httpSecurity) throws Exception {
        OAuth2AuthorizationServerConfiguration.applyDefaultSecurity(httpSecurity);
        httpSecurity.csrf().disable();
        httpSecurity.getConfigurer(OAuth2AuthorizationServerConfigurer.class)
                .oidc(Customizer.withDefaults());

        httpSecurity.exceptionHandling(e -> e.authenticationEntryPoint((new LoginUrlAuthenticationEntryPoint("/login"))));

        return httpSecurity.build();
    }

//    @Order(2)
//    @Bean
//    public SecurityFilterChain appSecurity(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity.authorizeHttpRequests(request -> request.anyRequest().authenticated())
//                .oauth2Login(oauth2 -> oauth2.defaultSuccessUrl("/", true));
//
//        return httpSecurity.build();
//    }

    @Bean
    public UserDetailsService userDetailsService() {
        var user = User.withUsername("user").password("password").authorities("read").roles("USER").build();

        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public RegisteredClientRepository registeredClientRepository() {
        var registeredClient = RegisteredClient.withId(UUID.randomUUID().toString()).clientId("fdggdfgdf").clientSecret("gfdgfdgfdgfd").scope(OidcScopes.OPENID).scope(OidcScopes.PROFILE).redirectUri("fsdfdsfsdfsd")
                .clientAuthenticationMethod(ClientAuthenticationMethod.NONE)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
                .authorizationGrantTypes(grantType -> {
                            grantType.add(AuthorizationGrantType.AUTHORIZATION_CODE);
                            grantType.add(AuthorizationGrantType.REFRESH_TOKEN);
                            grantType.add(AuthorizationGrantType.CLIENT_CREDENTIALS);
                        }
                ).clientSettings(ClientSettings.builder().requireProofKey(true).build()).build();

        return new InMemoryRegisteredClientRepository(registeredClient);
    }

    @Bean
    public AuthorizationServerSettings authorizationServerSettings() {
        return AuthorizationServerSettings.builder().build();
    }

    @Bean
    public JWKSource<SecurityContext> jwkSource() throws NoSuchAlgorithmException {
        KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
        keyPairGenerator.initialize(2048);

        var keys = keyPairGenerator.generateKeyPair();
        var publicKey = (RSAPublicKey) keys.getPublic();
        var privateKey = keys.getPrivate();

        var rsaKey = new RSAKey.Builder(publicKey).privateKey(privateKey).keyID(UUID.randomUUID().toString()).build();

        JWKSet jwkSet = new JWKSet(rsaKey);
        return new ImmutableJWKSet<>(jwkSet);
    }

    @Bean
    public JwtDecoder jwtDecoder(JWKSource<SecurityContext> jwkSource) {
        return OAuth2AuthorizationServerConfiguration.jwtDecoder(jwkSource);
    }

}