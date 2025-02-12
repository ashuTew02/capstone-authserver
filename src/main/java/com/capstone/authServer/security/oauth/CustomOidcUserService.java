package com.capstone.authServer.security.oauth;

import com.capstone.authServer.model.User;
import com.capstone.authServer.service.UserService;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class CustomOidcUserService implements OAuth2UserService<OidcUserRequest, OidcUser> {

    private final UserService userService;

    public CustomOidcUserService(UserService userService) {
        this.userService = userService;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) {
        // Delegate to default
        OidcUser oidcUser = new OidcUserService().loadUser(userRequest);

        // Extract attributes
        Map<String, Object> attributes = oidcUser.getAttributes();
        String oauthId = (String) attributes.get("sub");
        String email   = (String) attributes.get("email");
        String name    = (String) attributes.get("name");
        String picture = (String) attributes.get("picture");

        // Upsert
        User user = userService.handleOAuth2User(oauthId, email, name, picture, "google");

        // Return a new DefaultOidcUser
        return new DefaultOidcUser(
            oidcUser.getAuthorities(),
            oidcUser.getIdToken(),
            oidcUser.getUserInfo(),
            "email"
        );
    }
}
