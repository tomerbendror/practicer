package com.practice.repository;

import com.practice.model.User;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.TypedQuery;

/**
 * User: tomer
 */
@Repository("userRepository")
@Transactional
public class UserRepository extends BaseRepository {

    @Autowired
    protected PasswordEncoder passwordEncoder;

    @Transactional(readOnly = true)
    public boolean exist(String email, String userName) {
        TypedQuery<User> query;
        if (StringUtils.isNotBlank(email)) {
            query = getEm().createQuery("from User u where u.userName=:userName or email=:email", User.class);
            query.setParameter("email", email);
        } else {
            query = getEm().createQuery("from User u where u.userName=:userName", User.class);
        }
        query.setParameter("userName", userName);
        User existUser = genericDao.getFirst(query);
        return existUser != null;
    }

    public boolean exist(User user) {
        return exist(user.getEmail(), user.getUserName());
    }

    @Transactional(readOnly = true)
    public UserDetails getUserSecurityDetails(String userName) {
        TypedQuery<User> query = getEm().createQuery("from ParentUser u where u.email=:userName", User.class);
        query.setParameter("userName", userName);   // for parent the user name is the email address
        User user = genericDao.getSingleResult(query);

        if (user == null) {
            query = getEm().createQuery("from ChildUser u where u.userName=:userName", User.class);
            query.setParameter("userName", userName);
            user = genericDao.getSingleResult(query);
        }
        return new com.practice.security.UserDetails(user);
    }

    @Transactional(readOnly = true)
    public User getUserByMail(String email) {
        TypedQuery<User> query = getEm().createQuery("from User where email=:email", User.class);
        query.setParameter("email", email);
        return genericDao.getSingleResult(query);
    }

    public void updateUserData(User user, String profileImageUrl) {
        user.setProfileImageUrl(profileImageUrl);
        genericDao.flush();
    }

    public <T extends User> T updatePassword(T user) {
        T existUser = getUserById(user.getId());
        existUser.setPassword(passwordEncoder.encode(user.getPassword()));
        return existUser;
    }

    public <T extends User> T getUserById(Long userId) {
        @SuppressWarnings("unchecked")
        T user = (T) getEm().find(User.class, userId);
        return user;
    }

    @Transactional(readOnly = true)
    public boolean isCurrentLoggedInUser(Long userId) {
        return getUserDetails().getId().equals(userId);
    }
}
