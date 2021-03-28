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