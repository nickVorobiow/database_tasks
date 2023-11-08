package org.example.dao.impl;

import lombok.RequiredArgsConstructor;
import org.example.utils.HibernateUtil;
import org.example.dao.UserDao;
import org.example.exceptions.ObjectNotFoundException;
import org.example.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Component;

import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

//@Primary
@Component
@RequiredArgsConstructor
public class UserDaoImplCriteriaAPI implements UserDao {

    public User getById(Long id) {

        User user = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            user = session.get(User.class, id);
        } catch (Exception e) {
            e.printStackTrace();
        }

        if (user == null) {
            throw new ObjectNotFoundException("No user with id = " + id);
        }

        return user;
    }

    public List<User> findAllUsers() {

        List<User> users = new ArrayList<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
            Root<User> root = criteriaQuery.from(User.class);

            criteriaQuery.select(root);
            users = session.createQuery(criteriaQuery).getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    public User createUser(User user) {

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.save(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }

        return user;
    }

    @Override
    public List<User> getUsersForParams(Integer from, Integer size, String nameFilter) {

        List<User> users = new ArrayList<>();

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<User> criteriaQuery = criteriaBuilder.createQuery(User.class);
            Root<User> root = criteriaQuery.from(User.class);
            Predicate namePredicate = criteriaBuilder.like(criteriaBuilder.lower(root.get(("firstName"))), nameFilter.toLowerCase());

            criteriaQuery.where(namePredicate);
            criteriaQuery.orderBy(criteriaBuilder.asc(root.get("id")));

            TypedQuery<User> typedQuery = session.createQuery(criteriaQuery);

            typedQuery.setFirstResult(from);
            typedQuery.setMaxResults(size);

            users = typedQuery.getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    public User updateUser(Long id, User user) {

        getById(id);
        user.setId(id);

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.update(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }

        return user;
    }

    @Override
    public void deleteUser(Long id) {

        Transaction transaction = null;
        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();
            User user = session.load(User.class, id);

            session.delete(user);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
}
