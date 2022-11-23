package io.security.corespringsecurity.security.service;

import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.List;
import java.util.Map;

public interface ResourceMapper {

    Map<RequestMatcher, List<ConfigAttribute>> getResources();
}
