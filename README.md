# JPA
- 객체를 테이블에 맞추어서 모델링하면 협력관계를 만들 수 없고 다시 다 조회를 해야한다.
```java
Member m = em.find(Member.class, 1);
Team t = em.find(Team.class, m.getTeamId());
```

## 연관관계
- 다대일 관계에서 1의 pk는 다의 fk가 된다. 다가 연관관계의 주인이 된다.


## 양방향 연관관계와 연관관계의 주인
- db테이블에는 무조건 양방향임
- 객체지향에서는 단방향 , 양방향이 가능함.
- 다대일 관계에서 일의 엔티티에 @OneToMany(mappedBy = "다의 매핑된 변수명")
    - 객체와 연관관계의 차이 - 양방향으로 참조하려면 단방향을 두개 만들어야한다.
        - 객체  1. 멤버 -> 팀 2. 팀 -> 멤버 (단방향이 두개일뿐)
        - 테이블 멤버 <-> 팀
    - **둘 중 하나로 외래 키를 관리해야 한다!!!!!!!**
        - 연관 관계의 주인
        - 양방향 매핑 규칙!
        - 연관관계의 주인만이 외래키 관리
        - 주인이 아닌쪽은 `읽기만` 사용
        - 주인은 joinColumn
        - 주인이 아니면 mappedBy로 주인을 지정한다.
        - 반대편은 조회만 가능하다!!!
        - **주인은 테이블에 외래키가 있는 곳으로 정한다**
        - 그렇지 않으면 수정할 때 다른쪽 테이블에 수정 쿼리가 나간다.
- 다대일 관계에서 다의 엔티티에 @ManyToOne @JoinColumn(name = "일의pk")

## 단방향 관계만 정리하면 기본적인 매핑은 완료
- 혹시 필요하다면 양방향을 고려하는 것
- **연관관계의 주인 쪽에 데이터를 저장해줘야 디비에 정확하게 값이 들어간다**
- 주인이 아닌 곳에 저장해봐야 디비에 값이 저장되지 않는다!
- changeTeam() 등의 편의성 메소드 사용을 권장한다.
- toString, Lombok, Json 라이브러리에서 무한루프에 빠질 가능성이 있다.
- **실무에서는 절대 컨트롤러단에서 entity를 반환하지 않는다.**

## 단방향 양방향
- 테이블
    - 외래 키 하나로 조인가능
    - 사실 방향 x
- 객체
    - 참조형 필드가 있어야 참조가능
    - 단방향이 두개가 있을뿐
## 다중성
1. 다대일 @ManyToOne -> 권장
    - 테이블의 외래키가 있는 곳이 연관관계의 주인이 된다.
    - 다대일은 mappedby 가 없고 주인이 되어야 한다.
2. 일대다 @OneToMany 권장 x
    - 일의 연관관계의 주인
    - 외래키관리를 One에서 하면 쿼리가 나갈때 다의 테이블은 건드려서 별로다. 업데이트 쿼리가 더 나감
    - 실무에서 테이블이 너무 많기 때문에 확인하기가 너무 힘듬
    - joinColumn이 없으면 조인테이블이 생성됨
3. 일대일 @OneToOne
    - 외래키를 어디에 넣어도 상관없음
    - 외래키에 데이터베이스 유니크 제약조건이 추가되어야함
    - 반대편은 mapped by
4. 다대다 @ManyToMany -> 실무에서 사용 x
    - 연결 테이블을 추가해서 일대다 , 다대일 관계로 풀어내야함
    - 객체는 다대다 관계가 가능함
    - 연결 테이블을 @Entity로 승격
    - member -< memberProduct >- product 관계로 풀어내야함
    - 실무에서는 그냥 테이블에 왠만하면 generate value로 id 값을 잡아주는거 권장
    
## @MappedSuperClass
- 동일한 속성이 있을 때 사용
- 상속관계 매핑 X / Entity X
- 추상클래스로 권장
- 등록일 등록자 수정일 수정자 등등
- jpa에서는 도메인 상속받을때 @Entity or @MappedSuperClass가 있어야함

## 상속
- data가 커지면 커질수록 상속을 사용하기가 힘들어질수도있음


## proxy와 연관관계

### proxy
- em.getReference() -> 사용 시 실제 사용시 jpa가 쿼리를 실행함 - proxy객체로 리턴해줌
- getName -> memberProxy -> 영속성 컨텍스트에 초기화 요청 -> DB -> Member 객체 생성 -> 프록시가 다시 조회
- 프록시 객체는 처음 사용할 때 한번만 초기화됨
- 프록시 객체가 실제 엔티티로 바뀌는게 아니고 target이 생기고 접근이 가능함.
- 타입 체크시 == 이 아닌 instance of 로 비교해야함.
- 영속성 컨텍스트에 있으면 getReference()는 진짜 객체를 반환해줌 -> 1차 캐시에 있으면 

