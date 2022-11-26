package io.security.corespringsecurity.security.voter;

import io.security.corespringsecurity.domain.AccessIp;
import io.security.corespringsecurity.repository.AccessIpRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import java.util.Collection;
import java.util.Optional;

@RequiredArgsConstructor
public class IpAddressVoter implements AccessDecisionVoter<Object> {

    private final AccessIpRepository accessIpRepository;

    private String rolePrefix = "ROLE_";

    public String getRolePrefix() {
        return rolePrefix;
    }

    public void setRolePrefix(String rolePrefix) {
        this.rolePrefix = rolePrefix;
    }

    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return true;
    }

    @Override
    public int vote(Authentication authentication, Object object, Collection<ConfigAttribute> attributes) {
        if (authentication == null) {
            return ACCESS_DENIED;
        }

        String clientIp = obtainClientIp(authentication);
        Optional<AccessIp> optionalAccessIp = accessIpRepository.findByIpAddress(clientIp);
        if (optionalAccessIp.isPresent()){
            throw new AccessDeniedException("Ip is Blocked Ip : " + clientIp);
        }

        return ACCESS_ABSTAIN;
    }

    private String obtainClientIp(Authentication authentication) {
        WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
        return details.getRemoteAddress();
    }
}
