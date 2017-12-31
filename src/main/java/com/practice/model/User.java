package com.practice.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.common.collect.Sets;
import com.practice.security.PracticerAuthority;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.Length;
import org.springframework.security.core.GrantedAuthority;

import javax.persistence.*;
import javax.validation.constraints.Past;
import java.util.*;

/**
 * User: tomer
 */
@Entity
@Table(name="user")
@Inheritance(strategy=InheritanceType.JOINED)
public class User extends BaseEntity {

    @Email(message = "יש להזין כתובת אימייל חוקית")
    @Column(name = "EMAIL", length = 100)
    private String email;

    @Length(min=2, max=15, message = "יש להזין שם פרטי בן 2-15 אותיות")
    @Column(name = "FIRST_NAME", length = 100)
    private String firstName;

    @Length(min=2, max=15)
    @Column(name = "LAST_NAME", length = 100)
    private String lastName;

//    @NotNull @Length(min=4, max=15)
    @Column(name = "USER_NAME", length = 100)
    private String userName;

    @Length(min=6, max=100)
    @Column(name = "PASSWORD", length = 100)
    @JsonIgnore
    private String password;

    @Column(name = "GENDER", length = 20)
    @Enumerated(value = EnumType.STRING)
    private Gender gender;

    @Past(message = "תאריך הלידה חייב להיות ערך בעבר")
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "DOB")
    private Date birthDate;

    @Length(min=2, max=30)
    @Column(name = "DESCRIPTION")
    private String description;

    @Column(name = "PROFILE_IMAGE_URL")
    private String profileImageUrl;

    @OneToMany(orphanRemoval = true, mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<UserProperty<?>> userProperties = new ArrayList<>();

    public User() {
    }

    public List<UserProperty<?>> getUserProperties() {
        return userProperties;
    }

    public void setUserProperties(List<UserProperty<?>> userProperties) {
        this.userProperties = userProperties;
    }

    public <T> T getPropertyValue(UserProperty.PropertyKey<T> propertyKey) {
        for (UserProperty<?> userProperty : getUserProperties()) {
            if (userProperty.getPropertyName().equals(propertyKey.name)) {
                @SuppressWarnings("unchecked")
                T value = (T)userProperty.getValue();
                return value;
            }
        }
        return propertyKey.defaultVal;
    }

    @SuppressWarnings("unchecked")
    public <T> void setPropertyValue(UserProperty.PropertyKey<T> propertyKey, T value) {
        UserProperty<T> toUpdate = null;
        for (UserProperty<?> userProperty : getUserProperties()) {
            if (userProperty.getPropertyName().equals(propertyKey.name)) {
                toUpdate = (UserProperty<T>) userProperty;
                break;
            }
        }

        if (toUpdate == null) {
            if (!Objects.equals(propertyKey.defaultVal, value)) {
                toUpdate = new UserProperty<>(this, propertyKey, value);
                getUserProperties().add(toUpdate);
            }
        } else {
            if (Objects.equals(propertyKey.defaultVal, value)) {
                clearProperty(propertyKey);
            } else {
                toUpdate.setValue(value);
            }
        }
    }

    public boolean clearProperty(UserProperty.PropertyKey<?> propertyKey) {
        UserProperty<?> toRemove = null;
        for (UserProperty<?> userProperty : getUserProperties()) {
            if (userProperty.getPropertyName().equals(propertyKey.name)) {
                toRemove = userProperty;
                break;
            }
        }

        if (toRemove == null) {
            return false;
        } else {
            getUserProperties().remove(toRemove);
            return true;
        }
    }

    public String getActualUserName() {
        return email;
    }

    @JsonIgnore
    public Set<GrantedAuthority> getAuthorities() {
        return Sets.newHashSet(PracticerAuthority.USER.role);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof BaseEntity)) return false;
        BaseEntity that = (BaseEntity) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @JsonIgnore
    public String getPassword() {
        return password;
    }

    @JsonProperty
    public void setPassword(String password) {
        this.password = password;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public void setProfileImageUrl(String profileImageUrl) {
        this.profileImageUrl = profileImageUrl;
    }

    public String getFullName() {
        return (StringUtils.isBlank(getFirstName()) ? "" : getFirstName() + " ") +  (StringUtils.isBlank(getFirstName()) ? "" : getLastName());
    }

    @Override
    public String toString() {
        return "userName: " + userName;
    }

    public String getDisplayName() {
        if (StringUtils.isNotBlank(getFullName())) {
            return getFullName();
        }
        return getActualUserName();
    }
}