## 고급매핑 - 상속관계 매핑
- 관계형 데이터베이스는 상속 관계가 없다.
- 객체의 상속관계 구조와 db 슈퍼타입 서브타입 관계를 매핑한다.
- 논리모델 to 물리 모델 전략
    - 조인 전략 : 슈퍼타입에 기본정보 insert는 두번 select는 조인
        - 비즈니스적으로 중요하고 확장가능성이 많으면 좋음
        - 정규화
        - 외래키 참조 무결성 제약조건 활용가능 다른 테이블에서도 볼 수 있음
        - 저장공간 효율성 높다
        - 단점 : 조회시 조인 사용
        - insert시 쿼리가 2번 나감
    - 단일 테이블전략 : 논리모델을 그냥 한 테이블로 합쳐버림
        - Dtype 필수
        - 성능상 이점이 있음, 단순함
        - 조회 성능이 빠름, 조회 쿼리가 단순함
        - 자식 클래스의 null허용이 필요함
        - 테이블이 커지면 조회 성능이 느려질 수도 있다.
        - 단순할 경우 단일테이블도 좋음
    - 각각 테이블에 다 가지고 있는 전략
        - 쓰면 안됨
        - 쿼리가 너무 복잡함 조회 성능이 느림
- jpa 기본 전략은 단일 테이블전략 하지만 조인 전략이 정석적
- 어떤 전략이더라도 DTYPE은 만들자.



- 오후 집에서한거
- 먼저 프록시로 가져오면 그 뒤에 find를 하더라도 프록시로 반환함
- 영속성 컨텍스트에 도움을 받을 수 없는 준영속 상태일때 레퍼런스는 예외를 터트림
- 실무에서 트랜잭션 종료후에 레퍼런스 조회하면 무조건 터짐
```java
Member refMember = em.getReference(~);
//준영속 상태로 변경햇을때
em.close(); or em.detach(refMember);
refMember.getName();
//LazyInitializationException 을 발생시킴
```
- proxy 초기화 여부  emf.gerPersistenceUnitUtil().isLoaded()
- 프록시 강제 초기화 : org.hibernate.Hibernate.initialize(entity);
- jpa표준에는 초기화기능이 없음.

## 지연로딩
- 비즈니스 로직에 따라 같이 조회할지 따로 조회할지 알아본 뒤 지연로딩과 즉시로딩을 정해야함 ( em.find )
- 지연로딩 : Lazy   즉시로딩 : Eager
- 지연로딩 시 가져온 것은 프록시 객체
- 즉시로딩은 진짜 객체로 반환해줌
## 실무 사용
- 실무에서는 지연로딩만 사용
- 즉시로딩시 전혀 예상하지 못한 쿼리가 나감(join 테이블이 너무 많아짐)
- jpql에서 N+1 의 문제가 생김
    - sql에서 가져온 뒤에 eager된 테이블을 또 조회함.
    - member - team 관계에서 team이 n개면 n개의 팀 조회 쿼리가 나감
- 기본적으로 Lazy로 세팅
    1. jpal에서 fetchjoin을 통해 동적으로 다 가져옴
    2. batch size
    3. 엔티티 그래프
- @ManyToOne , @OneToOne  -> default EAGER
- @OneToMany -> default LAZY

## 영속성 전이
- 특정 엔티티를 영속상태로 만들 때 연관된 엔티티도 함께 영속
- 부모 엔티티 저장할 때 자식 엔티티도 함께 저장
- 영속성 전이와 연관관게 매핑과는 관련 없음
- 하나의 부모가 자식들을 관리할때만 사용할때, Life Cycle이 거의 유사할때 ex) 게시판 - 첨부파일

## 고아 객체
- orphanRemoval 부모 객체에서 연관관계가 끊어지면 삭제한다
- 영속성 전이가 가능한 곳에서만 사용하기
- cascade.all + orphanRemovel=true -> 부모가 자식의 생명주기를 관리함

## 값타입
- 엔티티 타입
    - @Entity로 정의하는 객체
    - 엔티티의 값이 변해도 식별자로 인식가능
- 값 타입
    - int, Integer , String 처럼 단순히 값으로 사용
    - 식별자가 없고 값만 있음
