package jpabook.jpashop;

import jpabook.jpashop.domain.Book;
import jpabook.jpashop.domain.Order;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;

public class JpaMain {
	public static void main(String[] args) {
		//emf는 하나만 생성한다
		EntityManagerFactory emf = Persistence.createEntityManagerFactory("hello");
		//transaction 단위당 entity manager가 필요하다
		EntityManager em = emf.createEntityManager();

		EntityTransaction tx = em.getTransaction();
		tx.begin();

		try {

			Book book = new Book();
			book.setName("jpa");
			book.setAuthor("he");
			em.persist(book);

			tx.commit();
		} catch (Exception e) {
			tx.rollback();
		} finally {
			em.clear();
			em.close();
		}
		emf.close();
	}
}
