package io.security.corespringsecurity.security.service;

import io.security.corespringsecurity.domain.Resources;
import io.security.corespringsecurity.repository.ResourcesRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.SecurityConfig;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UrlSecurityResourceMapper implements UrlResourceMapper {

    private final ResourcesRepository repository;

    @Override
    public LinkedHashMap<RequestMatcher, List<ConfigAttribute>> getResources() {
        LinkedHashMap<RequestMatcher, List<ConfigAttribute>> result = new LinkedHashMap<>();
        List<Resources> allResources = repository.findAllResources();

        for (Resources resources : allResources) {
            List<ConfigAttribute> roleList = resources.getRoleSet().stream().map(m -> new SecurityConfig(m.getRoleName())).collect(Collectors.toList());
            result.put(new AntPathRequestMatcher(resources.getResourceName()), roleList);
        }

        return result;
    }
}