### 기본값 타입
    - 자바 기본 타입(int, double) primitive type 은 공유 안됨 call by value
    - 래퍼 클래스 (Integer, Long) 레퍼런스이지만 공유 불가~
    - 생명주기가 엔티티에 의존 -> 회원 삭제시 이름 나이 필드도 삭제
    - 값 타입은 공유하면 안됨. side effect
### 임베디드 타입 -> 결국 엔티티의 값일뿐이다.
    - 복합 값 타입(좌표)
    - 새로운 값 타입을 직접 정의가능
    - 기본 값 타입을 모아서 만들어서 복합 값 타입
    - 엔티티와 매핑하는 테이블은 같다!!
    - @Embeddable 값 타입을 정의하는 곳(address)
    - @Embedded 값 타임을 사용하는 곳 (member)
    - 객체와 테이블을 더욱더 세밀하게 매핑이 가능하다.
    - 중복된 임베디드 타입은 @AttributeOverrides(value = AttributeOverride)를 통해 디비타입을 따로 매핑한다.
### 값타입과 불변객체
    - 임베디드 타입같은 값 타입을 여러 엔티티에서 공유하면 위험함 ( 메모리 주소를 가지고 있기 때문에)
    - 값 타입의 인스턴스를 공유하면 위험 -> 복사해서 사용해야함.
    - 객체 타입은 참조 문제를 해결할 수 없음
        - 객체 타입을 수정할 수 없게 만들면 부작용 원천 차단
        - 생성자로만 값을 설정할 수 있게 ( setter 를 private이거나 삭제)
        - 불변 객체로 만든다.
        - 수정할때 다시 생성자를 통해 만들어서 바꾼다.
### 값 타입의 비교
    - 인스턴스가 달라도 그 안에 값이 같으면 같은 것으로 봐야함.
    1. 동일성 비교
        - 인스턴스의 참조 값을 비교 == 사용
    2. 동등성 비교
        - 인스턴스의 값 비교 .equals()
        - 모든 필드를 다 비교해야한다.
        - Equals 오버라이드 할때는 왠만하면 인텔리제이가 해주는거로 해라
### 컬렉션 값 타입
    - list
    - @ElementCollection , @CollectionTable(name = "tablename", joinColumns = @JoinColumn(name = "joinColumn"))
    - 컬렉션은 한 테이블에 넣을 수 는 없다 ( 다대일 구조로 풀어내야한다)
    - 컬렉션 값 타입은 생명주기가 부모에 소속된다. cascade.all + orphanRemovel = true 처럼 동작한다.
    - 컬렉션을 찾아올때는 기본적으로 지연로딩이다.
    - 엔티티와 다르게 식별자 개념이 없기 때문에 값을 변경하면 추적이 어렵다.
    - 값 타입 컬렉션에 변경이 발생하면 -> 주인 엔티티와 관련된 모든 데이터 삭제 후 현재값을 모두 저장한다.
    - **복잡한 방법이 많기 때문에 실무에서는 컬렉션으로 들어갈 친구는 entity로 승격한다.**
    - 위의 경우에 @OneToMany(cascade = ALL, orphanRemoval = true) 로 선언한다.
    - 값타입을 사용할때는 아주 단순할때 List<String> 정도로 풀 수 있을때.


## 객체지향 쿼리언어

### JPQL
- 객체지향sql
- 가장 단순한 조회방법
    - EntityManager.find()
    - 객체 그래프 탐색 a.get().getb()
- 검색을 할떄도 엔티티 객체를 대상으로 검색 ( 테이블을 검색하는 것이 아니다. )
- sql을 추상화
- select / from / where /group by / having /join 안심표준 사용 가능

### JPQL 기본문법
- jpql은 sql을 추상화해서 특정 데이터베이스 sql에 의존하지 않는다.
- jpql은 결국 sql로 나온다.
- 문법은 sql과 비슷함.
    - `select m from Member as m where m.age > 18`
    - from 절에서는 엔티티의 이름을 사용함 
    - 별칭은 필수 as 생략가능
    - 안심 sql 은 사용가능 count, max, avg, sum , group by , order by 등등 사용가능
- TypeQuery, Query
    - TypeQuery<Member> : return 값이 정확할때
    - Query : 반환 타입값이 명확하지 않을때
- 결과 반환
    - getResultList() -> 한개 이상일 경우 리스트 반환
    - getSingleResult(); 결과가 정확히 하나
        - 두개거나 없으면 exception터짐
        - Spring Data Jpa -> 결과가 없으면 Null or Optional로 반환
