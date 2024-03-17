//package ru.dksu.dao;
//
//import org.hibernate.Session;
//import org.hibernate.SessionFactory;
//import org.springframework.stereotype.Service;
//import ru.dksu.entity.UserEntity;
//
//import java.util.List;
//
//@Service
//public class UserDao {
//    private final SessionFactory sessionFactory;
//
//    public UserDao(SessionFactory sessionFactory) {
//        this.sessionFactory = sessionFactory;
//    }
//
//    public UserEntity findById(int id) {
//        try (Session session = sessionFactory.openSession()) {
//            UserEntity user = session.get(UserEntity.class, id);
//            return user;
//        }
//    }
//
//    public List<UserEntity> findAll() {
//        try (Session session = sessionFactory.openSession()) {
//            return session.createQuery("SELECT u FROM UserEntity u", UserEntity.class).getResultList();
//        }
//    }
//
//    public void insert(UserEntity user) {
//        try (Session session = sessionFactory.openSession()){
//            session.beginTransaction();
//            session.persist(user);
//            session.flush();
//        }
//    }
//
//    public void delete(int id) {
//        try (Session session = sessionFactory.openSession()){
//            UserEntity user = session.get(UserEntity.class, id);
//            session.beginTransaction();
//            session.remove(user);
//            session.flush();
//        }
//    }
//}
