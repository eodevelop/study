# [10분 테코톡] 🌕제이의 Spring AOP
 
### 영상 요약
- AOP 에 대하여
    - 서비스에서 필요한 내용이 아닌 부가기능을 추가하는 인프라 로직을 사용할 때 주로 사용
        - 인프라 로직
            - 애플리케이션 전 영역에서 나타날 수 있음
            - 중복코드를 만들어 낼 가능성 떄문에 유지보수가 힘들어짐
            - 비즈니스 로직과 함께 있으면 비즈니스 로직을 이해하기 어려워짐
    - AOP란 Aspect Oriented Programming의 약자로 관점 지향 프로그래밍을 의미
- AOP 용어
    - Target
        - 어떤 대상에 부가 기능을 부여할 것인가
    - Aspect
        - 어떤 부가 기능? Before, After, Around, AfterReturning, AfterThrowing
    - Join point
        - 어디에 적용할 것인가? 메서드, 필드, 객체, 생성자 등
    - Point cut
        - 실제 advice가 적용될 지점, Spring AOP 에서는 advice가 적용될 메서드를 지정
  
### 느낀점
- 영상 중간에 중단. 김영한 강의와, 프록시 패턴에 대해 디자인 패턴책을 읽기로 결정
  
### 영상 링크
- [유튜브](https://www.youtube.com/watch?v=Hm0w_9ngDpM)
  