- 파라미터 바인딩
    - 포지션으로 쓰지말고 이름 입력해서 쓰자

### projection
    - select m from Memmber m -> 엔티티 프로젝션
        - 영속성 컨텍스트에서 관리해줌
    - select m.team from Member m -> 엔티티 프로젝션
        - result를 Team으로 받아야함
        - 실행하면 join query가 나감
        - 조인 쿼리가 나가지 않게 왠만하면 sql과 비슷하게 씁시다.
    - select m.address from Member m -> 임베디드 타입 프로젝션
    - select m.username, m.name from Member m -> 스칼라 타입
        - query Type 조회 : em.createQuery() -> Object[]로 조회
        - List<Object[]> result = em.createQuery()
        - new 명령어 조회 dto로 바로 조회
```java
List<MeberDTO> result = em.createQuery("select new jpql.MeberDTO(m.name,m.age) from Member m")
        .getResultList();
    MeberDTO meberDTO = result.get(0);
    System.out.println("meberDTO.getName() = " + meberDTO.getName());
    System.out.println("meberDTO.getAge() = " + meberDTO.getAge());    
```
    - disinct 사용 가능

### Paging
- JPA는 페이징을 다음 두 API로 추상화
    - setFirstResult 조회 시작 위치
    - setMaxResult 조회할 데이터 수
    - 매우 추상화해서 쉽다 디비 방언들을 dialect을 보고 다 적용해줌 oracle, mysql 등등
    ```java
            List<Member> result = em.createQuery("select m from Member m order by m.age", Member.class)
                    .setFirstResult(1)
                    .setMaxResults(10)
                    .getResultList();
            for (Member member1 : result) {
                System.out.println("member1 = " + member1);
            }
    ```

### Join
- 객체 스타일로 조인이 나감
    - 내부조인 select m from Member m join m.team t
    - 외부조인 sleect m from Member m LEFT JOIN m.team t
- 2.1 부터 on 사용가능
- 연관 관계 없는 엔티티 외부 조인 가능
    - 연관 관계가 없어도 엔티티의 조인이 가능

### sub query
- EXISTS 함수 지원
- all | any | some | in
- jpa 표준 스팩은 where having 절에서만 서브쿼리 사용가능
- but 하이버네이트에서 select 절에서도 사용가능
- **From절의 서브 쿼리는 jpql에서 불가능**
    - join으로 풀어줘야 함
    - 애플리케이션에서 조작하는 형식으로 할 수도 있음
    - native query

### jpql의 타입 표현
- 문자 : '
- 숫자 : 10L, 10D, 10F
- boolean : true
- enum : 자바 패키지까지 적어야함  jpabook.MemberType.Admin
    - querydsl 에서 다 커버 가능
- 엔티티타입 : Type(m) - > Dtype 를 적어주는거임  상속관계에서

### 조건식
- 기본 CASE 식
    ```java
    String query = 
    "select " +
    "case when m.age <= 10 then '학생요금'" +
    "when m.age >= 60 then '일반요금'" +
    "else '테스트'" +
    "end " +
    "from Member m";
    List<String> resultList = em.createQuery(query, String.class).getResultList();
    for (String s : resultList) {
        System.out.println("s = " + s);
    }
    ```
- 단순 CASE 식
- COALESCE : null 이면 나온다
- NULLIF : 두 값이 같으면 null, 아니면 첫번째 값 변환
```sql
    select nullif(m.username, '관리자') as username
    from Member m
```

### jpql 함수
- 기본함수
    - concat
    - substring
    - trim
    - lower upper
    - length
    - lcoate
    - abs, sqrt, mod
    - size, index
        - size onetomany 크기의 사이즈 돌려줌
        - index 
- 사용자 정의 함수
    - 사용하는 db 방언을 상속받고 사용자 정의 함수를 등록한다
    - select function('group_dept')
    - 기본적으로 dialect를 통해 함수가 미리 등록되어있음
    - diarect를 상속받아서 코드를 짜고 써야함


### JPA Criteria
- 자바코드 기반 쿼리 생성
- 자바코드 기반이기때문에 오타에 대한 걱정이 적음
- 하지만 sql처럼 눈으로 안보임
- 실무에서 잘 안씀
- 차라리 QueryDSL을 쓴다.


### QueryDSL
- 자바코드 기반
- 단순하고 쉬움
- 실무에서 좋은듯
- www.querydsl.com
- jpql을 먼저 알아야함

### JDBC
- 영속성 컨텍스트와 상관 없기 때문에 적당한 타이밍에 flush() 해줘야함.

