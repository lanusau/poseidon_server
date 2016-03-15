package com.untd.database.poseidon.dao;

import java.io.Serializable;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

@Transactional
public class HibernateDao<T, ID extends Serializable> {

	protected SessionFactory sessionFactory;
	private Class<T> type;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	public SessionFactory getSessionFactory() {
		return sessionFactory;
	}

	public HibernateDao(Class<T> type) {
		this.type = type;
	}

	public T save(T entity) {
		Assert.notNull(entity);
		sessionFactory.getCurrentSession().save(entity);
		return entity;
	}
	
	public T update(T entity) {
		Assert.notNull(entity);
		sessionFactory.getCurrentSession().update(entity);
		return entity;
	}

	@SuppressWarnings("unchecked")
	public T findOne(ID primaryKey) {
		Assert.notNull(primaryKey);
		return (T) sessionFactory.getCurrentSession().get(type, primaryKey);
	}

	@SuppressWarnings("unchecked")
	public List<T> findAll() {
		return sessionFactory.getCurrentSession().createCriteria(type).list();
	}

	public Long count() {
		return (Long) sessionFactory.getCurrentSession().createCriteria(type).setProjection(Projections.rowCount())
				.uniqueResult();

	}

	public void delete(T entity) {
		Assert.notNull(entity);
		sessionFactory.getCurrentSession().delete(entity);
	}

	public boolean exists(ID primaryKey) {
		Assert.notNull(primaryKey);
		return findOne(primaryKey) == null ? false : true;
	}

}
