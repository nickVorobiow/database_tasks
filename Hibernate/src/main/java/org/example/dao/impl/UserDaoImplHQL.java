package org.example.dao.impl;

import lombok.RequiredArgsConstructor;
import org.example.utils.HibernateUtil;
import org.example.dao.UserDao;
import org.example.exceptions.ObjectNotFoundException;
import org.example.model.User;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Primary
@Component
@RequiredArgsConstructor
public class UserDaoImplHQL implements UserDao {

    public User getById(Long id) {

        User user = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            user = session.createQuery("FROM User WHERE id = :id", User.class)
                    .setParameter("id", id)
                    .uniqueResult();
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
            users = session.createQuery("from User", User.class).getResultList();
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
            String hql = "FROM User WHERE lower(firstName) LIKE :nameFilter ORDER BY id ASC";
            users = session.createQuery(hql, User.class)
                    .setParameter("nameFilter", nameFilter)
                    .setFirstResult(from)
                    .setMaxResults(size)
                    .getResultList();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return users;
    }

    public User updateUser(Long id, User user) {

        getById(id);

        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.createQuery("UPDATE User SET lastName = :lastName, firstName = :firstName, patronymic = :patronymic, dateOfBirth = :dateOfBirth, email = :email " +
                            "WHERE id = :id")
                    .setParameter("lastName", user.getLastName())
                    .setParameter("firstName", user.getFirstName())
                    .setParameter("patronymic", user.getPatronymic())
                    .setParameter("dateOfBirth", user.getDateOfBirth())
                    .setParameter("email", user.getEmail())
                    .setParameter("id", id)
                    .executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }

        return getById(id);
    }

    @Override
    public void deleteUser(Long id) {

        Transaction transaction = null;

        try (Session session = HibernateUtil.getSessionFactory().openSession()) {
            transaction = session.beginTransaction();

            session.createQuery("DELETE FROM User WHERE id = :id")
                    .setParameter("id", id)
                    .executeUpdate();
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
    }
}
