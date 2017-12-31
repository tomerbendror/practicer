package com.practice.repository;

import com.practice.model.BaseEntity;
import com.practice.security.UserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.persistence.EntityManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: tomer
 */
public class BaseRepository {
    @Autowired
    protected GenericDao genericDao;

    protected EntityManager getEm() {
        return genericDao.getEm();
    }

    protected <T extends BaseEntity> Map<Long, T> getEntityMap(List<T> entitiesList) {
        Map<Long, T> retMap = new HashMap<>(entitiesList.size());
        for (T entity : entitiesList) {
            retMap.put(entity.getId(), entity);
        }
        return retMap;
    }

    protected UserDetails getUserDetails() {
        return (UserDetails) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
