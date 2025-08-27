package ru.practicum.repository;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.model.User;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    @Query("select u from User u order by u.id")
    List<User> findAllOrderById(PageRequest page);

    List<User> findAllByIdIn(List<Long> ids);

    void deleteUserById(Long id);

    Optional<User> findByName(String name);
}