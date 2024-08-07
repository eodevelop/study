임시. 트랜잭션 격리 수준
    - Read Uncommiteed : 커밋 되지 않은 데이터 읽기 가능 
    - Read Committed : 커밋된 데이터만 읽을 수 읽기 가능
    - Repeatable Read : 트랜잭션 동안 읽은 데이터가 변경 되지 않도록 보장
    - Serializable : 가장 높은 격리수준. 트랜잭션을 순차적으로 실행하는 것을 보장.

1. 스프링 프레임워크와 스프링 부트의 차이
    - 스프링 프레임워크 : 스프링 프레임워크는 자바 엔터프라이즈 개발을 위한 오픈 소스 프레임워크로, DI, AOP, MVC 등의 기능을 제공한다.
    - 스프링 부트 : 스프링 부트는 스프링 프레임워크를 사용하여 개발할 때, 설정을 간소화하고 빠르게 개발할 수 있도록 도와주는 프레임워크로, 내장형 서버를 제공한다.

2. 스프링 DI 와 IoC
    - DI : Dependency Injection, 의존성 주입. 객체 간의 의존성을 외부에서 주입하는 것
        > 장점 : 객체 간의 결합도를 낮추어 유지보수성을 높이고, 테스트하기 쉽다.
    - IoC : Inversion of Control, 제어의 역전. 객체의 생성과 생명주기 관리를 프레임워크가 담당하는 것
        > 장점 : 객체의 생명주기를 외부에서 관리하기 때문에 객체 간의 결합도를 낮출 수 있다.

3. JPA의 N+1 문제와 해결법
    - N+1 문제 : 연관관계 매핑시 발생하는 문제로, 연관관계 매핑된 엔티티를 조회할 때, 연관관계 매핑된 엔티티를 조회하는 쿼리가 N+1번 발생하는 문제
    - 해결법 : fetch join, 엔티티 그래프, @BatchSize, @EntityGraph, @QueryHint, @NamedEntityGraph

4. 스프링 AOP 에 대해서 간략히 설명하시오
    - AOP : Aspect Oriented Programming, 관점 지향 프로그래밍. 핵심 비즈니스 로직과 공통 기능을 분리하여 관리하는 방법
    - 간단한 예시 : 로깅, 트랜잭션, 보안, 예외처리 등의 공통 기능을 별도의 클래스로 분리하여 관리하고, 핵심 비즈니스 로직에 적용하는 방법

5. 스프링 MVC의 동작 방식
    - 클라이언트의 요청을 DispatcherServlet이 받아서 HandlerMapping을 통해 적절한 컨트롤러를 찾고, HandlerAdapter를 통해 컨트롤러를 실행한다.

6. @Controller 와 @RestController 의 차이
    - @RestController 를 사용하면 @ResponseBody를 생략할 수 있다.

7. filter와 interceptor의 차이
    - filter : 서블릿의 앞단에서 요청과 응답을 가로채는 역할을 한다.
    - interceptor : 핸들러를 실행하기 전, 후에 요청과 응답을 가로채는 역할을 한다.