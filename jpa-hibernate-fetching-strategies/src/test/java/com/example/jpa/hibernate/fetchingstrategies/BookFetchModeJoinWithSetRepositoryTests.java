package com.example.jpa.hibernate.fetchingstrategies;

import com.example.jpa.hibernate.fetchingstrategies.entity.Book;
import com.example.jpa.hibernate.fetchingstrategies.entity.BookFetchModeJoinWithSet;
import com.example.jpa.hibernate.fetchingstrategies.repository.BookFetchModeJoinWithSetRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
public class BookFetchModeJoinWithSetRepositoryTests extends BookRepositoryTestsBase<BookFetchModeJoinWithSet> {

    @Autowired
    private BookFetchModeJoinWithSetRepository bookRepository;

    @Override
    protected BookFetchModeJoinWithSet newBook() {
        return new BookFetchModeJoinWithSet();
    }

    @Override
    protected JpaRepository<BookFetchModeJoinWithSet, Long> getBookRepository() {
        return bookRepository;
    }

    @Transactional
    @Test
    @Order(1)
    void testFindById() {
        log.info("BookFetchModeJoinWithSetRepository#getOne(long)");

        BookFetchModeJoinWithSet poeaa = bookRepository.getOne(this.poeaa.getId());
        assertThat(poeaa.getTitle()).isEqualTo(this.poeaa.getTitle());
        assertThatHasAuthors(poeaa, martinFowler.getFullName());

        BookFetchModeJoinWithSet eip = getBookRepository().getOne(this.eip.getId());
        assertThat(eip.getTitle()).isEqualTo(this.eip.getTitle());
        assertThatHasAuthors(eip, gregorHohpe.getFullName(), bobbyWoolf.getFullName());
    }
}
