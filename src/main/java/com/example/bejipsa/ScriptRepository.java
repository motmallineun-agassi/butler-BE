package com.example.bejipsa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ScriptRepository extends JpaRepository<Script, Integer> {
    @Query(nativeQuery = true, value = "SELECT * FROM script orders LIMIT 0, 122")
    List<Script> findCharacter1();

    @Query(nativeQuery = true, value = "SELECT * FROM script orders LIMIT 122, 210")
    List<Script> findCharacter2();

    @Query(nativeQuery = true, value = "SELECT * FROM script orders LIMIT 332, 194")
    List<Script> findCharacter3();

    @Query(nativeQuery = true, value = "SELECT * FROM script orders LIMIT 526, 170")
    List<Script> findCharacter4();
}
