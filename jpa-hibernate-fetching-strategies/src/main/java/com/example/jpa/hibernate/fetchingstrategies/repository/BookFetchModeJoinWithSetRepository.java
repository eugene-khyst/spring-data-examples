package com.example.jpa.hibernate.fetchingstrategies.repository;

import com.example.jpa.hibernate.fetchingstrategies.entity.BookFetchModeJoinWithSet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookFetchModeJoinWithSetRepository extends JpaRepository<BookFetchModeJoinWithSet, Long> {

}
