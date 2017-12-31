package com.practice.repository;

import org.hibernate.Filter;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Session;
import org.hibernate.engine.jdbc.NonContextualLobCreator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.*;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.Blob;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: tomer
 */
@Repository
@Transactional
public class GenericDao {

    @PersistenceContext
    private EntityManager em;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private NamedParameterJdbcTemplate namedJdbcTemplate;

    public void flush() {
        em.flush();
    }

    public <T> void persist(T obj) {
        em.persist(obj);
    }

    public void clear() {
        em.clear();
    }

    public void remove(Class clazz, long id) {
        Object o = em.getReference(clazz, id);
        em.remove(o);
    }

    public <T> T merge(T obj) {
        T merged = em.merge(obj);
        em.flush();
        return merged;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> query(Class<T> c, String queryString, final Object... params) {
        return query(false, c, queryString, params);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> query(Class<T> c, String queryString, Map<String, Object> parameters) {
        return query(false, c, queryString, parameters);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> query(Class<T> c, String queryString, Map<String, Object> parameters, Integer maxResult) {
        return query(false, c, queryString, parameters, maxResult);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> queryImmutable(Class<T> c, String queryString, final Object... params) {
        return query(true, c, queryString, params);
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> queryImmutable(Class<T> c, String queryString, Map<String, Object> parameters) {
        return query(true, c, queryString, parameters);
    }

    private <T> List<T> query(boolean readOnly, Class<T> c, String queryString, Map<String, Object> params) {
        return query(readOnly, c, queryString, params, null);
    }

    private <T> List<T> query(boolean readOnly, Class<T> c, String queryString, Map<String, Object> params, Integer maxResult) {
        Query query = createQuery(readOnly, c, queryString);
        if (maxResult != null) {
            query.setMaxResults(maxResult);
        }
        return insertParametersToQuery(query, params).getResultList();
    }

    private <T> List<T> query(boolean readOnly, Class<T> c, String queryString, final Object... params) {
        Query query = createQuery(readOnly, c, queryString);
        return insertParametersToQuery(query, params).getResultList();
    }

    private <T> Query createQuery(boolean readOnly, Class<T> c, String queryString){
        Query query = em.createQuery("from " + c.getSimpleName() + " " + queryString);
        query.setHint("org.hibernate.readOnly", readOnly);
        query.setHint("org.hibernate.cacheable", readOnly);
        return query;
    }

    private Query insertParametersToQuery(Query query,  Object... params){
        int i = 1;
        for (Object param : params) query.setParameter(i++, param);
        return query;
    }

    private Query insertParametersToQuery(Query query,  Map<String, Object> params){
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query;
    }

    public List queryRaw(String queryString, final Object... params) {
        Query query = em.createQuery(queryString);
        int i = 1;
        for (Object param : params) query.setParameter(i++, param);
        return query.getResultList();
    }

    public List queryRaw(String queryString, final Map<String,Object> params) {
        Query query = em.createQuery(queryString);
        // Set query parameter values
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }
        return query.getResultList();
    }

    //----------------------------------------------------------------
    // Returns only first result from query, or null if nothing found
    //----------------------------------------------------------------

    public <T> T queryFirst(Class<T> c, String queryString, final Object... params) {
        List<T> list = query(c, queryString, params);
        if (list.size() == 0) return null;
        return list.get(0);
    }

    public <T> T queryFirst(Class<T> c, String queryString, Map<String, Object> parameters) {
        List<T> list = query(c, queryString, parameters);
        if (list.size() == 0) return null;
        return list.get(0);
    }


    public Object queryFirstRaw(String queryString, final Object... params) {
        List list = queryRaw(queryString, params);
        if (list.size() == 0) return null;
        return list.get(0);
    }

    public Object queryFirstRaw(String queryString, Map<String, Object> params) {
        List list = queryRaw(queryString, params);
        if (list.size() == 0) return null;
        return list.get(0);
    }

    public int queryUpdate(String queryString, Object... parameters) {
        Query query = em.createQuery(queryString);
        int i = 1;
        for (Object param : parameters) query.setParameter(i++, param);
        return query.executeUpdate();
    }

    /**
     *
     * @param update true-update, false-delete
     * @param c
     * @param queryString
     * @param params
     * @param <T>
     * @return
     */
    public <T> int  queryUpdate(boolean update, Class<T> c, String queryString, final Map<String,Object> params) {
        Query query = em.createQuery(((update)? "update " : "delete ") + c.getSimpleName() + " "  + queryString);
        return insertParametersToQuery(query, params).executeUpdate();
    }

    public long count(String countQueryString, final Object... params) {
        Long count = (Long) queryFirstRaw("select count(*) " + countQueryString, params);
        return count == null ? 0 : count;
    }

    public long count(String countQueryString, Map<String, Object> params) {
        Long count = (Long) queryFirstRaw("select count(*) " + countQueryString, params);
        return count == null ? 0 : count;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> queryRestrictedBulk(Class<T> c, int startIndex, int amount, String queryString, final Object... params) {
        Query query = em.createQuery("from " + c.getSimpleName() + " " + queryString);

        int i = 1;
        for (Object param : params) query.setParameter(i++, param);
        query.setFirstResult(startIndex);
        if (startIndex == 0) {
            amount += 1;
        }
        query.setMaxResults(amount);
        return query.getResultList();
    }


    @SuppressWarnings("unchecked")
    public <T> List<T> queryRestrictedBulk(Class<T> c, int startIndex, int amount, String queryString, Map<String, Object> parameters) {
        Query query = em.createQuery("from  " + c.getSimpleName() + " " + queryString);

        // Set query parameter values
        for (Map.Entry<String, Object> entry : parameters.entrySet()) {
            query.setParameter(entry.getKey(), entry.getValue());
        }

        query.setFirstResult(startIndex);
        if (startIndex == 0) {
            amount += 1;
        }
        query.setMaxResults(amount);
        return query.getResultList();
    }

    /**
     * return the number of elements when using  ("SELECT device.swVersion, COUNT(device)   FROM Customer c GROUP BY c.country ");
     */
    public <T> Map<Object, Long> countPerValue(Class<T> clazz, String attribute) {
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        sb.append("e.");
        sb.append(attribute);
        sb.append(", count(e) from ");
        sb.append(clazz.getSimpleName());
        sb.append(" e group by e.");
        sb.append(attribute);
        List<Object[]> list = queryRaw(sb.toString());
        Map<Object, Long> retVal = new HashMap<Object, Long>();
        for (Object[] objs : list) {
            if (objs[0] != null) {
                retVal.put(objs[0], (Long) objs[1]);
            } else {
                retVal.put(Empty.getInstance(), (Long) objs[1]);
            }
        }
        return retVal;
    }

    //-------------------------------------------------------------------------------------
    // Returns number of elements when using 'select count(*) from <clazz> where ...' syntax
    //-------------------------------------------------------------------------------------

    public <T> long count(Class<T> clazz, String countQueryString, final Object... params) {
        StringBuffer sb = new StringBuffer();
        sb.append("from ");
        sb.append(clazz.getSimpleName());
        sb.append(" ");
        sb.append(countQueryString);
        return count(sb.toString(), params);
    }

    public <T> long count(Class<T> clazz, String countQueryString, Map<String, Object> params) {
        StringBuffer sb = new StringBuffer();
        sb.append("from ");
        sb.append(clazz.getSimpleName());
        sb.append(" ");
        sb.append(countQueryString);
        return count(sb.toString(), params);
    }


    public long sequenceNextVal(String sequence) {
        return sequenceNextValImpl(sequence);
    }

    public long sequenceNextVal() {
        return sequenceNextValImpl("HIBERNATE_SEQUENCE");
    }

    private long sequenceNextValImpl(String sequence) {
        Query query = em.createNativeQuery("select " + sequence + ".nextval from DUAL");
        List list = query.getResultList();
        BigDecimal newID = (BigDecimal) list.get(0);
        return newID.longValue();
    }

    //-----------------------------------------
    // Returns object if found, or null if not.
    //-----------------------------------------

    public <T> T get(Class<T> clazz, Object id) {
        return em.find(clazz, id);
    }

    //---------------------------------------------------------------------
    // loads object, and throws RuntimeException if there is no such object
    //---------------------------------------------------------------------

    public <T> T find(Class<T> clazz, Object id) {
        return em.find(clazz, id);
    }

    //---------------------------------------------------------------------
    // loads object, and throws RuntimeException if there is no such object
    //---------------------------------------------------------------------

//    public <T> T load(Class<T> clazz, Object id, Long organizationId) {
//        TypedQuery<T> query = em.createQuery("from " + clazz.getSimpleName() + " where id=" + id + " and organization_id = " + organizationId, clazz);
//        return query.getSingleResult();
//    }

    public void refresh(Object o) {
        em.refresh(o);
    }
//
//    public void refresh(String entityName, Serializable identifier) {
//        try {
//            refresh(load(Class.forName(entityName), identifier));
//        } catch (Throwable t) {}
//    }

    public void executeSP(String spName) throws DataAccessException {
        String spStatement = "{call " + spName + " }";
        jdbcTemplate.execute(spStatement);
    }
    public void updateJDBC(String sqlQuery, Object... objs) throws DataAccessException {
        jdbcTemplate.update(sqlQuery,objs);
    }

    public int countJDBC(String sqlQuery) {
        return jdbcTemplate.queryForInt(sqlQuery);
    }

    public List queryJDBC(String sqlQuery,Class clazz) throws DataAccessException{
        return  jdbcTemplate.query(sqlQuery, ParameterizedBeanPropertyRowMapper.newInstance(clazz));
    }


    @SuppressWarnings("unchecked")
    public List queryJDBC(String sqlQuery, RowMapper<?> mapper , Map<String,Object> params) throws DataAccessException {
        return namedJdbcTemplate.query(sqlQuery, params, mapper);
    }

    @SuppressWarnings("unchecked")
    public List queryJDBC(String sqlQuery, RowMapper<?> mapper , Object...params) throws DataAccessException {
        return jdbcTemplate.query(sqlQuery, mapper, params);
    }

    @SuppressWarnings("unchecked")
    public int queryJdbcUpdate(String sqlQuery) throws DataAccessException {
        return jdbcTemplate.update(sqlQuery);
    }

    /**
     * Please not that  Hibernate.createBlob(input)  cause null pointer exception.
     *
     * @param input
     * @param size
     * @return
     */
    public Blob createBlob(java.io.InputStream input, long size) {
        return NonContextualLobCreator.INSTANCE.wrap(NonContextualLobCreator.INSTANCE.createBlob(input, size));
    }

    public Filter enableFilter(String filterName) {
        return getHibernateSession().enableFilter(filterName);
    }

    public void disableFilter(String filterName) {
        getHibernateSession().disableFilter(filterName);
    }

    public void evict(Object entity) {
        getHibernateSession().evict(entity);
    }

    private Session getHibernateSession() {
        return (Session) em.getDelegate();
    }

    public <T> T getFirst(Query query) {
        query.setMaxResults(1);
        return getSingleResult(query);
    }

    @SuppressWarnings("unchecked")
    public <T> T getSingleResult(Query query) {
        try {
            return (T) query.getSingleResult();
        } catch (NoResultException | EmptyResultDataAccessException e) {
            return null;
        }
    }

    public EntityManager getEm() {
        return em;
    }
}

