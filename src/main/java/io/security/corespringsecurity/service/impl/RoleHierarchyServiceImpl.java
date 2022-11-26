package io.security.corespringsecurity.service.impl;

import io.security.corespringsecurity.domain.RoleHierarchy;
import io.security.corespringsecurity.repository.RoleHierarchyRepository;
import io.security.corespringsecurity.service.RoleHierarchyService;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Iterator;
import java.util.List;

@Service
public class RoleHierarchyServiceImpl implements RoleHierarchyService {

    @Setter(onMethod_ = @Autowired)
    private RoleHierarchyRepository roleHierarchyRepository;

    @Override
    @Transactional
    public String findAllHierarchy() {
        List<RoleHierarchy> roleHierarchies = roleHierarchyRepository.findAll();

        Iterator<RoleHierarchy> iterator = roleHierarchies.iterator();
        StringBuilder concatRoles = new StringBuilder();

        while(iterator.hasNext()) {
            RoleHierarchy hierarchy = iterator.next();

            if (hierarchy.getParentName() != null) {
                concatRoles.append(hierarchy.getParentName().getChildName());
                concatRoles.append(" > ");
                concatRoles.append(hierarchy.getChildName());
                concatRoles.append("\n");
            }
        }
        return concatRoles.toString();
    }
}
